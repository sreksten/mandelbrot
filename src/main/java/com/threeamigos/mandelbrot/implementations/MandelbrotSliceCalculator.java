package com.threeamigos.mandelbrot.implementations;

import com.threeamigos.mandelbrot.interfaces.DataBuffer;
import com.threeamigos.mandelbrot.interfaces.MandelbrotCalculator;
import com.threeamigos.mandelbrot.interfaces.PointsInfo;

public class MandelbrotSliceCalculator implements Runnable {

	private PointsInfo pointsInfo;
	private int startX;
	private int startY;
	private int endX;
	private int endY;
	private DataBuffer dataBuffer;

	private boolean running;

	public MandelbrotSliceCalculator(PointsInfo drawInfo, int startX, int startY, int endX, int endY,
			DataBuffer dataBuffer) {
		this.pointsInfo = drawInfo;
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.dataBuffer = dataBuffer;
	}

	@Override
	public void run() {
		running = true;
		calculateSliceRecursively(startX, endX, startY, endY);
	}

	public void stop() {
		running = false;
	}

	private void calculateSliceRecursively(int fromX, int toX, int fromY, int toY) {
		if (!running) {
			return;
		}
		boolean cardioidVisible = pointsInfo.isCardioidVisible(fromX, toX, fromY, toY);
		boolean period2BulbVisible = pointsInfo.isPeriod2BulbVisible(fromX, toX, fromY, toY);
		if (toX - fromX < 5 || toY - fromY < 5) {
			calculateEveryPixel(fromX, toX, fromY, toY, cardioidVisible, period2BulbVisible);
		} else {
			int uniqueValue = DataBuffer.NOT_CALCULATED;
			boolean hasUniqueValue = true;

			for (int x = fromX; x < toX && running; x++) {
				int iterations = calculateIterations(x, fromY, cardioidVisible, period2BulbVisible);
				if (uniqueValue == DataBuffer.NOT_CALCULATED) {
					uniqueValue = iterations;
				} else if (uniqueValue != iterations) {
					hasUniqueValue = false;
				}
			}

			for (int y = fromY; y < toY && running; y++) {
				int iterations = calculateIterations(fromX, y, cardioidVisible, period2BulbVisible);
				if (uniqueValue == DataBuffer.NOT_CALCULATED) {
					uniqueValue = iterations;
				} else if (uniqueValue != iterations) {
					hasUniqueValue = false;
				}
			}

			for (int x = fromX; x < toX && running; x++) {
				int iterations = calculateIterations(x, toY - 1, cardioidVisible, period2BulbVisible);
				if (uniqueValue == DataBuffer.NOT_CALCULATED) {
					uniqueValue = iterations;
				} else if (uniqueValue != iterations) {
					hasUniqueValue = false;
				}
			}

			for (int y = fromY; y < toY && running; y++) {
				int iterations = calculateIterations(toX - 1, y, cardioidVisible, period2BulbVisible);
				if (uniqueValue == DataBuffer.NOT_CALCULATED) {
					uniqueValue = iterations;
				} else if (uniqueValue != iterations) {
					hasUniqueValue = false;
				}
			}

			if (running) {
				if (hasUniqueValue) {
					setEveryPixel(fromX + 1, toX - 1, fromY + 1, toY - 1, uniqueValue);
				} else {
					int halfX = fromX + (toX - fromX) / 2;
					int halfY = fromY + (toY - fromY) / 2;
					calculateSliceRecursively(fromX, halfX, fromY, halfY);
					calculateSliceRecursively(halfX, toX, fromY, halfY);
					calculateSliceRecursively(fromX, halfX, halfY, toY);
					calculateSliceRecursively(halfX, toX, halfY, toY);
				}
			}
		}
	}

	private void calculateEveryPixel(int fromX, int toX, int fromY, int toY, boolean cardioidVisible,
			boolean period2BulbVisible) {
		for (int x = fromX; x < toX && running; x++) {
			for (int y = fromY; y < toY && running; y++) {
				calculateIterations(x, y, cardioidVisible, period2BulbVisible);
			}
		}
	}

	private void setEveryPixel(int fromX, int toX, int fromY, int toY, int value) {
		for (int x = fromX; x < toX && running; x++) {
			for (int y = fromY; y < toY && running; y++) {
				dataBuffer.setPixel(x, y, value);
			}
		}
	}

	private int calculateIterations(int x, int y, boolean cardioidVisible, boolean period2BulbVisible) {
		int iterations = dataBuffer.getPixel(x, y);
		if (iterations == DataBuffer.NOT_CALCULATED) {
			double cReal = pointsInfo.toCReal(x);
			double cImaginary = pointsInfo.toCImaginary(y);
			iterations = calculateIterations(cReal, cImaginary, cardioidVisible, period2BulbVisible);
			dataBuffer.setPixel(x, y, iterations);
		}
		return iterations;
	}

	private int calculateIterations(double cReal, double cImaginary, boolean cardioidVisible,
			boolean period2BulbVisible) {
		int iterations;
		if (cardioidVisible
				&& (inCardioid(cReal, cImaginary) || period2BulbVisible && inPeriod2Bulb(cReal, cImaginary))) {
			iterations = MandelbrotCalculator.MAX_ITERATIONS;
		} else {
			iterations = calculateIterationsImpl(cReal, cImaginary, MandelbrotCalculator.MAX_ITERATIONS);
		}
		return iterations;
	}

	private boolean inCardioid(double x, double y) {
		double xMinusQuarter = x - 0.25d;
		double y2 = y * y;
		double q = xMinusQuarter * xMinusQuarter + y2;
		return q * (q + xMinusQuarter) <= .25 * y2;
	}

	private boolean inPeriod2Bulb(double x, double y) {
		double xPlusOne = x + 1.0d;
		return xPlusOne * xPlusOne + y * y <= 0.0625d;
	}

	private int calculateIterationsImpl(double cReal, double cImaginary, int maxIterations) {
		double real = 0;
		double imaginary = 0;
		double tempReal;

		double real2;
		double imaginary2;

		double realOld = 0.0d;
		double imaginaryOld = 0.0d;
		int period = 0;

		for (int iteration = 0; iteration < maxIterations; iteration++) {
			real2 = real * real;
			imaginary2 = imaginary * imaginary;

			if (real2 + imaginary2 > 4.0) {
				return iteration;
			}

			// Z=Z*Z+C
			tempReal = real2 - imaginary2 + cReal;
			imaginary = (real + real) * imaginary + cImaginary; // 2.0 * real * imaginary
			real = tempReal;

			if (real == realOld && imaginary == imaginaryOld) {
				return maxIterations;
			}

			period++;
			if (period > 20) {
				period = 0;
				realOld = real;
				imaginaryOld = imaginary;
			}
		}

		return maxIterations;

	}

}
