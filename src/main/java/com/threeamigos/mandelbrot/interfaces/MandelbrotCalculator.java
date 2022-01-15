package com.threeamigos.mandelbrot.interfaces;

public interface MandelbrotCalculator {

	public static final int MAX_ITERATIONS = 8192;

	public int getNumberOfThreads();

	public void calculate(PointsInfo pointsInfo, int width, int height);

	public void interruptPreviousCalculation();

	public long getDrawTime();

	public DataBuffer getDataBuffer();

}
