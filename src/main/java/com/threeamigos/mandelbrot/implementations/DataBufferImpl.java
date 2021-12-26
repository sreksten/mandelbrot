package com.threeamigos.mandelbrot.implementations;

import java.util.Arrays;

import com.threeamigos.mandelbrot.interfaces.DataBuffer;

public class DataBufferImpl implements DataBuffer {

	private int width;
	private int height;
	private int[] pixels;

	@Override
	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
		pixels = new int[width * height];
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
	public void clear() {
		Arrays.fill(pixels, NOT_CALCULATED);
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

}
