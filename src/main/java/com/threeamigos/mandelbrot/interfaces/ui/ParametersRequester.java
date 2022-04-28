package com.threeamigos.mandelbrot.interfaces.ui;

import java.awt.Component;

import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;

/**
 * An object that allows an user to choose a {@link Resolution} and
 * {@link CalculationParameters} used to render a fractal.
 *
 * @author Stefano Reksten
 *
 */
public interface ParametersRequester {

	public boolean requestParameters();

	public boolean requestParameters(Component component);

	public boolean requestParameters(boolean matchScreenResolution, int maxIterations, Component component);

	public Resolution getResolution();

	public CalculationParameters getCalculationParameters();

}
