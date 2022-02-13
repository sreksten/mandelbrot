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
	final SliceDeque deque;
	final IterationsBuffer iterationsBuffer;
	final SliceCalculator[] calculators;
	final PercentageTracker percentageTracker;
	final FractalType fractalType;
	boolean isJuliaConnected;

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
		this.deque = new SliceDeque();
		this.fractalType = points.getFractalType();

		int width = points.getWidth();
		int height = points.getHeight();

		percentageTracker = new PercentageTracker(width, height);

		prepareSlices(width, height);

		if (fractalType == FractalType.JULIA) {
			isJuliaConnected = points.isJuliaConnected();
		}

		this.iterationsBuffer = new IterationsBufferImpl(width, height);
		calculators = new SliceCalculator[maxThreads];
		globalRunning = true;
	}

	void prepareSlices(int width, int height) {

		deque.clear();

		if (points.getMinY() < 0 && 0 < points.getMaxY()) {
			// A part of the image may be symmetric

			if (points.getFractalType() == FractalType.MANDELBROT) {
				// A part of the image IS symmetric

				double imaginaryInterval = points.getMaxY() - points.getMinY();

				int upperHeight = 0;
				int symmetricHeight = (int) (height * Math.min(-points.getMinY(), points.getMaxY())
						/ imaginaryInterval);
				int lowerHeight = 0;

				if (-points.getMinY() > points.getMaxY()) {
					upperHeight = height - 2 * symmetricHeight;
				} else if (-points.getMinY() < points.getMaxY()) {
					lowerHeight = height - 2 * symmetricHeight;
				}

				if (upperHeight > 0) {
					deque.add(new Slice(0, 0, width, upperHeight));
				}

				// This is symmetric
				Slice s1 = new Slice(0, upperHeight, width, upperHeight + symmetricHeight);
				s1.symmetricity = Slice.SimmetricityType.X_AXIS;
				s1.originX = 0;
				s1.originY = upperHeight + symmetricHeight;
				deque.add(s1);

				if (lowerHeight > 0) {
					deque.add(new Slice(0, symmetricHeight * 2, width, height));
				}

				return;

			} else if (points.getFractalType() == FractalType.JULIA && points.getMinX() < 0 && 0 < points.getMaxX()) {
				// A part of the image IS symmetric

				double realInterval = points.getMaxX() - points.getMinX();
				double imaginaryInterval = points.getMaxY() - points.getMinY();

				int upperHeight = 0;
				int symmetricHeight = (int) (height * Math.min(-points.getMinY(), points.getMaxY())
						/ imaginaryInterval);
				int lowerHeight = 0;

				if (-points.getMinY() > points.getMaxY()) {
					upperHeight = height - 2 * symmetricHeight;
				} else if (-points.getMinY() < points.getMaxY()) {
					lowerHeight = height - 2 * symmetricHeight;
				}

				int leftWidth = 0;
				int symmetricWidth = (int) (width * Math.min(-points.getMinX(), points.getMaxX()) / realInterval);
				int rightWidth = 0;

				if (-points.getMinX() > points.getMaxX()) {
					leftWidth = width - 2 * symmetricWidth;
				} else if (-points.getMinX() < points.getMaxX()) {
					rightWidth = width - 2 * symmetricWidth;
				}

				if (upperHeight > 0) {
					deque.add(new Slice(0, 0, width, upperHeight));
				}

				if (leftWidth > 0) {
					deque.add(new Slice(0, upperHeight, leftWidth, upperHeight + symmetricHeight * 2));
				}

				// This is symmetric
				Slice s1 = new Slice(leftWidth, upperHeight, leftWidth + symmetricWidth, upperHeight + symmetricHeight);
				s1.symmetricity = Slice.SimmetricityType.ORIGIN;
				s1.originX = leftWidth + symmetricWidth;
				s1.originY = upperHeight + symmetricHeight;
				deque.add(s1);

				// This is symmetric
				Slice s2 = new Slice(leftWidth + symmetricWidth, upperHeight, leftWidth + symmetricWidth * 2,
						upperHeight + symmetricHeight);
				s2.symmetricity = Slice.SimmetricityType.ORIGIN;
				s2.originX = leftWidth + symmetricWidth;
				s2.originY = upperHeight + symmetricHeight;
				deque.add(s2);

				if (rightWidth > 0) {
					deque.add(new Slice(2 * symmetricWidth, upperHeight, width, upperHeight + symmetricHeight * 2));
				}

				if (lowerHeight > 0) {
					deque.add(new Slice(0, symmetricHeight * 2, width, height));
				}

				return;

			}
		}

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
				deque.add(new Slice(startX, startY, endX, endY));
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
				Slice slice = deque.remove();
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

	private SliceCalculator createSliceCalculator(Slice slice) {
		if (fractalType == FractalType.JULIA) {
			if (isJuliaConnected) {
				return new ConnectedJuliaSliceCalculator(points, slice, this, maxIterations);
			} else {
				return new NotConnectedJuliaSliceCalculator(points, slice, this, maxIterations);
			}
		} else {
			return new MandelbrotSliceCalculator(points, slice, this, maxIterations);
		}
	}

	long getCalculationTime() {
		return calculationTime;
	}

}
