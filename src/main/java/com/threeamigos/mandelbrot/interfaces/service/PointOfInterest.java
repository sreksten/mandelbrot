package com.threeamigos.mandelbrot.interfaces.service;

public interface PointOfInterest {

	public void setName(String name);

	public String getName();

	public double getMinImaginary();

	public double getMaxImaginary();

	public double getCentralReal();

	public int getZoomCount();

	public int getMaxIterations();

}
