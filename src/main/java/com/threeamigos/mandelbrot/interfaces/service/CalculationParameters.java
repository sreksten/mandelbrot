package com.threeamigos.mandelbrot.interfaces.service;

import com.threeamigos.mandelbrot.interfaces.ui.Resolution;

public interface CalculationParameters {

	public Resolution getResolution();

	public int getMaxThreads();

	public int getMaxIterations();

}
