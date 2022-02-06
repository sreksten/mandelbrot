package com.threeamigos.mandelbrot.implementations.service.fractal;

interface IterationsBuffer {

	public int getWidth();

	public int getHeight();

	public void setPixel(int x, int y, int value);

	public int getIterations(int x, int y);

	public int[] getIterations();

	public void clear();

}
