package com.threeamigos.mandelbrot.implementations.service;

import com.threeamigos.mandelbrot.implementations.service.mandelbrot.MultithreadedMandelbrotService;
import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;
import com.threeamigos.mandelbrot.interfaces.service.MandelbrotService;
import com.threeamigos.mandelbrot.interfaces.service.MandelbrotServiceFactory;
import com.threeamigos.mandelbrot.interfaces.service.SchedulerService;

public class MandelbrotServiceFactoryImpl implements MandelbrotServiceFactory {

	@Override
	public MandelbrotService createInstance(CalculationParameters calculationParameters,
			SchedulerService schedulerService, int priority) {
		return new MultithreadedMandelbrotService(calculationParameters, schedulerService, priority);
	}

}
