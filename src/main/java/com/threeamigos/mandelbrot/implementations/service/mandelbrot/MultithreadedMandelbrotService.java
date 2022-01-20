package com.threeamigos.mandelbrot.implementations.service.mandelbrot;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;

import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;
import com.threeamigos.mandelbrot.interfaces.service.MandelbrotService;
import com.threeamigos.mandelbrot.interfaces.service.PointsInfo;

public class MultithreadedMandelbrotService implements MandelbrotService {

	private long drawTime;

	int maxThreads;
	int maxIterations;
	private Thread[] threads;
	private MandelbrotSliceCalculator[] calculators;
	private SliceDataDeque deque;
	private DataBuffer dataBuffer;
	private PropertyChangeSupport propertyChangeSupport;

	boolean running;
	boolean interrupted;

	public MultithreadedMandelbrotService(CalculationParameters calculationParameters) {
		this.maxThreads = calculationParameters.getMaxThreads();
		this.maxIterations = calculationParameters.getMaxIterations();
		threads = new Thread[maxThreads];
		calculators = new MandelbrotSliceCalculator[maxThreads];
		deque = SliceDataDeque.getInstance();
		propertyChangeSupport = new PropertyChangeSupport(this);
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
	public boolean incrementThreads() {
		if (maxThreads < Runtime.getRuntime().availableProcessors()) {
			maxThreads++;
			return true;
		}
		return false;
	}

	@Override
	public boolean decrementThreads() {
		if (maxThreads > 1) {
			maxThreads--;
			return true;
		}
		return false;
	}

	boolean finished() {
		for (Thread t : threads) {
			if (t != null && t.isAlive()) {
				return false;
			}
		}
		if (!deque.isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public void calculate(PointsInfo pointsInfo) {

		int width = pointsInfo.getWidth();
		int height = pointsInfo.getHeight();

		running = true;
		interrupted = false;

		dataBuffer = new DataBufferImpl(width, height);

		prepareSlices(pointsInfo, width, height);

		long startMillis = System.currentTimeMillis();

		while (running && !finished()) {
			for (int i = 0; i < maxThreads && !deque.isEmpty(); i++) {
				Thread thread = threads[i];
				if (thread == null || !thread.isAlive()) {
					SliceData slice = deque.remove();
					calculators[i] = new MandelbrotSliceCalculator(Thread.currentThread(), pointsInfo, slice,
							dataBuffer, maxIterations);
					thread = new Thread(calculators[i]);
					thread.setDaemon(true);
					threads[i] = thread;
					thread.start();
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// Thread.currentThread().interrupt();
			}
		}

		joinThreads();

		long endMillis = System.currentTimeMillis();

		for (int i = 0; i < maxThreads; i++) {
			threads[i] = null;
		}

		drawTime = (endMillis - startMillis);

		if (!interrupted) {
			propertyChangeSupport.firePropertyChange(CALCULATION_COMPLETE_PROPERTY_CHANGE, null, this);
		}
	}

	private void prepareSlices(PointsInfo pointsInfo, int width, int height) {
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
		running = false;
		interrupted = true;
		for (MandelbrotSliceCalculator calculator : calculators) {
			if (calculator != null) {
				calculator.stop();
			}
		}
		joinThreads();
		Arrays.fill(threads, null);
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
