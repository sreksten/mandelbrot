package com.threeamigos.mandelbrot.interfaces.service;

public interface CalculationParameters {

	public static final int MIN_ITERATIONS_EXPONENT = 5;

	public static final int MAX_ITERATIONS_EXPONENT = 15;

	public int getMaxThreads();

	public int getMaxIterations();

	public void setCalculationType(CalculationType calculationType);

	public CalculationType getCalculationType();

	public boolean setNumberOfThreads(int numberOfThreads);

	public boolean incrementNumberOfThreads();

	public boolean decrementNumberOfThreads();

	public boolean setMaxIterations(int maxIterations);

	public boolean doubleUpMaxIterations();

	public boolean halveMaxIterations();

}
