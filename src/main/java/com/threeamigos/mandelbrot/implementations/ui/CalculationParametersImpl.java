package com.threeamigos.mandelbrot.implementations.ui;

import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;
import com.threeamigos.mandelbrot.interfaces.service.CalculationType;

public class CalculationParametersImpl implements CalculationParameters {

	private int maxThreads;
	private int maxIterations;
	private CalculationType calculationType;

	CalculationParametersImpl(int maxThreads, int maxIterations) {
		this.maxThreads = maxThreads;
		this.maxIterations = maxIterations;
		this.calculationType = CalculationType.FOREGROUND;
	}

	CalculationParametersImpl(int maxThreads, int maxIterations, CalculationType calculationType) {
		this.maxThreads = maxThreads;
		this.maxIterations = maxIterations;
		this.calculationType = calculationType;
	}

	@Override
	public int getMaxThreads() {
		return maxThreads;
	}

	@Override
	public int getMaxIterations() {
		return maxIterations;
	}

	@Override
	public CalculationType getCalculationType() {
		return calculationType;
	}

	@Override
	public int getNumberOfThreads() {
		return maxThreads;
	}

	@Override
	public boolean doubleUpMaxIterations() {
		if (maxIterations < (1 << MAX_ITERATIONS_EXPONENT)) {
			maxIterations <<= 1;
			return true;
		}
		return false;
	}

	@Override
	public boolean halveMaxIterations() {
		if (maxIterations > (1 << MIN_ITERATIONS_EXPONENT)) {
			maxIterations >>= 1;
			return true;
		}
		return false;
	}

	@Override
	public boolean setMaxIterations(int maxIterations) {
		if (maxIterations >= (1 << MIN_ITERATIONS_EXPONENT) && maxIterations <= (1 << MAX_ITERATIONS_EXPONENT)) {
			this.maxIterations = maxIterations;
			return true;
		}
		return false;
	}

	@Override
	public boolean incrementNumberOfThreads() {
		if (maxThreads < Runtime.getRuntime().availableProcessors()) {
			maxThreads++;
			return true;
		}
		return false;
	}

	@Override
	public boolean decrementNumberOfThreads() {
		if (maxThreads > 1) {
			maxThreads--;
			return true;
		}
		return false;
	}

	@Override
	public boolean setNumberOfThreads(int numberOfThreads) {
		if (numberOfThreads <= Runtime.getRuntime().availableProcessors()) {
			maxThreads = numberOfThreads;
			return true;
		}
		return false;
	}

}
