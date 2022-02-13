package com.threeamigos.mandelbrot.implementations.service;

import com.threeamigos.mandelbrot.implementations.service.fractal.MultithreadedFractalService;
import com.threeamigos.mandelbrot.interfaces.service.FractalService;
import com.threeamigos.mandelbrot.interfaces.service.FractalServiceFactory;
import com.threeamigos.mandelbrot.interfaces.service.SchedulerService;

public class FractalServiceFactoryImpl implements FractalServiceFactory {

	private final SchedulerService schedulerService;

	public FractalServiceFactoryImpl(SchedulerService schedulerService) {
		this.schedulerService = schedulerService;
	}

	@Override
	public FractalService createInstance() {
		return new MultithreadedFractalService(schedulerService);
	}

}
