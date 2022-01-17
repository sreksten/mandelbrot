package com.threeamigos.mandelbrot.interfaces;

import java.awt.Image;

public interface MandelbrotCalculator {

	public static final int MIN_CALCULATIONS_EXPONENT = 8;

	public static final int MAX_CALCULATIONS_EXPONENT = 15;

	public int getNumberOfThreads();

	public int getMaxIterations();

	public void calculate(PointsInfo pointsInfo, int width, int height);

	public void interruptPreviousCalculation();

	public long getDrawTime();

	public Image produceImage(ImageProducer imageProducer);

	public int getIterations(int x, int y);

}
