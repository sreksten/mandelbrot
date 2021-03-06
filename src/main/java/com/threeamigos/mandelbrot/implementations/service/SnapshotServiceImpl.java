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

import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;
import com.threeamigos.mandelbrot.interfaces.service.CalculationType;
import com.threeamigos.mandelbrot.interfaces.service.FractalService;
import com.threeamigos.mandelbrot.interfaces.service.FractalServiceFactory;
import com.threeamigos.mandelbrot.interfaces.service.ImagePersisterService;
import com.threeamigos.mandelbrot.interfaces.service.ImageProducerService;
import com.threeamigos.mandelbrot.interfaces.service.ImageProducerServiceFactory;
import com.threeamigos.mandelbrot.interfaces.service.Points;
import com.threeamigos.mandelbrot.interfaces.service.SnapshotService;
import com.threeamigos.mandelbrot.interfaces.ui.ParametersRequester;
import com.threeamigos.mandelbrot.interfaces.ui.Resolution;

public class SnapshotServiceImpl implements SnapshotService, Runnable {

	private ParametersRequester parametersRequester;
	private FractalServiceFactory fractalServiceFactory;
	private ImageProducerServiceFactory imageProducerServiceFactory;
	private ImagePersisterService imagePersisterService;

	private final PropertyChangeSupport propertyChangeSupport;
	private final Queue<SnapshotJob> queuedSnapshotJobs;

	private final JFileChooser fileChooser;

	private AtomicBoolean running;
	private Thread queuedSnapshotsThread;

	private FractalService bkgCalculator;

	public SnapshotServiceImpl(ParametersRequester parametersRequester, FractalServiceFactory fractalServiceFactory,
			ImageProducerServiceFactory imageProducerServiceFactory, ImagePersisterService imageService) {
		this.parametersRequester = parametersRequester;
		this.fractalServiceFactory = fractalServiceFactory;
		this.imageProducerServiceFactory = imageProducerServiceFactory;
		this.imagePersisterService = imageService;

		this.propertyChangeSupport = new PropertyChangeSupport(this);
		this.queuedSnapshotJobs = new ConcurrentLinkedQueue<>();

		fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Select snapshot destination");
		fileChooser.setApproveButtonText("Save");
		fileChooser.setApproveButtonToolTipText("Saves the snapshot to the selected file");
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

		running = new AtomicBoolean(true);
		queuedSnapshotsThread = new Thread(null, this, "QueuedSnapshotsThread");
		queuedSnapshotsThread.setDaemon(true);
		queuedSnapshotsThread.start();
	}

	@Override
	public void saveSnapshot(Points points, int maxIterations, String colorModelName, Image bufferedImage,
			Component parentComponent) {

		if (!parametersRequester.requestParameters(true, maxIterations, parentComponent)) {
			return;
		}

		String filename = askFilename(parentComponent);
		if (filename == null) {
			return;
		}

		CalculationParameters bkgCalculationParameters = parametersRequester.getCalculationParameters();
		bkgCalculationParameters.setCalculationType(CalculationType.BACKGROUND);
		Resolution bkgResolution = parametersRequester.getResolution();

		if (hasSameResolution(bufferedImage, bkgResolution)) {
			saveImage(bufferedImage, filename);
		} else {
			Points newPoints = points.adaptToResolution(bkgResolution);
			queuedSnapshotJobs
					.add(new SnapshotJob(bkgCalculationParameters, bkgResolution, newPoints, colorModelName, filename));
			queuedSnapshotsThread.interrupt();
		}
	}

	private String askFilename(Component parentComponent) {
		String filename = new StringBuilder().append("3AM_Mandelbrot_")
				.append(new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())).append(".png").toString();

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

	private boolean hasSameResolution(Image image, Resolution resolution) {
		return resolution.getWidth() == image.getWidth(null) && resolution.getHeight() == image.getHeight(null);
	}

	private void saveImage(Image image, String filename) {
		imagePersisterService.saveImage(image, filename);
	}

	@Override
	public void run() {
		while (running.get()) {
			waitForScheduledSnapshot();
			while (!queuedSnapshotJobs.isEmpty()) {
				SnapshotJob snapshotJob = queuedSnapshotJobs.remove();
				CalculationParameters bkgParameters = snapshotJob.calculationParameters;
				bkgCalculator = fractalServiceFactory.createInstance();
				for (PropertyChangeListener propertyChangeListener : propertyChangeSupport
						.getPropertyChangeListeners()) {
					bkgCalculator.addPropertyChangeListener(propertyChangeListener);
				}
				bkgCalculator.calculate(snapshotJob.points, snapshotJob.resolution, snapshotJob.calculationParameters);
				ImageProducerService bkgImageProducer = imageProducerServiceFactory
						.createInstance(bkgParameters.getMaxIterations());
				bkgImageProducer.switchColorModel(snapshotJob.colorModelName);
				Image imageToSave = bkgImageProducer.produceImage(snapshotJob.points.getWidth(),
						snapshotJob.points.getHeight(), bkgCalculator.getIterations());
				saveImage(imageToSave, snapshotJob.filename);
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
		return queuedSnapshotJobs.size();
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
		final Resolution resolution;
		final Points points;
		final String colorModelName;
		final String filename;

		SnapshotJob(CalculationParameters calculationParameters, Resolution resolution, Points points,
				String colorModelName, String filename) {
			this.calculationParameters = calculationParameters;
			this.resolution = resolution;
			this.points = points.copy();
			this.colorModelName = colorModelName;
			this.filename = filename;
		}
	}
}
