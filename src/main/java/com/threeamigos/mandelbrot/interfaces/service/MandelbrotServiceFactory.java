package com.threeamigos.mandelbrot.interfaces.service;

public interface MandelbrotServiceFactory {

	public MandelbrotService createInstance(CalculationParameters calculationParameters,
			SchedulerService schedulerService, MandelbrotServiceTypeEnum serviceType);

}
