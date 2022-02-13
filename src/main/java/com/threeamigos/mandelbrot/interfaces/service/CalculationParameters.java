package com.threeamigos.mandelbrot.interfaces.service;

/**
 * An object that holds parameters needed to calculate a fractal - number of
 * threads to use and maximum iterations per single point.
 *
 * @author stefano.reksten
 *
 */
public interface CalculationParameters {

	public static final int MIN_ITERATIONS_EXPONENT = 5;

	public static final int MAX_ITERATIONS_EXPONENT = 15;

	public boolean setMaxThreads(int numberOfThreads);

	public int getMaxThreads();

	public boolean incrementMaxThreads();

	public boolean decrementMaxThreads();

	public boolean setMaxIterations(int maxIterations);

	public int getMaxIterations();

	public boolean doubleUpMaxIterations();

	public boolean halveMaxIterations();

	public void setCalculationType(CalculationType calculationType);

	public CalculationType getCalculationType();

}
