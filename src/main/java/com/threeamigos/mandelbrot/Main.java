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
import com.threeamigos.mandelbrot.implementations.ui.CalculationParametersRequesterImpl;
import com.threeamigos.mandelbrot.implementations.ui.ShowHelpImpl;
import com.threeamigos.mandelbrot.implementations.ui.ShowInfoImpl;
import com.threeamigos.mandelbrot.implementations.ui.ShowPointOfInterestNameImpl;
import com.threeamigos.mandelbrot.implementations.ui.ZoomBoxImpl;
import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;
import com.threeamigos.mandelbrot.interfaces.service.ImagePersisterService;
import com.threeamigos.mandelbrot.interfaces.service.ImageProducerServiceFactory;
import com.threeamigos.mandelbrot.interfaces.service.MandelbrotService;
import com.threeamigos.mandelbrot.interfaces.service.MandelbrotServiceFactory;
import com.threeamigos.mandelbrot.interfaces.service.Points;
import com.threeamigos.mandelbrot.interfaces.service.PointsOfInterestService;
import com.threeamigos.mandelbrot.interfaces.service.SnapshotService;
import com.threeamigos.mandelbrot.interfaces.ui.CalculationParametersRequester;
import com.threeamigos.mandelbrot.interfaces.ui.ShowHelp;
import com.threeamigos.mandelbrot.interfaces.ui.ShowInfo;
import com.threeamigos.mandelbrot.interfaces.ui.ShowPointOfInterestName;
import com.threeamigos.mandelbrot.interfaces.ui.ZoomBox;

public class Main {

	private CalculationParametersRequester calculationParametersRequester;
	private CalculationParameters calculationParameters;
	private Resolution resolution;
	private Points points;
	private MandelbrotServiceFactory mandelbrotServiceFactory;
	private ImageProducerServiceFactory imageProducerServiceFactory;
	private ImagePersisterService imagePersisterService;
	private PointsOfInterestService pointsOfInterestService;
	private SnapshotService snapshotService;
	private MandelbrotService mandelbrotService;
	private ShowInfo showInfo;
	private ShowHelp showHelp;
	private ShowPointOfInterestName showPointOfInterestName;
	private ZoomBox zoomBox;

	public Main() {

		calculationParametersRequester = new CalculationParametersRequesterImpl();

		calculationParameters = calculationParametersRequester.getCalculationParameters(null);
		if (calculationParameters == null) {
			return;
		}

		resolution = calculationParameters.getResolution();

		points = new PointsImpl(resolution);

		mandelbrotServiceFactory = new MandelbrotServiceFactoryImpl();

		imageProducerServiceFactory = new ImageProducerServiceFactoryImpl();

		imagePersisterService = new ImagePersisterServiceImpl();

		pointsOfInterestService = new PointsOfInterestServiceImpl();

		snapshotService = new SnapshotServiceImpl(calculationParametersRequester, mandelbrotServiceFactory,
				imageProducerServiceFactory, imagePersisterService);

		mandelbrotService = mandelbrotServiceFactory.createInstance(calculationParameters);

		showInfo = new ShowInfoImpl(resolution, mandelbrotService, points);

		showHelp = new ShowHelpImpl(resolution, pointsOfInterestService);

		showPointOfInterestName = new ShowPointOfInterestNameImpl(resolution, pointsOfInterestService);

		zoomBox = new ZoomBoxImpl(points);

		MandelbrotCanvas mandelbrotCanvas = new MandelbrotCanvas(mandelbrotService, pointsOfInterestService,
				imageProducerServiceFactory, snapshotService, points, calculationParameters, zoomBox, showInfo,
				showHelp, showPointOfInterestName);

		mandelbrotService.addPropertyChangeListener(mandelbrotCanvas);

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
