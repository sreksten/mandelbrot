package com.threeamigos.mandelbrot;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.threeamigos.mandelbrot.implementations.CalculationParametersRequesterImpl;
import com.threeamigos.mandelbrot.implementations.DiskPersister;
import com.threeamigos.mandelbrot.implementations.ImageProducerImpl;
import com.threeamigos.mandelbrot.implementations.MandelbrotCalculatorFactory;
import com.threeamigos.mandelbrot.implementations.MultipleVariantImageProducerFactoryImpl;
import com.threeamigos.mandelbrot.implementations.PointsInfoImpl;
import com.threeamigos.mandelbrot.implementations.PointsOfInterestImpl;
import com.threeamigos.mandelbrot.interfaces.CalculationParameters;
import com.threeamigos.mandelbrot.interfaces.CalculationParametersRequester;
import com.threeamigos.mandelbrot.interfaces.DataPersister;
import com.threeamigos.mandelbrot.interfaces.DataPersister.PersistResult;
import com.threeamigos.mandelbrot.interfaces.MandelbrotCalculatorProducer;
import com.threeamigos.mandelbrot.interfaces.MultipleVariantImageProducer;
import com.threeamigos.mandelbrot.interfaces.MultipleVariantImageProducerFactory;
import com.threeamigos.mandelbrot.interfaces.PointsInfo;
import com.threeamigos.mandelbrot.interfaces.PointsOfInterest;

public class Main {

	public Main() {

		CalculationParametersRequester calculationParametersRequester = new CalculationParametersRequesterImpl();

		CalculationParameters calculationParameters = calculationParametersRequester.getCalculationParameters(null);

		if (calculationParameters == null) {
			return;
		}

		Resolution resolution = calculationParameters.getResolution();
		int width = resolution.getWidth();
		int height = resolution.getHeight();

		PointsInfo pointsInfo = new PointsInfoImpl();
		pointsInfo.setDimensions(width, height);

		MandelbrotCalculatorProducer mandelbrotCalculatorProducer = new MandelbrotCalculatorFactory(
				calculationParameters.getMaxThreads(), calculationParameters.getMaxIterations());
		MultipleVariantImageProducerFactory imageProducerFactory = new MultipleVariantImageProducerFactoryImpl();
		MultipleVariantImageProducer imageProducer = new ImageProducerImpl(calculationParameters.getMaxIterations());
		DataPersister dataPersister = new DiskPersister();
		PointsOfInterest pointsOfInterest = loadPointsOfInterest(dataPersister);

		MandelbrotCanvas mandelbrotCanvas = new MandelbrotCanvas(width, height, calculationParametersRequester,
				mandelbrotCalculatorProducer, pointsInfo, imageProducerFactory, pointsOfInterest, dataPersister,
				calculationParameters);

		prepareFrame(mandelbrotCanvas);
	}

	private final PointsOfInterest loadPointsOfInterest(DataPersister dataPersister) {
		PointsOfInterest pointsOfInterest = null;

		PersistResult persistResult = dataPersister.loadPointsOfInterest();
		if (persistResult.isSuccessful()) {
			pointsOfInterest = new PointsOfInterestImpl(persistResult.getPointsOfInterest());
		} else {
			pointsOfInterest = new PointsOfInterestImpl(new ArrayList<>());
		}
		return pointsOfInterest;
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
