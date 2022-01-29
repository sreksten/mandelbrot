package com.threeamigos.mandelbrot.implementations.service.mandelbrot;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;

import com.threeamigos.mandelbrot.Resolution;
import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;
import com.threeamigos.mandelbrot.interfaces.service.MandelbrotService;
import com.threeamigos.mandelbrot.interfaces.service.Points;

public class MultithreadedMandelbrotService implements MandelbrotService {

	private final Resolution resolution;
	private final Thread[] threads;
	private final MandelbrotSliceCalculator[] calculators;
	private final SliceDataDeque deque;
	private final PropertyChangeSupport propertyChangeSupport;

	int maxThreads;
	int maxIterations;
	private DataBuffer dataBuffer;

	private long drawTime;
	boolean running;
	boolean interrupted;

	public MultithreadedMandelbrotService(CalculationParameters calculationParameters) {
		resolution = calculationParameters.getResolution();
		int cores = Runtime.getRuntime().availableProcessors();
		threads = new Thread[cores];
		calculators = new MandelbrotSliceCalculator[cores];
		deque = SliceDataDeque.getInstance();
		propertyChangeSupport = new PropertyChangeSupport(this);

		maxThreads = calculationParameters.getMaxThreads();
		maxIterations = calculationParameters.getMaxIterations();
		createDataBuffer();
	}

	private void createDataBuffer() {
		dataBuffer = new DataBufferImpl(resolution);
	}

	@Override
	public int getNumberOfThreads() {
		return maxThreads;
	}

	@Override
	public int getMaxIterations() {
		return maxIterations;
	}

	@Override
	public boolean doubleUpMaxIterations() {
		if (maxIterations < (1 << MAX_ITERATIONS_EXPONENT)) {
			maxIterations <<= 1;
			return true;
		}
		return false;
	}

	@Override
	public boolean halveMaxIterations() {
		if (maxIterations > (1 << MIN_ITERATIONS_EXPONENT)) {
			maxIterations >>= 1;
			return true;
		}
		return false;
	}

	@Override
	public boolean setMaxIterations(int maxIterations) {
		if (maxIterations >= (1 << MIN_ITERATIONS_EXPONENT) && maxIterations <= (1 << MAX_ITERATIONS_EXPONENT)) {
			this.maxIterations = maxIterations;
			return true;
		}
		return false;
	}

	@Override
	public boolean incrementNumberOfThreads() {
		if (maxThreads < Runtime.getRuntime().availableProcessors()) {
			maxThreads++;
			return true;
		}
		return false;
	}

	@Override
	public boolean decrementNumberOfThreads() {
		if (maxThreads > 1) {
			maxThreads--;
			return true;
		}
		return false;
	}

	@Override
	public boolean setNumberOfThreads(int numberOfThreads) {
		if (numberOfThreads <= Runtime.getRuntime().availableProcessors()) {
			maxThreads = numberOfThreads;
			return true;
		}
		return false;
	}

	private boolean finished() {
		for (Thread t : threads) {
			if (t != null && t.isAlive()) {
				return false;
			}
		}
		return deque.isEmpty();
	}

	@Override
	public void calculate(Points points) {

		running = true;
		interrupted = false;

		createDataBuffer();

		int width = points.getWidth();
		int height = points.getHeight();

		PercentageTracker percentageTracker = new PercentageTracker(width, height);

		prepareSlices(width, height);

		long startMillis = System.currentTimeMillis();

		while (running && !finished()) {
			if (percentageTracker.shouldUpdatePercentage()) {
				propertyChangeSupport.firePropertyChange(CALCULATION_IN_PROGRESS_PROPERTY_CHANGE, null,
						percentageTracker.getPercentage(deque));
			}
			createNewThreadIfPossible(points);
			waitForAnyThreadToFinish();
		}

		joinThreads();

		long endMillis = System.currentTimeMillis();

		drawTime = (endMillis - startMillis);

		if (!interrupted) {
			propertyChangeSupport.firePropertyChange(CALCULATION_COMPLETE_PROPERTY_CHANGE, null, this);
		}
	}

	private void createNewThreadIfPossible(Points points) {
		for (int i = 0; i < maxThreads && !deque.isEmpty(); i++) {
			Thread thread = threads[i];
			if (thread == null || !thread.isAlive()) {
				SliceData slice = deque.remove();
				calculators[i] = new MandelbrotSliceCalculator(Thread.currentThread(), points, slice, dataBuffer,
						maxIterations);
				thread = new Thread(calculators[i]);
				thread.setDaemon(true);
				threads[i] = thread;
				thread.start();
			}
		}
	}

	private void waitForAnyThreadToFinish() {
		// Any thread that finishes will interrupt the main thread (this).
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private void prepareSlices(int width, int height) {

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

	@Override
	public void interruptPreviousCalculation() {
		interrupted = true;
		for (MandelbrotSliceCalculator calculator : calculators) {
			if (calculator != null) {
				calculator.stop();
			}
		}
		joinThreads();
		running = false;
	}

	private void joinThreads() {
		for (Thread thread : threads) {
			if (thread != null) {
				try {
					thread.join();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
		Arrays.fill(threads, null);
	}

	@Override
	public boolean isCalculating() {
		return running;
	}

	@Override
	public long getDrawTime() {
		return drawTime;
	}

	@Override
	public int[] getIterations() {
		return dataBuffer.getPixels();
	}

	@Override
	public int getIterations(int x, int y) {
		return dataBuffer.getPixel(x, y);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		propertyChangeSupport.addPropertyChangeListener(pcl);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		propertyChangeSupport.removePropertyChangeListener(pcl);
	}

}
