package com.threeamigos.mandelbrot.interfaces.service;

/**
 * Enumerates the type of fractals this program can render
 *
 * @author Stefano Reksten
 *
 */
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
