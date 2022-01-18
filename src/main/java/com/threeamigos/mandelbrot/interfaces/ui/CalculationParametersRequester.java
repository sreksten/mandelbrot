package com.threeamigos.mandelbrot.interfaces.ui;

import java.awt.Component;

import com.threeamigos.mandelbrot.interfaces.CalculationParameters;

public interface CalculationParametersRequester {

	public CalculationParameters getCalculationParameters(Component component);

}
