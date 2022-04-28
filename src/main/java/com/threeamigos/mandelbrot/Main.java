package com.threeamigos.mandelbrot;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.WindowConstants;

import com.threeamigos.mandelbrot.implementations.service.FractalServiceFactoryImpl;
import com.threeamigos.mandelbrot.implementations.service.ImagePersisterServiceImpl;
import com.threeamigos.mandelbrot.implementations.service.ImageProducerServiceFactoryImpl;
import com.threeamigos.mandelbrot.implementations.service.PointsImpl;
import com.threeamigos.mandelbrot.implementations.service.PointsOfInterestServiceImpl;
import com.threeamigos.mandelbrot.implementations.service.SnapshotServiceImpl;
import com.threeamigos.mandelbrot.implementations.service.imageproducer.BlackWhiteColorModelImageProducer;
import com.threeamigos.mandelbrot.implementations.service.scheduler.PrioritizedRunnableLIFOComparator;
import com.threeamigos.mandelbrot.implementations.service.scheduler.SchedulerServiceImpl;
import com.threeamigos.mandelbrot.implementations.ui.AboutWindowImpl;
import com.threeamigos.mandelbrot.implementations.ui.CalculationParametersImpl;
import com.threeamigos.mandelbrot.implementations.ui.CustomResolution;
import com.threeamigos.mandelbrot.implementations.ui.FontServiceImpl;
import com.threeamigos.mandelbrot.implementations.ui.JuliaBoundariesServiceImpl;
import com.threeamigos.mandelbrot.implementations.ui.ParametersRequesterImpl;
import com.threeamigos.mandelbrot.implementations.ui.WindowDecoratorHelpFragmentImpl;
import com.threeamigos.mandelbrot.implementations.ui.WindowDecoratorInfoFragmentImpl;
import com.threeamigos.mandelbrot.implementations.ui.WindowDecoratorPointOfInterestNameFragmentImpl;
import com.threeamigos.mandelbrot.implementations.ui.WindowDecoratorServiceImpl;
import com.threeamigos.mandelbrot.implementations.ui.WindowDecoratorSnapshotServiceFragmentImpl;
import com.threeamigos.mandelbrot.implementations.ui.ZoomBoxServiceImpl;
import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;
import com.threeamigos.mandelbrot.interfaces.service.FractalService;
import com.threeamigos.mandelbrot.interfaces.service.FractalServiceFactory;
import com.threeamigos.mandelbrot.interfaces.service.ImagePersisterService;
import com.threeamigos.mandelbrot.interfaces.service.ImageProducerServiceFactory;
import com.threeamigos.mandelbrot.interfaces.service.Points;
import com.threeamigos.mandelbrot.interfaces.service.PointsOfInterestService;
import com.threeamigos.mandelbrot.interfaces.service.SchedulerService;
import com.threeamigos.mandelbrot.interfaces.service.SnapshotService;
import com.threeamigos.mandelbrot.interfaces.ui.FontService;
import com.threeamigos.mandelbrot.interfaces.ui.ParametersRequester;
import com.threeamigos.mandelbrot.interfaces.ui.RenderableConsumer;
import com.threeamigos.mandelbrot.interfaces.ui.Resolution;
import com.threeamigos.mandelbrot.interfaces.ui.WindowDecoratorService;

public class Main {

	public Main() {

		ParametersRequester parametersRequester = new ParametersRequesterImpl();

		if (!parametersRequester.requestParameters()) {
			// User cancelled the operation
			return;
		}

		Resolution resolution = parametersRequester.getResolution();

		CalculationParameters calculationParameters = parametersRequester.getCalculationParameters();

		Points points = new PointsImpl(resolution);

		SchedulerService schedulerService = new SchedulerServiceImpl(new PrioritizedRunnableLIFOComparator());

		FractalServiceFactory fractalServiceFactory = new FractalServiceFactoryImpl(schedulerService);

		ImageProducerServiceFactory imageProducerServiceFactory = new ImageProducerServiceFactoryImpl();

		ImagePersisterService imagePersisterService = new ImagePersisterServiceImpl();

		PointsOfInterestService pointsOfInterestService = new PointsOfInterestServiceImpl();

		SnapshotService snapshotService = new SnapshotServiceImpl(parametersRequester, fractalServiceFactory,
				imageProducerServiceFactory, imagePersisterService);

		FractalService fractalService = fractalServiceFactory.createInstance();

		FontService fontService = new FontServiceImpl();

		WindowDecoratorService windowDecoratorService = new WindowDecoratorServiceImpl(
				new WindowDecoratorInfoFragmentImpl(resolution, fontService, fractalService, calculationParameters,
						points),
				new WindowDecoratorHelpFragmentImpl(resolution, fontService, pointsOfInterestService),
				new WindowDecoratorPointOfInterestNameFragmentImpl(resolution, fontService, pointsOfInterestService),
				new WindowDecoratorSnapshotServiceFragmentImpl(resolution, fontService, snapshotService));

		FractalCanvas fractalCanvas = new FractalCanvas(fractalService, pointsOfInterestService,
				imageProducerServiceFactory, snapshotService, points, windowDecoratorService, new AboutWindowImpl(),
				resolution, calculationParameters);

		fractalCanvas.addRenderableConsumer(new ZoomBoxServiceImpl(points));
		fractalCanvas.addRenderableConsumer(
				createJuliaBoundariesService(points, fontService, pointsOfInterestService, fractalService));

		points.addPropertyChangeListener(fractalCanvas);
		fractalService.addPropertyChangeListener(fractalCanvas);
		snapshotService.addPropertyChangeListener(fractalCanvas);

		imagePersisterService.setMessageNotifier(fractalCanvas);
		pointsOfInterestService.setMessageNotifier(fractalCanvas);

		pointsOfInterestService.loadPointsOfInterest();

		JFrame jframe = prepareFrame(fractalCanvas);

		fractalCanvas.addMenus(prepareMenu(jframe));

		jframe.setVisible(true);

		fractalCanvas.startCalculationThread();

	}

	private RenderableConsumer createJuliaBoundariesService(Points points, FontService fontService,
			PointsOfInterestService pointsOfInterestService, FractalService fractalService) {

		JuliaBoundariesServiceImpl juliaBoundariesService = new JuliaBoundariesServiceImpl(points, fontService,
				pointsOfInterestService);

		final int diameter = juliaBoundariesService.getDiameter();
		Resolution targetResolution = new CustomResolution(diameter, diameter);

		Points targetPoints = new PointsImpl(targetResolution);
		targetPoints.setMinX(-2.0d);
		targetPoints.setMaxX(2.0d);
		targetPoints.setMinY(-2.0d);
		targetPoints.setMaxY(2.0d);
		points.adaptToResolution(targetResolution);

		final int maxIterations = 32;

		fractalService.calculate(targetPoints, targetResolution,
				new CalculationParametersImpl(Runtime.getRuntime().availableProcessors(), maxIterations));

		int[] iterations = fractalService.getIterations();
		for (int i = 0; i < iterations.length; i++) {
			iterations[i] = (iterations[i] == maxIterations ? 1 : 0);
		}

		juliaBoundariesService
				.setImage(new BlackWhiteColorModelImageProducer().produceImage(diameter, diameter, iterations));

		return juliaBoundariesService;
	}

	private JFrame prepareFrame(FractalCanvas mandelbrotCanvas) {

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

	private JMenuBar prepareMenu(JFrame jframe) {
		JMenuBar menuBar = new JMenuBar();
		jframe.setJMenuBar(menuBar);
		return menuBar;
	}

	public static void main(String[] args) {
		new Main();
	}

}
