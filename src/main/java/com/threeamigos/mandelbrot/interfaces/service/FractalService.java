package com.threeamigos.mandelbrot.interfaces.service;

import java.beans.PropertyChangeListener;

import com.threeamigos.mandelbrot.interfaces.ui.Resolution;

/**
 * An object that calculates a fractal representation from a set of
 * {@link Points}, a {@link Resolution} and some {@link CalculationParameters}
 *
 * @author Stefano Reksten
 *
 */
public interface FractalService {

	String CALCULATION_RESTART_REQUIRED_PROPERTY_CHANGE = "Calculation restart required";

	String CALCULATION_IN_PROGRESS_PROPERTY_CHANGE = "Calculation in progress";

	String CALCULATION_COMPLETE_PROPERTY_CHANGE = "Calculation complete";

	String BACKGROUND_CALCULATION_IN_PROGRESS_PROPERTY_CHANGE = "Background calculation in progress";

	String BACKGROUND_CALCULATION_COMPLETE_PROPERTY_CHANGE = "Background calculation complete";

	int ITERATION_NOT_CALCULATED = -1;

	void calculate(Points points, Resolution resolution, CalculationParameters calculationParameters);

	void interruptCalculation();

	boolean isCalculating();

	int getPercentage();

	long getCalculationTime();

	int[] getIterations();

	int getIterations(int x, int y);

	void addPropertyChangeListener(PropertyChangeListener pcl);

	void removePropertyChangeListener(PropertyChangeListener pcl);

}
