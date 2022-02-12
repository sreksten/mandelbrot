package com.threeamigos.mandelbrot.implementations.ui;

import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;
import com.threeamigos.mandelbrot.interfaces.ui.Resolution;

public class CalculationParametersImpl implements CalculationParameters {

	private final Resolution resolution;
	private final int maxThreads;
	private final int maxIterations;

	CalculationParametersImpl(Resolution resolution, int maxThreads, int maxIterations) {
		this.resolution = resolution;
		this.maxThreads = maxThreads;
		this.maxIterations = maxIterations;
	}

	@Override
	public Resolution getResolution() {
		return resolution;
	}

	@Override
	public int getMaxThreads() {
		return maxThreads;
	}

	@Override
	public int getMaxIterations() {
		return maxIterations;
	}

}
