package com.threeamigos.mandelbrot.implementations.ui;

import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;

public class CalculationParametersImpl implements CalculationParameters {

	private final int maxThreads;
	private final int maxIterations;

	CalculationParametersImpl(int maxThreads, int maxIterations) {
		this.maxThreads = maxThreads;
		this.maxIterations = maxIterations;
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
