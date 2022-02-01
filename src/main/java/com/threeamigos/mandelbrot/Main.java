package com.threeamigos.mandelbrot;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.WindowConstants;

import com.threeamigos.mandelbrot.implementations.service.ImagePersisterServiceImpl;
import com.threeamigos.mandelbrot.implementations.service.ImageProducerServiceFactoryImpl;
import com.threeamigos.mandelbrot.implementations.service.MandelbrotServiceFactoryImpl;
import com.threeamigos.mandelbrot.implementations.service.PointsImpl;
import com.threeamigos.mandelbrot.implementations.service.PointsOfInterestServiceImpl;
import com.threeamigos.mandelbrot.implementations.service.SnapshotServiceImpl;
import com.threeamigos.mandelbrot.implementations.service.scheduler.PrioritizedRunnableLIFOComparator;
import com.threeamigos.mandelbrot.implementations.service.scheduler.SchedulerServiceImpl;
import com.threeamigos.mandelbrot.implementations.ui.CalculationParametersRequesterImpl;
import com.threeamigos.mandelbrot.implementations.ui.WindowDecoratorHelpFragmentImpl;
import com.threeamigos.mandelbrot.implementations.ui.WindowDecoratorInfoFragmentImpl;
import com.threeamigos.mandelbrot.implementations.ui.WindowDecoratorPointOfInterestNameFragmentImpl;
import com.threeamigos.mandelbrot.implementations.ui.WindowDecoratorServiceImpl;
import com.threeamigos.mandelbrot.implementations.ui.WindowDecoratorSnapshotServiceFragmentImpl;
import com.threeamigos.mandelbrot.implementations.ui.ZoomBoxServiceImpl;
import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;
import com.threeamigos.mandelbrot.interfaces.service.ImagePersisterService;
import com.threeamigos.mandelbrot.interfaces.service.ImageProducerServiceFactory;
import com.threeamigos.mandelbrot.interfaces.service.MandelbrotService;
import com.threeamigos.mandelbrot.interfaces.service.MandelbrotServiceFactory;
import com.threeamigos.mandelbrot.interfaces.service.CalculationType;
import com.threeamigos.mandelbrot.interfaces.service.Points;
import com.threeamigos.mandelbrot.interfaces.service.PointsOfInterestService;
import com.threeamigos.mandelbrot.interfaces.service.SchedulerService;
import com.threeamigos.mandelbrot.interfaces.service.SnapshotService;
import com.threeamigos.mandelbrot.interfaces.ui.CalculationParametersRequester;
import com.threeamigos.mandelbrot.interfaces.ui.WindowDecoratorService;
import com.threeamigos.mandelbrot.interfaces.ui.ZoomBoxService;

public class Main {

	private CalculationParametersRequester calculationParametersRequester;
	private CalculationParameters calculationParameters;
	private Resolution resolution;
	private Points points;
	private SchedulerService schedulerService;
	private MandelbrotServiceFactory mandelbrotServiceFactory;
	private ImageProducerServiceFactory imageProducerServiceFactory;
	private ImagePersisterService imagePersisterService;
	private PointsOfInterestService pointsOfInterestService;
	private SnapshotService snapshotService;
	private MandelbrotService mandelbrotService;
	private WindowDecoratorService windowDecoratorService;
	private ZoomBoxService zoomBox;

	public Main() {

		calculationParametersRequester = new CalculationParametersRequesterImpl();

		calculationParameters = calculationParametersRequester.getCalculationParameters(null);
		if (calculationParameters == null) {
			return;
		}

		resolution = calculationParameters.getResolution();

		points = new PointsImpl(resolution);

		mandelbrotServiceFactory = new MandelbrotServiceFactoryImpl();

		schedulerService = new SchedulerServiceImpl(new PrioritizedRunnableLIFOComparator());

		imageProducerServiceFactory = new ImageProducerServiceFactoryImpl();

		imagePersisterService = new ImagePersisterServiceImpl();

		pointsOfInterestService = new PointsOfInterestServiceImpl();

		snapshotService = new SnapshotServiceImpl(calculationParametersRequester, mandelbrotServiceFactory,
				imageProducerServiceFactory, imagePersisterService, schedulerService);

		mandelbrotService = mandelbrotServiceFactory.createInstance(calculationParameters, schedulerService,
				CalculationType.FOREGROUND);

		windowDecoratorService = new WindowDecoratorServiceImpl(
				new WindowDecoratorInfoFragmentImpl(resolution, mandelbrotService, points),
				new WindowDecoratorHelpFragmentImpl(resolution, pointsOfInterestService),
				new WindowDecoratorPointOfInterestNameFragmentImpl(resolution, pointsOfInterestService),
				new WindowDecoratorSnapshotServiceFragmentImpl(resolution, snapshotService));

		zoomBox = new ZoomBoxServiceImpl(points);

		MandelbrotCanvas mandelbrotCanvas = new MandelbrotCanvas(mandelbrotService, pointsOfInterestService,
				imageProducerServiceFactory, snapshotService, points, calculationParameters, zoomBox,
				windowDecoratorService);

		mandelbrotService.addPropertyChangeListener(mandelbrotCanvas);
		snapshotService.addPropertyChangeListener(mandelbrotCanvas);

		imagePersisterService.setMessageNotifier(mandelbrotCanvas);
		pointsOfInterestService.setMessageNotifier(mandelbrotCanvas);

		pointsOfInterestService.loadPointsOfInterest();

		JFrame jframe = prepareFrame(mandelbrotCanvas);

		JMenuBar menuBar = prepareMenu(jframe);

		mandelbrotCanvas.addMenus(menuBar);

		jframe.setVisible(true);

		mandelbrotCanvas.startCalculationThread();

	}

	private JFrame prepareFrame(MandelbrotCanvas mandelbrotCanvas) {

		JFrame jframe = new JFrame("3AM Mandelbrot");
		jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		jframe.setLayout(null);
		Container c = jframe.getContentPane();
		c.setPreferredSize(new Dimension(mandelbrotCanvas.getWidth(), mandelbrotCanvas.getHeight()));
		jframe.add(mandelbrotCanvas);
		mandelbrotCanvas.setLocation(0, 0);

		jframe.pack();
		jframe.setResizable(false);

		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();

		jframe.setLocation((screenDimension.width - jframe.getSize().width) / 2,
				(screenDimension.height - jframe.getSize().height) / 2);

		return jframe;
	}

	public JMenuBar prepareMenu(JFrame jframe) {
		JMenuBar menuBar = new JMenuBar();
		jframe.setJMenuBar(menuBar);
		return menuBar;
	}

	public static void main(String[] args) {
		new Main();
	}

}
