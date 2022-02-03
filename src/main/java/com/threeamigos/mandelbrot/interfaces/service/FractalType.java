package com.threeamigos.mandelbrot.interfaces.service;

public enum FractalType {

	MANDELBROT("Mandelbrot"),
	JULIA("Julia");

	private String description;

	FractalType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
