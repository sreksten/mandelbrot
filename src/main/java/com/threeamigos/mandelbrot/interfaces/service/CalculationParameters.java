package com.threeamigos.mandelbrot.interfaces.service;

import com.threeamigos.mandelbrot.Resolution;

public interface CalculationParameters {

	public Resolution getResolution();

	public int getMaxThreads();

	public int getMaxIterations();

}
