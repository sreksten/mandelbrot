package com.threeamigos.mandelbrot.interfaces.service;

/**
 * An object that holds parameters needed to calculate a fractal - number of
 * threads to use and maximum iterations per single point.
 *
 * @author stefano.reksten
 *
 */
public interface CalculationParameters {

	int MIN_ITERATIONS_EXPONENT = 5;

	int MAX_ITERATIONS_EXPONENT = 15;

	boolean setMaxThreads(int numberOfThreads);

	int getMaxThreads();

	boolean incrementMaxThreads();

	boolean decrementMaxThreads();

	boolean setMaxIterations(int maxIterations);

	int getMaxIterations();

	boolean doubleUpMaxIterations();

	boolean halveMaxIterations();

	void setCalculationType(CalculationType calculationType);

	CalculationType getCalculationType();

}
