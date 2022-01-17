package com.threeamigos.mandelbrot.interfaces;

public interface MandelbrotCalculatorProducer {

	public MandelbrotCalculator createInstance();

	public MandelbrotCalculator createInstance(int maxThreads, int maxIterations);

}
