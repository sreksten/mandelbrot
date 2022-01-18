package com.threeamigos.mandelbrot;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.threeamigos.mandelbrot.implementations.ImageProducerImpl;
import com.threeamigos.mandelbrot.implementations.MandelbrotCalculatorFactory;
import com.threeamigos.mandelbrot.implementations.MultipleVariantImageProducerFactoryImpl;
import com.threeamigos.mandelbrot.implementations.PointsInfoImpl;
import com.threeamigos.mandelbrot.implementations.service.ImageServiceImpl;
import com.threeamigos.mandelbrot.implementations.service.PointsOfInterestServiceImpl;
import com.threeamigos.mandelbrot.implementations.ui.CalculationParametersRequesterImpl;
import com.threeamigos.mandelbrot.interfaces.CalculationParameters;
import com.threeamigos.mandelbrot.interfaces.MandelbrotCalculatorProducer;
import com.threeamigos.mandelbrot.interfaces.MultipleVariantImageProducer;
import com.threeamigos.mandelbrot.interfaces.MultipleVariantImageProducerFactory;
import com.threeamigos.mandelbrot.interfaces.PointsInfo;
import com.threeamigos.mandelbrot.interfaces.service.ImageService;
import com.threeamigos.mandelbrot.interfaces.service.PointsOfInterestService;
import com.threeamigos.mandelbrot.interfaces.ui.CalculationParametersRequester;

public class Main {

	public Main() {

		CalculationParametersRequester calculationParametersRequester = new CalculationParametersRequesterImpl();

		CalculationParameters calculationParameters = calculationParametersRequester.getCalculationParameters(null);
		if (calculationParameters == null) {
			return;
		}

		Resolution resolution = calculationParameters.getResolution();

		PointsInfo pointsInfo = new PointsInfoImpl();
		pointsInfo.setDimensions(resolution.getWidth(), resolution.getHeight());

		MandelbrotCalculatorProducer mandelbrotCalculatorProducer = new MandelbrotCalculatorFactory(
				calculationParameters.getMaxThreads(), calculationParameters.getMaxIterations());
		MultipleVariantImageProducerFactory imageProducerFactory = new MultipleVariantImageProducerFactoryImpl();
		MultipleVariantImageProducer imageProducer = new ImageProducerImpl(calculationParameters.getMaxIterations());
		ImageService imageService = new ImageServiceImpl();
		PointsOfInterestService pointsOfInterestService = new PointsOfInterestServiceImpl();

		MandelbrotCanvas mandelbrotCanvas = new MandelbrotCanvas(calculationParametersRequester,
				mandelbrotCalculatorProducer, pointsInfo, imageProducerFactory, pointsOfInterestService, imageService,
				calculationParameters);

		prepareFrame(mandelbrotCanvas);

		pointsOfInterestService.loadPointsOfInterest(mandelbrotCanvas);
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
