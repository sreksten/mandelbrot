package com.threeamigos.mandelbrot;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.threeamigos.mandelbrot.implementations.service.ImageServiceImpl;
import com.threeamigos.mandelbrot.implementations.service.MandelbrotServiceFactoryImpl;
import com.threeamigos.mandelbrot.implementations.service.MultipleColorModelImageProducerServiceFactoryImpl;
import com.threeamigos.mandelbrot.implementations.service.PointsInfoImpl;
import com.threeamigos.mandelbrot.implementations.service.PointsOfInterestServiceImpl;
import com.threeamigos.mandelbrot.implementations.ui.CalculationParametersRequesterImpl;
import com.threeamigos.mandelbrot.interfaces.persister.PersistResult;
import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;
import com.threeamigos.mandelbrot.interfaces.service.ImageService;
import com.threeamigos.mandelbrot.interfaces.service.MandelbrotServiceFactory;
import com.threeamigos.mandelbrot.interfaces.service.MultipleColorModelImageProducerServiceFactory;
import com.threeamigos.mandelbrot.interfaces.service.PointsInfo;
import com.threeamigos.mandelbrot.interfaces.service.PointsOfInterestService;
import com.threeamigos.mandelbrot.interfaces.ui.CalculationParametersRequester;

public class Main {

	public Main() {

		CalculationParametersRequester calculationParametersRequester = new CalculationParametersRequesterImpl();

		CalculationParameters calculationParameters = calculationParametersRequester.getCalculationParameters(null);
		if (calculationParameters == null) {
			return;
		}

		PointsInfo pointsInfo = new PointsInfoImpl();
		pointsInfo.setResolution(calculationParameters.getResolution());

		MandelbrotServiceFactory mandelbrotServiceFactory = new MandelbrotServiceFactoryImpl();
		MultipleColorModelImageProducerServiceFactory imageProducerServiceFactory = new MultipleColorModelImageProducerServiceFactoryImpl();
		ImageService imageService = new ImageServiceImpl();
		PointsOfInterestService pointsOfInterestService = new PointsOfInterestServiceImpl();

		MandelbrotCanvas mandelbrotCanvas = new MandelbrotCanvas(calculationParametersRequester,
				mandelbrotServiceFactory, pointsOfInterestService, imageService, imageProducerServiceFactory,
				pointsInfo, calculationParameters);

		prepareFrame(mandelbrotCanvas);

		PersistResult result = pointsOfInterestService.loadPointsOfInterest();
		if (!result.isSuccessful()) {
			mandelbrotCanvas.notify(result.getError());
		}
	}

	private void prepareFrame(MandelbrotCanvas mandelbrotCanvas) {

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
		jframe.setVisible(true);
	}

	public static void main(String[] args) {
		new Main();
	}

}
