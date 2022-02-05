package com.threeamigos.mandelbrot.implementations.service.fractal;

import com.threeamigos.mandelbrot.interfaces.service.FractalService;
import com.threeamigos.mandelbrot.interfaces.service.Points;

class JuliaSliceCalculator implements SliceCalculator {

	private Points points;
	private int startX;
	private int startY;
	private int endX;
	private int endY;
	private CalculationService calculationService;
	private PixelBuffer dataBuffer;
	private SliceDataDeque deque;
	private int maxIterations;
	private String name;
	private boolean localRunning;
	private double cr;
	private double ci;

	public JuliaSliceCalculator(Points points, SliceData slice, CalculationService calculationService,
			int maxIterations) {
		this.points = points;
		this.cr = points.getJuliaCReal();
		this.ci = points.getJuliaCImaginary();
		this.startX = slice.startX;
		this.startY = slice.startY;
		this.endX = slice.endX;
		this.endY = slice.endY;
		this.calculationService = calculationService;
		this.dataBuffer = calculationService.pixelBuffer;
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
		calculateSliceRecursively(startX, endX, startY, endY);
		localRunning = false;
	}

	private void calculateSliceRecursively(int fromX, int toX, int fromY, int toY) {
		if (!calculationService.globalRunning) {
			return;
		}
		if (toX - fromX <= 5 || toY - fromY <= 5) {
			calculateEveryPixel(fromX, toX, fromY, toY);
		} else {
			if (calculationService.globalRunning) {
				int diffX = toX - fromX;
				int diffY = toY - fromY;
				int halfX = fromX + diffX / 2;
				int halfY = fromY + diffY / 2;
				if (diffX > 80 && diffY > 80) {
					deque.add(new SliceData(fromX, fromY, halfX, halfY));
					deque.add(new SliceData(halfX, fromY, toX, halfY));
					deque.add(new SliceData(fromX, halfY, halfX, toY));
					deque.add(new SliceData(halfX, halfY, toX, toY));
				} else {
					calculateSliceRecursively(fromX, halfX, fromY, halfY);
					calculateSliceRecursively(halfX, toX, fromY, halfY);
					calculateSliceRecursively(fromX, halfX, halfY, toY);
					calculateSliceRecursively(halfX, toX, halfY, toY);
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
		int iterations = dataBuffer.getPixel(x, y);
		if (iterations == FractalService.ITERATION_NOT_CALCULATED) {
			double cReal = points.toCReal(x);
			double cImaginary = points.toCImaginary(y);
			iterations = calculateIterationsImpl(cReal, cImaginary);
			dataBuffer.setPixel(x, y, iterations);
		}
		return iterations;
	}

	private int calculateIterationsImpl(double cReal, double cImaginary) {
		double real = cReal;
		double imaginary = cImaginary;
		double tempReal;

		double real2;
		double imaginary2;

		for (int iteration = 0; iteration < maxIterations && calculationService.globalRunning; iteration++) {
			real2 = real * real;
			imaginary2 = imaginary * imaginary;

			if (real2 + imaginary2 > 4.0) {
				return iteration;
			}

			// Z=Z*Z+C
			tempReal = real2 - imaginary2 + cr;
			imaginary = (real + real) * imaginary + ci; // 2.0 * real * imaginary
			real = tempReal;
		}

		return maxIterations;

	}

}
