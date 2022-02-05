package com.threeamigos.mandelbrot.implementations.service.fractal;

import com.threeamigos.mandelbrot.interfaces.service.FractalType;
import com.threeamigos.mandelbrot.interfaces.service.Points;
import com.threeamigos.mandelbrot.interfaces.service.SchedulerService;

class CalculationService implements Runnable {

	private static int requestCounter = 0;

	private final SchedulerService schedulerService;
	private final int priority;
	final int requestNumber;
	final int maxThreads;
	final int maxIterations;
	final Points points;
	final SliceDataDeque deque;
	final PixelBuffer pixelBuffer;
	final SliceCalculator[] calculators;
	final PercentageTracker percentageTracker;
	final FractalType fractalType;

	boolean globalRunning;
	private boolean interrupted = false;
	private long calculationTime = -1;

	CalculationService(int maxThreads, int maxIterations, Points points, SchedulerService schedulerService,
			int priority) {
		this.schedulerService = schedulerService;
		this.priority = priority;
		this.requestNumber = ++requestCounter;
		this.maxThreads = maxThreads;
		this.maxIterations = maxIterations;
		this.points = points;
		this.deque = new SliceDataDeque();
		this.fractalType = points.getFractalType();

		int width = points.getWidth();
		int height = points.getHeight();

		percentageTracker = new PercentageTracker(width, height);

		prepareSlices(width, height);

		this.pixelBuffer = new PixelBufferImpl(width, height);
		calculators = new SliceCalculator[maxThreads];
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
		Thread thread = new Thread(null, this, "CalculationService-" + requestNumber);
		thread.setDaemon(true);
		thread.start();
	}

	public void stopCalculation() {
		globalRunning = false;
		interrupted = true;
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

		globalRunning = false;

		if (!interrupted) {
			long endMillis = System.currentTimeMillis();
			calculationTime = (endMillis - startMillis);
		}
	}

	boolean shouldUpdatePercentage() {
		return percentageTracker.shouldUpdatePercentage(deque);
	}

	int getPercentage() {
		return percentageTracker.getPercentage();
	}

	boolean finished() {
		for (SliceCalculator calculator : calculators) {
			if (calculator != null && calculator.isAlive()) {
				return false;
			}
		}
		return deque.isEmpty();
	}

	void createNewCalculatorIfPossible() {
		for (int i = 0; i < maxThreads && !deque.isEmpty(); i++) {
			SliceCalculator calculator = calculators[i];
			if (calculator == null || !calculator.isAlive()) {
				SliceData slice = deque.remove();
				String name = "R" + requestNumber + "-T" + i;
				calculator = createSliceCalculator(slice);
				calculators[i] = calculator;
				schedulerService.schedule(Thread.currentThread(), calculator, priority, true, name);
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

	private SliceCalculator createSliceCalculator(SliceData slice) {
		if (fractalType == FractalType.JULIA) {
			return new JuliaSliceCalculator(points, slice, this, maxIterations);
		} else {
			return new MandelbrotSliceCalculator(points, slice, this, maxIterations);
		}
	}

	long getCalculationTime() {
		return calculationTime;
	}

}
