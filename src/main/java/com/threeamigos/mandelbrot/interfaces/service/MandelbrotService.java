package com.threeamigos.mandelbrot.interfaces.service;

import java.beans.PropertyChangeListener;

public interface MandelbrotService {

	public static final String CALCULATION_COMPLETE_PROPERTY_CHANGE = "Calculation complete";

	public static final int MIN_ITERATIONS_EXPONENT = 8;

	public static final int MAX_ITERATIONS_EXPONENT = 15;

	public static final int ITERATION_NOT_CALCULATED = -1;

	public int getNumberOfThreads();

	public int getMaxIterations();

	public boolean doubleUpMaxIterations();

	public boolean halveMaxIterations();

	public boolean decrementThreads();

	public boolean incrementThreads();

	public void calculate(PointsInfo pointsInfo);

	public void interruptPreviousCalculation();

	public long getDrawTime();

	public int[] getIterations();

	public int getIterations(int x, int y);

	public void addPropertyChangeListener(PropertyChangeListener pcl);

	public void removePropertyChangeListener(PropertyChangeListener pcl);

}
