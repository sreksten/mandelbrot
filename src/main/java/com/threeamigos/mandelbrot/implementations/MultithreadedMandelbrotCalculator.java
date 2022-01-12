package com.threeamigos.mandelbrot.implementations;

import java.util.Arrays;

import com.threeamigos.mandelbrot.interfaces.DataBuffer;
import com.threeamigos.mandelbrot.interfaces.MandelbrotCalculator;
import com.threeamigos.mandelbrot.interfaces.PointsInfo;

public class MultithreadedMandelbrotCalculator implements MandelbrotCalculator {

	private long drawTime;

	int cores;
	private Thread[] threads;
	private MandelbrotSliceCalculator[] calculators;
	private SliceDataDeque deque;

	boolean running;

	public MultithreadedMandelbrotCalculator() {
		cores = Runtime.getRuntime().availableProcessors();
		threads = new Thread[cores];
		calculators = new MandelbrotSliceCalculator[cores];
		deque = SliceDataDeque.getInstance();
	}

	@Override
	public int getNumberOfThreads() {
		return cores;
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
	public void calculate(PointsInfo pointsInfo, DataBuffer dataBuffer) {

		running = true;

		dataBuffer.clear();

		prepareSlices(pointsInfo);

		long startMillis = System.currentTimeMillis();

		while (running && !finished()) {
			for (int i = 0; i < cores && !deque.isEmpty(); i++) {
				Thread thread = threads[i];
				if (thread == null || !thread.isAlive()) {
					SliceData slice = deque.remove();
					calculators[i] = new MandelbrotSliceCalculator(Thread.currentThread(), pointsInfo, slice,
							dataBuffer);
					thread = new Thread(calculators[i]);
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

		for (int i = 0; i < cores; i++) {
			threads[i] = null;
		}

		drawTime = (endMillis - startMillis);

	}

	private void prepareSlices(PointsInfo pointsInfo) {
		int horizontalSlices = 8;
		int verticalSlices = 8;

		int width = pointsInfo.getWidth();
		int height = pointsInfo.getHeight();

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

}
