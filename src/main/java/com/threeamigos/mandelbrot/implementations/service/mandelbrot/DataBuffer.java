package com.threeamigos.mandelbrot.implementations.service.mandelbrot;

interface DataBuffer {

	public int getWidth();

	public int getHeight();

	public void setPixel(int x, int y, int value);

	public int getPixel(int x, int y);

	public int[] getPixels();

	public void clear();

}
