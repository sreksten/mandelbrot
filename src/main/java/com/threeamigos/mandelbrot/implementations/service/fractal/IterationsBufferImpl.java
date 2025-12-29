package com.threeamigos.mandelbrot.implementations.service.fractal;

import java.util.Arrays;

import com.threeamigos.mandelbrot.interfaces.service.FractalService;

class IterationsBufferImpl implements IterationsBuffer {

	private final int width;
	private final int height;
	private final int[] pixels;

	public IterationsBufferImpl(int width, int height) {
		this.width = width;
		this.height = height;
		pixels = new int[width * height];
		clear();
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
	public void setPixel(int x, int y, int value) {
		pixels[x + y * width] = value;
	}

	@Override
	public int getIterations(int x, int y) {
		return pixels[x + y * width];
	}

	@Override
	public int[] getIterations() {
		return pixels;
	}

	@Override
	public void clear() {
		Arrays.fill(pixels, FractalService.ITERATION_NOT_CALCULATED);
	}

}
