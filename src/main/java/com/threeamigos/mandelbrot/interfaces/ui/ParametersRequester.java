package com.threeamigos.mandelbrot.interfaces.ui;

import java.awt.Component;

import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;

public interface ParametersRequester {

	public boolean requestParameters(Component component);

	public boolean requestParameters(boolean matchScreenResolution, int maxIterations, Component component);

	public Resolution getResolution();

	public CalculationParameters getCalculationParameters();

}
