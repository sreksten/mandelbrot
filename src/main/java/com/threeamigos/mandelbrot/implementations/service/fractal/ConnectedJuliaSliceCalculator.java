package com.threeamigos.mandelbrot.implementations.service.fractal;

import com.threeamigos.mandelbrot.interfaces.service.FractalService;
import com.threeamigos.mandelbrot.interfaces.service.Points;

public class ConnectedJuliaSliceCalculator implements SliceCalculator {

	private Points points;
	private Slice slice;
	private CalculationService calculationService;
	private IterationsBuffer dataBuffer;
	private SliceDeque deque;
	private int maxIterations;
	private String name;
	private boolean localRunning;
	private double cr;
	private double ci;

	public ConnectedJuliaSliceCalculator(Points points, Slice slice, CalculationService calculationService,
			int maxIterations) {
		this.points = points;
		this.cr = points.getJuliaCReal();
		this.ci = points.getJuliaCImaginary();
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

		if (endX - startX <= 5 || endY - startY <= 5) {
			calculateEveryPixel(startX, endX, startY, endY);
			if (level == 0) {
				slice.replicate(dataBuffer);
			}
		} else {
			int uniqueValue = FractalService.ITERATION_NOT_CALCULATED;
			boolean hasUniqueValue = true;

			for (int x = startX; x < endX && calculationService.globalRunning; x++) {
				int iterations = calculateIterations(x, startY);
				if (uniqueValue == FractalService.ITERATION_NOT_CALCULATED) {
					uniqueValue = iterations;
				} else if (uniqueValue != iterations) {
					hasUniqueValue = false;
				}
			}

			for (int y = startY; y < endY && calculationService.globalRunning; y++) {
				int iterations = calculateIterations(startX, y);
				if (uniqueValue == FractalService.ITERATION_NOT_CALCULATED) {
					uniqueValue = iterations;
				} else if (uniqueValue != iterations) {
					hasUniqueValue = false;
				}
			}

			for (int x = startX; x < endX && calculationService.globalRunning; x++) {
				int iterations = calculateIterations(x, endY - 1);
				if (uniqueValue == FractalService.ITERATION_NOT_CALCULATED) {
					uniqueValue = iterations;
				} else if (uniqueValue != iterations) {
					hasUniqueValue = false;
				}
			}

			for (int y = startY; y < endY && calculationService.globalRunning; y++) {
				int iterations = calculateIterations(endX - 1, y);
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

	private void calculateEveryPixel(int fromX, int toX, int fromY, int toY) {
		for (int x = fromX; x < toX && calculationService.globalRunning; x++) {
			for (int y = fromY; y < toY && calculationService.globalRunning; y++) {
				calculateIterations(x, y);
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

	private int calculateIterations(int x, int y) {
		int iterations = dataBuffer.getIterations(x, y);
		if (iterations == FractalService.ITERATION_NOT_CALCULATED) {
			double cReal = points.toCReal(x);
			double cImaginary = points.toCImaginary(y);
			iterations = calculateIterations(cReal, cImaginary);
			dataBuffer.setPixel(x, y, iterations);
		}
		return iterations;
	}

	private int calculateIterations(double cReal, double cImaginary) {
		double real = cReal;
		double imaginary = cImaginary;
		double tempReal;

		double real2;
		double imaginary2;

		double realOld = 0.0d;
		double imaginaryOld = 0.0d;
		int period = 0;

		for (int iteration = 0; iteration < maxIterations && calculationService.globalRunning; iteration++) {
			real2 = real * real;
			imaginary2 = imaginary * imaginary;

			if (real2 + imaginary2 > 4.0d) {
				return iteration;
			}

			// Z=Z*Z+C
			tempReal = real2 - imaginary2 + cr;
			imaginary = (real + real) * imaginary + ci; // 2 * real * imaginary
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
