package com.threeamigos.mandelbrot.implementations.service;

import java.awt.Component;
import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFileChooser;

import com.threeamigos.mandelbrot.Resolution;
import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;
import com.threeamigos.mandelbrot.interfaces.service.CalculationType;
import com.threeamigos.mandelbrot.interfaces.service.ImagePersisterService;
import com.threeamigos.mandelbrot.interfaces.service.ImageProducerService;
import com.threeamigos.mandelbrot.interfaces.service.ImageProducerServiceFactory;
import com.threeamigos.mandelbrot.interfaces.service.FractalService;
import com.threeamigos.mandelbrot.interfaces.service.FractalServiceFactory;
import com.threeamigos.mandelbrot.interfaces.service.Points;
import com.threeamigos.mandelbrot.interfaces.service.SchedulerService;
import com.threeamigos.mandelbrot.interfaces.service.SnapshotService;
import com.threeamigos.mandelbrot.interfaces.ui.CalculationParametersRequester;

public class SnapshotServiceImpl implements SnapshotService, Runnable {

	private final PropertyChangeSupport propertyChangeSupport;

	private CalculationParametersRequester calculationParametersRequester;
	private FractalServiceFactory mandelbrotServiceFactory;
	private ImageProducerServiceFactory imageProducerServiceFactory;
	private ImagePersisterService imagePersisterService;
	private SchedulerService schedulerService;

	private final JFileChooser fileChooser;

	private Queue<SnapshotJob> queuedSnapshots;
	private FractalService bkgCalculator;
	private Thread queuedSnapshotsThread;
	private AtomicBoolean running;

	public SnapshotServiceImpl(CalculationParametersRequester calculationParametersRequester,
			FractalServiceFactory mandelbrotServiceFactory, ImageProducerServiceFactory imageProducerServiceFactory,
			ImagePersisterService imageService, SchedulerService schedulerService) {
		this.calculationParametersRequester = calculationParametersRequester;
		this.mandelbrotServiceFactory = mandelbrotServiceFactory;
		this.imageProducerServiceFactory = imageProducerServiceFactory;
		this.imagePersisterService = imageService;
		this.schedulerService = schedulerService;
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		this.queuedSnapshots = new ConcurrentLinkedQueue<>();

		fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Select snapshot destination");
		fileChooser.setApproveButtonText("Save");
		fileChooser.setApproveButtonToolTipText("Saves the snapshot to the selected file");

		running = new AtomicBoolean(true);
		queuedSnapshotsThread = new Thread(null, this, "QueuedSnapshotsThread");
		queuedSnapshotsThread.setDaemon(true);
		queuedSnapshotsThread.start();
	}

	@Override
	public void saveSnapshot(Points points, int maxIterations, String colorModelName, Image bufferedImage,
			Component parentComponent) {

		CalculationParameters tempCalculationParameters = calculationParametersRequester.getCalculationParameters(true,
				maxIterations, parentComponent);
		if (tempCalculationParameters == null) {
			return;
		}

		String filename = askFilename(parentComponent);
		if (filename == null) {
			return;
		}

		if (hasSameResolution(bufferedImage, tempCalculationParameters)) {
			saveImage(bufferedImage, filename);
		} else {
			Points newPoints = points.adaptToResolution(tempCalculationParameters.getResolution());
			queuedSnapshots.add(new SnapshotJob(tempCalculationParameters, newPoints, colorModelName, filename));
			queuedSnapshotsThread.interrupt();
		}
	}

	private String askFilename(Component parentComponent) {
		String filename = new StringBuilder().append("3AM_Mandelbrot_")
				.append(new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())).append(".png").toString();

		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		fileChooser.setSelectedFile(new File(filename));
		int returnVal = fileChooser.showOpenDialog(parentComponent);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			try {
				return file.getCanonicalPath();
			} catch (IOException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return null;
	}

	private boolean hasSameResolution(Image image, CalculationParameters calculationParameters) {
		Resolution resolution = calculationParameters.getResolution();
		return resolution.getWidth() == image.getWidth(null) && resolution.getHeight() == image.getHeight(null);
	}

	private void saveImage(Image image, String filename) {
		imagePersisterService.saveImage(image, filename);
	}

	@Override
	public void run() {
		while (running.get()) {
			waitForScheduledSnapshot();
			while (!queuedSnapshots.isEmpty()) {
				SnapshotJob snapshot = queuedSnapshots.remove();
				CalculationParameters bkgParameters = snapshot.calculationParameters;
				bkgCalculator = mandelbrotServiceFactory.createInstance(bkgParameters, schedulerService,
						CalculationType.BACKGROUND);
				for (PropertyChangeListener propertyChangeListener : propertyChangeSupport
						.getPropertyChangeListeners()) {
					bkgCalculator.addPropertyChangeListener(propertyChangeListener);
				}
				bkgCalculator.calculate(snapshot.points);
				ImageProducerService bkgImageProducer = imageProducerServiceFactory
						.createInstance(bkgParameters.getMaxIterations());
				bkgImageProducer.switchColorModel(snapshot.colorModelName);
				Image imageToSave = bkgImageProducer.produceImage(snapshot.points.getWidth(),
						snapshot.points.getHeight(), bkgCalculator.getIterations());
				saveImage(imageToSave, snapshot.filename);
				bkgCalculator = null;
			}
		}
	}

	private void waitForScheduledSnapshot() {
		try {
			synchronized (this) {
				wait();
			}
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public int getQueuedSnapshots() {
		return queuedSnapshots.size();
	}

	@Override
	public Integer getCurrentSnapshotPercentage() {
		FractalService current = bkgCalculator;
		if (current != null) {
			return current.getPercentage();
		}
		return null;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		propertyChangeSupport.addPropertyChangeListener(pcl);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		propertyChangeSupport.removePropertyChangeListener(pcl);
	}

	private class SnapshotJob {

		final CalculationParameters calculationParameters;
		final Points points;
		final String colorModelName;
		final String filename;

		SnapshotJob(CalculationParameters calculationParameters, Points points, String colorModelName,
				String filename) {
			this.calculationParameters = calculationParameters;
			this.points = points.copy();
			this.colorModelName = colorModelName;
			this.filename = filename;
		}
	}
}
