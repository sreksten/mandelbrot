package com.threeamigos.mandelbrot.interfaces.service;

public interface FractalServiceFactory {

	public FractalService createInstance(CalculationParameters calculationParameters,
			SchedulerService schedulerService, CalculationType serviceType);

}
