package com.threeamigos.mandelbrot.implementations;

import com.threeamigos.mandelbrot.interfaces.PointOfInterest;

public class PointOfInterestImpl implements PointOfInterest {

	private String name;
	private double minImaginary;
	private double maxImaginary;
	private double centralReal;
	private int zoomCount;

	public PointOfInterestImpl(String name, double minImaginary, double maxImaginary, double centralReal,
			int zoomCount) {
		this.name = name;
		this.minImaginary = minImaginary;
		this.maxImaginary = maxImaginary;
		this.centralReal = centralReal;
		this.zoomCount = zoomCount;
	}

	@Override
	public String getName() {
		return name;
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
	public double getCentralReal() {
		return centralReal;
	}

	@Override
	public int getZoomCount() {
		return zoomCount;
	}

}
