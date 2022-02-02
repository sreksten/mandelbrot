package com.threeamigos.mandelbrot.implementations.service;

import com.threeamigos.mandelbrot.implementations.service.mandelbrot.MultithreadedFractalService;
import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;
import com.threeamigos.mandelbrot.interfaces.service.FractalService;
import com.threeamigos.mandelbrot.interfaces.service.FractalServiceFactory;
import com.threeamigos.mandelbrot.interfaces.service.CalculationType;
import com.threeamigos.mandelbrot.interfaces.service.SchedulerService;

public class FractalServiceFactoryImpl implements FractalServiceFactory {

	@Override
	public FractalService createInstance(CalculationParameters calculationParameters,
			SchedulerService schedulerService, CalculationType serviceType) {
		return new MultithreadedFractalService(calculationParameters, schedulerService, serviceType);
	}

}
