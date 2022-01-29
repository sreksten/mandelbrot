package com.threeamigos.mandelbrot.implementations.service.mandelbrot;

import java.util.Arrays;

import com.threeamigos.mandelbrot.Resolution;
import com.threeamigos.mandelbrot.interfaces.service.MandelbrotService;

class PixelBufferImpl implements PixelBuffer {

	private int width;
	private int height;
	private int[] pixels;

	public PixelBufferImpl(Resolution resolution) {
		this(resolution.getWidth(), resolution.getHeight());
	}

	public PixelBufferImpl(int width, int height) {
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
	public int getPixel(int x, int y) {
		return pixels[x + y * width];
	}

	@Override
	public int[] getPixels() {
		return pixels;
	}

	@Override
	public void clear() {
		Arrays.fill(pixels, MandelbrotService.ITERATION_NOT_CALCULATED);
	}

}
