package com.threeamigos.mandelbrot.implementations;

import com.threeamigos.mandelbrot.interfaces.PointOfInterest;

public class PointOfInterestImpl implements PointOfInterest {

	private String name;
	private double minReal;
	private double maxReal;
	private double minImaginary;
	private double maxImaginary;
	private int zoomCount;

	public PointOfInterestImpl(String name, double minReal, double maxReal, double minImaginary, double maxImaginary,
			int zoomCount) {
		this.name = name;
		this.minReal = minReal;
		this.maxReal = maxReal;
		this.minImaginary = minImaginary;
		this.maxImaginary = maxImaginary;
		this.zoomCount = zoomCount;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public double getMinReal() {
		return minReal;
	}

	@Override
	public double getMaxReal() {
		return maxReal;
	}

	@Override
	public double getMinImaginary() {
		return minImaginary;
	}

	@Override
	public double getMaxImaginary() {
		return maxImaginary;
	}

	@Override
	public int getZoomCount() {
		return zoomCount;
	}

}
