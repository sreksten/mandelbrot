package com.threeamigos.mandelbrot.implementations.service;

import com.threeamigos.mandelbrot.interfaces.service.PointOfInterest;

public class PointOfInterestImpl implements PointOfInterest {

	private String name;
	private double minImaginary;
	private double maxImaginary;
	private double centralReal;
	private int zoomCount;
	private int maxIterations;

	public PointOfInterestImpl(String name, double minImaginary, double maxImaginary, double centralReal, int zoomCount,
			int maxIterations) {
		this.name = name;
		this.minImaginary = minImaginary;
		this.maxImaginary = maxImaginary;
		this.centralReal = centralReal;
		this.zoomCount = zoomCount;
		this.maxIterations = maxIterations;
	}

	@Override
	public void setName(String name) {
		this.name = name;
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

	@Override
	public int getMaxIterations() {
		return maxIterations;
	}
}
