package com.threeamigos.mandelbrot.interfaces.ui;

import java.awt.Component;

import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;

public interface CalculationParametersRequester {

	public CalculationParameters getCalculationParameters(Component component);

	public CalculationParameters getCalculationParameters(int maxIterations, Component component);

}
