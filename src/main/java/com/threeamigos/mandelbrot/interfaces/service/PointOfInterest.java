package com.threeamigos.mandelbrot.interfaces.service;

/**
 * Any point in the complex plane around which a nice rendering of a fractal
 * appears using a certain number of maximum iterations
 *
 * @author Stefano Reksten
 *
 */
public interface PointOfInterest {

	void setName(String name);

	String getName();

	double getMinImaginary();

	double getMaxImaginary();

	double getCentralReal();

	int getZoomCount();

	int getMaxIterations();

	FractalType getFractalType();

	double getJuliaCReal();

	double getJuliaCImaginary();

}
