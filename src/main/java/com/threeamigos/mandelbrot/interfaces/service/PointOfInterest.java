package com.threeamigos.mandelbrot.interfaces.service;

/**
 * Any point in the complex plane around which a nice rendering of a fractal
 * appears using a certain number of maximum iterations
 *
 * @author Stefano Reksten
 *
 */
public interface PointOfInterest {

	public void setName(String name);

	public String getName();

	public double getMinImaginary();

	public double getMaxImaginary();

	public double getCentralReal();

	public int getZoomCount();

	public int getMaxIterations();

	public FractalType getFractalType();

	public double getJuliaCReal();

	public double getJuliaCImaginary();

}
