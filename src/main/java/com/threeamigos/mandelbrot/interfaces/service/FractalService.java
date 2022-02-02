package com.threeamigos.mandelbrot.interfaces.service;

import java.beans.PropertyChangeListener;

public interface FractalService {

	public static final String CALCULATION_IN_PROGRESS_PROPERTY_CHANGE = "Calculation in progress";

	public static final String CALCULATION_COMPLETE_PROPERTY_CHANGE = "Calculation complete";

	public static final String BACKGROUND_CALCULATION_IN_PROGRESS_PROPERTY_CHANGE = "Background calculation in progress";

	public static final String BACKGROUND_CALCULATION_COMPLETE_PROPERTY_CHANGE = "Background calculation complete";

	public static final int MIN_ITERATIONS_EXPONENT = 5;

	public static final int MAX_ITERATIONS_EXPONENT = 15;

	public static final int ITERATION_NOT_CALCULATED = -1;

	public boolean setNumberOfThreads(int numberOfThreads);

	public int getNumberOfThreads();

	public boolean incrementNumberOfThreads();

	public boolean decrementNumberOfThreads();

	public boolean setMaxIterations(int maxIterations);

	public int getMaxIterations();

	public boolean doubleUpMaxIterations();

	public boolean halveMaxIterations();

	public void calculate(Points points);

	public void interruptCalculation();

	public boolean isCalculating();

	public int getPercentage();

	public long getDrawTime();

	public int[] getIterations();

	public int getIterations(int x, int y);

	public void addPropertyChangeListener(PropertyChangeListener pcl);

	public void removePropertyChangeListener(PropertyChangeListener pcl);

}
