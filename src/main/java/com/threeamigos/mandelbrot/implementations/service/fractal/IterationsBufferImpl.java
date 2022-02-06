package com.threeamigos.mandelbrot.implementations.service.fractal;

import java.util.Arrays;

import com.threeamigos.mandelbrot.Resolution;
import com.threeamigos.mandelbrot.interfaces.service.FractalService;

class IterationsBufferImpl implements IterationsBuffer {

	private int width;
	private int height;
	private int[] pixels;

	public IterationsBufferImpl(Resolution resolution) {
		this(resolution.getWidth(), resolution.getHeight());
	}

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
