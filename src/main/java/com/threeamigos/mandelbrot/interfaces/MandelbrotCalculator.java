package com.threeamigos.mandelbrot.interfaces;

public interface MandelbrotCalculator {

	public static final int MAX_ITERATIONS = 8192;

	public void calculate(PointsInfo pointsInfo, DataBuffer dataBuffer);

	public void interruptPreviousCalculation();

	public long getDrawTime();

}
