package com.threeamigos.mandelbrot.implementations.service;

import com.threeamigos.mandelbrot.implementations.service.fractal.MultithreadedFractalService;
import com.threeamigos.mandelbrot.interfaces.service.FractalService;
import com.threeamigos.mandelbrot.interfaces.service.FractalServiceFactory;
import com.threeamigos.mandelbrot.interfaces.service.BackgroundExecutionService;

public class FractalServiceFactoryImpl implements FractalServiceFactory {

	private final BackgroundExecutionService backgroundExecutionService;

	public FractalServiceFactoryImpl(BackgroundExecutionService backgroundExecutionService) {
		this.backgroundExecutionService = backgroundExecutionService;
	}

	@Override
	public FractalService createInstance() {
		return new MultithreadedFractalService(backgroundExecutionService);
	}

}
