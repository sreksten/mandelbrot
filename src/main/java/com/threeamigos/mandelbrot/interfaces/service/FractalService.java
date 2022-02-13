package com.threeamigos.mandelbrot.interfaces.service;

import java.beans.PropertyChangeListener;

import com.threeamigos.mandelbrot.interfaces.ui.Resolution;

public interface FractalService {

	public static final String CALCULATION_RESTART_REQUIRED_PROPERTY_CHANGE = "Calculation restart required";

	public static final String CALCULATION_IN_PROGRESS_PROPERTY_CHANGE = "Calculation in progress";

	public static final String CALCULATION_COMPLETE_PROPERTY_CHANGE = "Calculation complete";

	public static final String BACKGROUND_CALCULATION_IN_PROGRESS_PROPERTY_CHANGE = "Background calculation in progress";

	public static final String BACKGROUND_CALCULATION_COMPLETE_PROPERTY_CHANGE = "Background calculation complete";

	public static final int ITERATION_NOT_CALCULATED = -1;

	public void calculate(Points points, Resolution resolution, CalculationParameters calculationParameters);

	public void interruptCalculation();

	public boolean isCalculating();

	public int getPercentage();

	public long getCalculationTime();

	public int[] getIterations();

	public int getIterations(int x, int y);

	public void addPropertyChangeListener(PropertyChangeListener pcl);

	public void removePropertyChangeListener(PropertyChangeListener pcl);

}
