package com.threeamigos.mandelbrot.implementations;

import com.threeamigos.mandelbrot.interfaces.CalculationParameters;
import com.threeamigos.mandelbrot.interfaces.MultipleVariantImageProducer;
import com.threeamigos.mandelbrot.interfaces.MultipleVariantImageProducerFactory;

public class MultipleVariantImageProducerFactoryImpl implements MultipleVariantImageProducerFactory {

	@Override
	public MultipleVariantImageProducer createInstance(CalculationParameters calculationParameters) {
		return new ImageProducerImpl(calculationParameters.getMaxIterations());
	}

}
