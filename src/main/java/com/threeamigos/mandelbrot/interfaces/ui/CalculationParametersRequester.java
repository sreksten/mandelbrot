package com.threeamigos.mandelbrot.interfaces.ui;

import java.awt.Component;

import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;

public interface CalculationParametersRequester {

	public CalculationParameters getCalculationParameters(Component component);

	public CalculationParameters getCalculationParameters(boolean matchScreenResolution, int maxIterations,
			Component component);

}
