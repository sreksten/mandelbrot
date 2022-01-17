package com.threeamigos.mandelbrot.implementations;

import com.threeamigos.mandelbrot.interfaces.MandelbrotCalculator;
import com.threeamigos.mandelbrot.interfaces.MandelbrotCalculatorProducer;

public class MandelbrotCalculatorFactory implements MandelbrotCalculatorProducer {

	private int defaultMaxThreads;
	private int defaultMaxIterations;

	public MandelbrotCalculatorFactory(int defaultMaxThreads, int defaultMaxIterations) {
		this.defaultMaxThreads = defaultMaxThreads;
		this.defaultMaxIterations = defaultMaxIterations;
	}

	@Override
	public MandelbrotCalculator createInstance() {
		return new MultithreadedMandelbrotCalculator(defaultMaxThreads, defaultMaxIterations);
	}

	@Override
	public MandelbrotCalculator createInstance(int maxThreads, int maxIterations) {
		return new MultithreadedMandelbrotCalculator(maxThreads, maxIterations);
	}

}
