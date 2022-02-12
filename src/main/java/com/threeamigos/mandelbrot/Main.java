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
import com.threeamigos.mandelbrot.implementations.service.scheduler.PrioritizedRunnableLIFOComparator;
import com.threeamigos.mandelbrot.implementations.service.scheduler.SchedulerServiceImpl;
import com.threeamigos.mandelbrot.implementations.ui.AboutWindowImpl;
import com.threeamigos.mandelbrot.implementations.ui.CalculationParametersRequesterImpl;
import com.threeamigos.mandelbrot.implementations.ui.FontServiceImpl;
import com.threeamigos.mandelbrot.implementations.ui.JuliaBoundariesServiceImpl;
import com.threeamigos.mandelbrot.implementations.ui.WindowDecoratorHelpFragmentImpl;
import com.threeamigos.mandelbrot.implementations.ui.WindowDecoratorInfoFragmentImpl;
import com.threeamigos.mandelbrot.implementations.ui.WindowDecoratorPointOfInterestNameFragmentImpl;
import com.threeamigos.mandelbrot.implementations.ui.WindowDecoratorServiceImpl;
import com.threeamigos.mandelbrot.implementations.ui.WindowDecoratorSnapshotServiceFragmentImpl;
import com.threeamigos.mandelbrot.implementations.ui.ZoomBoxServiceImpl;
import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;
import com.threeamigos.mandelbrot.interfaces.service.CalculationType;
import com.threeamigos.mandelbrot.interfaces.service.FractalService;
import com.threeamigos.mandelbrot.interfaces.service.FractalServiceFactory;
import com.threeamigos.mandelbrot.interfaces.service.ImagePersisterService;
import com.threeamigos.mandelbrot.interfaces.service.ImageProducerServiceFactory;
import com.threeamigos.mandelbrot.interfaces.service.Points;
import com.threeamigos.mandelbrot.interfaces.service.PointsOfInterestService;
import com.threeamigos.mandelbrot.interfaces.service.SchedulerService;
import com.threeamigos.mandelbrot.interfaces.service.SnapshotService;
import com.threeamigos.mandelbrot.interfaces.ui.CalculationParametersRequester;
import com.threeamigos.mandelbrot.interfaces.ui.FontService;
import com.threeamigos.mandelbrot.interfaces.ui.Resolution;
import com.threeamigos.mandelbrot.interfaces.ui.WindowDecoratorService;

public class Main {

	public Main() {

		CalculationParametersRequester calculationParametersRequester = new CalculationParametersRequesterImpl();

		CalculationParameters calculationParameters = calculationParametersRequester.getCalculationParameters(null);
		if (calculationParameters == null) {
			return;
		}

		Resolution resolution = calculationParameters.getResolution();

		Points points = new PointsImpl(resolution);

		FractalServiceFactory fractalServiceFactory = new FractalServiceFactoryImpl();

		SchedulerService schedulerService = new SchedulerServiceImpl(new PrioritizedRunnableLIFOComparator());

		ImageProducerServiceFactory imageProducerServiceFactory = new ImageProducerServiceFactoryImpl();

		ImagePersisterService imagePersisterService = new ImagePersisterServiceImpl();

		PointsOfInterestService pointsOfInterestService = new PointsOfInterestServiceImpl();

		SnapshotService snapshotService = new SnapshotServiceImpl(calculationParametersRequester, fractalServiceFactory,
				imageProducerServiceFactory, imagePersisterService, schedulerService);

		FractalService fractalService = fractalServiceFactory.createInstance(calculationParameters, schedulerService,
				CalculationType.FOREGROUND);

		FontService fontService = new FontServiceImpl();

		WindowDecoratorService windowDecoratorService = new WindowDecoratorServiceImpl(
				new WindowDecoratorInfoFragmentImpl(resolution, fontService, fractalService, points),
				new WindowDecoratorHelpFragmentImpl(resolution, fontService, pointsOfInterestService),
				new WindowDecoratorPointOfInterestNameFragmentImpl(resolution, fontService, pointsOfInterestService),
				new WindowDecoratorSnapshotServiceFragmentImpl(resolution, fontService, snapshotService));

		FractalCanvas fractalCanvas = new FractalCanvas(fractalService, pointsOfInterestService,
				imageProducerServiceFactory, snapshotService, points, windowDecoratorService, new AboutWindowImpl(),
				calculationParameters);

		fractalCanvas.addRenderableConsumer(new ZoomBoxServiceImpl(points));
		fractalCanvas
				.addRenderableConsumer(new JuliaBoundariesServiceImpl(points, fontService, pointsOfInterestService));

		points.addPropertyChangeListener(fractalCanvas);
		fractalService.addPropertyChangeListener(fractalCanvas);
		snapshotService.addPropertyChangeListener(fractalCanvas);

		imagePersisterService.setMessageNotifier(fractalCanvas);
		pointsOfInterestService.setMessageNotifier(fractalCanvas);

		pointsOfInterestService.loadPointsOfInterest();

		JFrame jframe = prepareFrame(fractalCanvas);

		JMenuBar menuBar = prepareMenu(jframe);

		fractalCanvas.addMenus(menuBar);

		jframe.setVisible(true);

		fractalCanvas.startCalculationThread();

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

	public JMenuBar prepareMenu(JFrame jframe) {
		JMenuBar menuBar = new JMenuBar();
		jframe.setJMenuBar(menuBar);
		return menuBar;
	}

	public static void main(String[] args) {
		new Main();
	}

}
