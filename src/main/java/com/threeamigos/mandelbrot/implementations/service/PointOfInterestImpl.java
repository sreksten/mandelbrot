package com.threeamigos.mandelbrot.implementations.service;

import com.threeamigos.mandelbrot.interfaces.service.FractalType;
import com.threeamigos.mandelbrot.interfaces.service.PointOfInterest;

public class PointOfInterestImpl implements PointOfInterest {

	private String name;
	private double minImaginary;
	private double maxImaginary;
	private double centralReal;
	private int zoomCount;
	private int maxIterations;
	private FractalType fractalType;
	private double juliaCReal;
	private double juliaCImaginary;

	public PointOfInterestImpl(String name, double minImaginary, double maxImaginary, double centralReal, int zoomCount,
			int maxIterations) {
		this.name = name;
		this.minImaginary = minImaginary;
		this.maxImaginary = maxImaginary;
		this.centralReal = centralReal;
		this.zoomCount = zoomCount;
		this.maxIterations = maxIterations;
		this.fractalType = FractalType.MANDELBROT;
	}

	public PointOfInterestImpl(String name, double minImaginary, double maxImaginary, double centralReal, int zoomCount,
			int maxIterations, double juliaCReal, double juliaCImaginary) {
		this.name = name;
		this.minImaginary = minImaginary;
		this.maxImaginary = maxImaginary;
		this.centralReal = centralReal;
		this.zoomCount = zoomCount;
		this.maxIterations = maxIterations;
		this.fractalType = FractalType.JULIA;
		this.juliaCReal = juliaCReal;
		this.juliaCImaginary = juliaCImaginary;
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

	@Override
	public FractalType getFractalType() {
		return fractalType;
	}

	@Override
	public double getJuliaCReal() {
		return juliaCReal;
	}

	@Override
	public double getJuliaCImaginary() {
		return juliaCImaginary;
	}

}
