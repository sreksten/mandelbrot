package com.threeamigos.mandelbrot.interfaces.ui;

import java.awt.Component;

import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;

/**
 * An object that allows a user to choose a {@link Resolution} and
 * {@link CalculationParameters} used to render a fractal.
 *
 * @author Stefano Reksten
 *
 */
public interface ParametersRequester {

	boolean requestParameters();

	boolean requestParameters(boolean matchScreenResolution, int maxIterations, Component component);

	Resolution getResolution();

	CalculationParameters getCalculationParameters();

}
