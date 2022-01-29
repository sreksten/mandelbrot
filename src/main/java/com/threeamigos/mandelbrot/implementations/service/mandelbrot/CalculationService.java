package com.threeamigos.mandelbrot.implementations.service.mandelbrot;

import com.threeamigos.mandelbrot.interfaces.service.Points;

class CalculationService implements Runnable {

	private static int requestCounter = 0;

	final int requestNumber;
	final int maxThreads;
	final int maxIterations;
	final Points points;
	final SliceDataDeque deque;
	final PixelBuffer pixelBuffer;
	final MandelbrotSliceCalculator[] calculators;
	final PercentageTracker percentageTracker;

	boolean globalRunning;
	private boolean interrupted = false;
	private long calculationTime = -1;

	CalculationService(int maxThreads, int maxIterations, Points points) {
		this.requestNumber = ++requestCounter;
		this.maxThreads = maxThreads;
		this.maxIterations = maxIterations;
		this.points = points;
		this.deque = new SliceDataDeque();

		int width = points.getWidth();
		int height = points.getHeight();

		percentageTracker = new PercentageTracker(width, height);

		prepareSlices(width, height);

		this.pixelBuffer = new PixelBufferImpl(width, height);
		calculators = new MandelbrotSliceCalculator[Runtime.getRuntime().availableProcessors()];
		globalRunning = true;
	}

	void prepareSlices(int width, int height) {

		deque.clear();

		int horizontalSlices = 8;
		int verticalSlices = 8;

		int sliceWidth = width / horizontalSlices;
		int sliceHeight = height / verticalSlices;

		for (int i = 0; i < horizontalSlices; i++) {
			for (int j = 0; j < verticalSlices; j++) {
				int startX = i * sliceWidth;
				int endX;
				if (i < horizontalSlices - 1) {
					endX = startX + sliceWidth;
				} else {
					endX = width;
				}
				int startY = j * sliceHeight;
				int endY;
				if (j < verticalSlices - 1) {
					endY = startY + sliceHeight;
				} else {
					endY = height;
				}
				deque.add(new SliceData(startX, startY, endX, endY));
			}
		}
	}

	public void startCalculation() {
		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}

	public void stopCalculation() {
		globalRunning = false;
		interrupted = true;
		joinCalculationThreads();
	}

	public boolean isRunning() {
		return globalRunning;
	}

	public boolean isInterrupted() {
		return interrupted;
	}

	@Override
	public void run() {
		long startMillis = System.currentTimeMillis();

		while (globalRunning && !finished()) {
			createNewCalculatorIfPossible();
			waitForAnyCalculatorToFinish();
		}

		joinCalculationThreads();

		globalRunning = false;

		if (!interrupted) {
			long endMillis = System.currentTimeMillis();
			calculationTime = (endMillis - startMillis);
		}
	}

	boolean shouldUpdatePercentage() {
		return percentageTracker.shouldUpdatePercentage();
	}

	int getPercentage() {
		return percentageTracker.getPercentage(deque);
	}

	boolean finished() {
		for (MandelbrotSliceCalculator calculator : calculators) {
			if (calculator != null && calculator.isAlive()) {
				return false;
			}
		}
		return deque.isEmpty();
	}

	void createNewCalculatorIfPossible() {
		for (int i = 0; i < maxThreads && !deque.isEmpty(); i++) {
			MandelbrotSliceCalculator calculator = calculators[i];
			if (calculator == null || !calculator.isAlive()) {
				SliceData slice = deque.remove();
				calculators[i] = new MandelbrotSliceCalculator(Thread.currentThread(), points, slice, this,
						maxIterations, "R" + requestNumber + "-T" + i);
			}
		}
	}

	void waitForAnyCalculatorToFinish() {
		// Any thread that finishes will interrupt the main thread (this).
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	void joinCalculationThreads() {
		for (int i = 0; i < calculators.length; i++) {
			MandelbrotSliceCalculator calculator = calculators[i];
			if (calculator != null) {
				try {
					calculator.join();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	long getCalculationTime() {
		return calculationTime;
	}
}
