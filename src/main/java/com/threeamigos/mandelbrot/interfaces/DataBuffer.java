package com.threeamigos.mandelbrot.interfaces;

public interface DataBuffer {

	public static final int NOT_CALCULATED = -1;

	public int getWidth();

	public int getHeight();

	public void setPixel(int x, int y, int value);

	public int getPixel(int x, int y);

	public int[] getPixels();

}
