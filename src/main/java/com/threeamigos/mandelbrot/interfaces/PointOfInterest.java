package com.threeamigos.mandelbrot.interfaces;

public interface PointOfInterest {

	public String getName();

	public double getMinReal();

	public double getMaxReal();

	public double getMinImaginary();

	public double getMaxImaginary();

	public int getZoomCount();

}
