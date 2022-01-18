package com.threeamigos.mandelbrot.implementations.service;

import com.threeamigos.mandelbrot.implementations.service.mandelbrot.MultithreadedMandelbrotService;
import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;
import com.threeamigos.mandelbrot.interfaces.service.MandelbrotService;
import com.threeamigos.mandelbrot.interfaces.service.MandelbrotServiceFactory;

public class MandelbrotServiceFactoryImpl implements MandelbrotServiceFactory {

	@Override
	public MandelbrotService createInstance(CalculationParameters calculationParameters) {
		return new MultithreadedMandelbrotService(calculationParameters);
	}

}
