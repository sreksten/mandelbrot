package com.threeamigos.mandelbrot.implementations.ui;

import com.threeamigos.mandelbrot.interfaces.ui.Resolution;

public class CustomResolution implements Resolution {

	private int width;
	private int height;

	CustomResolution(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public String getName() {
		return toString();
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public String toString() {
		return String.format("Custom (%d x %d)", width, height);
	}

}
