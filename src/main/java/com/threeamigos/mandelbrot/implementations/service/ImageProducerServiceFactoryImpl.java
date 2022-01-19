package com.threeamigos.mandelbrot.implementations.service;

import com.threeamigos.mandelbrot.implementations.service.imageproducer.MultipleColorModelImageProducerImpl;
import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;
import com.threeamigos.mandelbrot.interfaces.service.MultipleColorModelImageProducerService;
import com.threeamigos.mandelbrot.interfaces.service.MultipleColorModelImageProducerServiceFactory;

public class MultipleColorModelImageProducerServiceFactoryImpl implements MultipleColorModelImageProducerServiceFactory {

	@Override
	public MultipleColorModelImageProducerService createInstance(CalculationParameters calculationParameters) {
		return new MultipleColorModelImageProducerImpl(calculationParameters.getMaxIterations());
	}

}
