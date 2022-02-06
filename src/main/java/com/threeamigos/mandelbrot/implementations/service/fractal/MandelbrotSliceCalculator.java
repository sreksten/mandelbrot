package com.threeamigos.mandelbrot.implementations.service.fractal;

import com.threeamigos.mandelbrot.interfaces.service.FractalService;
import com.threeamigos.mandelbrot.interfaces.service.Points;

class MandelbrotSliceCalculator implements SliceCalculator {

	private Points points;
	private Slice slice;
	private CalculationService calculationService;
	private IterationsBuffer dataBuffer;
	private SliceDeque deque;
	private int maxIterations;
	private String name;
	private boolean localRunning;

	public MandelbrotSliceCalculator(Points points, Slice slice, CalculationService calculationService,
			int maxIterations) {
		this.points = points;
		this.slice = slice;
		this.calculationService = calculationService;
		this.dataBuffer = calculationService.iterationsBuffer;
		this.deque = calculationService.deque;
		this.maxIterations = maxIterations;

		localRunning = true;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean isAlive() {
		return localRunning;
	}

	@Override
	public void run() {
		calculateSliceRecursively(slice, 0);
		localRunning = false;
	}

	private void calculateSliceRecursively(Slice slice, int level) {
		if (!calculationService.globalRunning) {
			return;
		}
		int startX = slice.startX;
		int startY = slice.startY;
		int endX = slice.endX;
		int endY = slice.endY;
		boolean cardioidVisible = isCardioidVisible(startX, endX, startY, endY);
		boolean period2BulbVisible = isPeriod2BulbVisible(startX, endX, startY, endY);
		if (endX - startX <= 5 || endY - startY <= 5) {
			calculateEveryPixel(startX, endX, startY, endY, cardioidVisible, period2BulbVisible);
			if (level == 0) {
				slice.replicate(dataBuffer);
			}
		} else {
			int uniqueValue = FractalService.ITERATION_NOT_CALCULATED;
			boolean hasUniqueValue = true;

			for (int x = startX; x < endX && calculationService.globalRunning; x++) {
				int iterations = calculateIterations(x, startY, cardioidVisible, period2BulbVisible);
				if (uniqueValue == FractalService.ITERATION_NOT_CALCULATED) {
					uniqueValue = iterations;
				} else if (uniqueValue != iterations) {
					hasUniqueValue = false;
				}
			}

			for (int y = startY; y < endY && calculationService.globalRunning; y++) {
				int iterations = calculateIterations(startX, y, cardioidVisible, period2BulbVisible);
				if (uniqueValue == FractalService.ITERATION_NOT_CALCULATED) {
					uniqueValue = iterations;
				} else if (uniqueValue != iterations) {
					hasUniqueValue = false;
				}
			}

			for (int x = startX; x < endX && calculationService.globalRunning; x++) {
				int iterations = calculateIterations(x, endY - 1, cardioidVisible, period2BulbVisible);
				if (uniqueValue == FractalService.ITERATION_NOT_CALCULATED) {
					uniqueValue = iterations;
				} else if (uniqueValue != iterations) {
					hasUniqueValue = false;
				}
			}

			for (int y = startY; y < endY && calculationService.globalRunning; y++) {
				int iterations = calculateIterations(endX - 1, y, cardioidVisible, period2BulbVisible);
				if (uniqueValue == FractalService.ITERATION_NOT_CALCULATED) {
					uniqueValue = iterations;
				} else if (uniqueValue != iterations) {
					hasUniqueValue = false;
				}
			}

			if (calculationService.globalRunning) {
				if (hasUniqueValue) {
					setEveryPixel(startX + 1, endX - 1, startY + 1, endY - 1, uniqueValue);
					if (level == 0) {
						slice.replicate(dataBuffer);
					}
				} else {
					int diffX = endX - startX;
					int diffY = endY - startY;

					if (diffX > 80 && diffY > 80) {
						for (Slice data : slice.split()) {
							deque.add(data);
						}
					} else {
						for (Slice data : slice.split()) {
							calculateSliceRecursively(data, level + 1);
						}
						if (level == 0) {
							slice.replicate(dataBuffer);
						}
					}
				}
			}
		}
	}

	private void calculateEveryPixel(int fromX, int toX, int fromY, int toY, boolean cardioidVisible,
			boolean period2BulbVisible) {
		for (int x = fromX; x < toX && calculationService.globalRunning; x++) {
			for (int y = fromY; y < toY && calculationService.globalRunning; y++) {
				calculateIterations(x, y, cardioidVisible, period2BulbVisible);
			}
		}
	}

	private void setEveryPixel(int fromX, int toX, int fromY, int toY, int value) {
		for (int x = fromX; x < toX; x++) {
			for (int y = fromY; y < toY; y++) {
				dataBuffer.setPixel(x, y, value);
			}
		}
	}

	private int calculateIterations(int x, int y, boolean cardioidVisible, boolean period2BulbVisible) {
		int iterations = dataBuffer.getIterations(x, y);
		if (iterations == FractalService.ITERATION_NOT_CALCULATED) {
			double cReal = points.toCReal(x);
			double cImaginary = points.toCImaginary(y);
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
			iterations = maxIterations;
		} else {
			iterations = calculateIterationsImpl(cReal, cImaginary, maxIterations);
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

		for (int iteration = 0; iteration < maxIterations && calculationService.globalRunning; iteration++) {
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

	private boolean isCardioidVisible(int fromX, int toX, int fromY, int toY) {
		double fromCReal = points.toCReal(fromX);
		double toCReal = points.toCReal(toX);
		double fromCImaginary = points.toCImaginary(fromY);
		double toCImaginary = points.toCImaginary(toY);
		return intervalsOverlap(fromCReal, toCReal, -0.75d, 0.375d)
				&& intervalsOverlap(fromCImaginary, toCImaginary, -0.65d, 0.65d);
	}

	private boolean isPeriod2BulbVisible(int fromX, int toX, int fromY, int toY) {
		double fromCReal = points.toCReal(fromX);
		double toCReal = points.toCReal(toX);
		double fromCImaginary = points.toCImaginary(fromY);
		double toCImaginary = points.toCImaginary(toY);
		return intervalsOverlap(fromCReal, toCReal, -1.25d, -0.75d)
				&& intervalsOverlap(fromCImaginary, toCImaginary, -0.25d, 0.25d);
	}

	private boolean intervalsOverlap(double firstStart, double firstEnd, double secondStart, double secondEnd) {
		return firstEnd >= secondStart && firstStart <= secondEnd;

	}

}
