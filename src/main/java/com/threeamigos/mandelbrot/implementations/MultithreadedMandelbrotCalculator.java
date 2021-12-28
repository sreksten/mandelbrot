package com.threeamigos.mandelbrot.implementations;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.threeamigos.mandelbrot.interfaces.DataBuffer;
import com.threeamigos.mandelbrot.interfaces.MandelbrotCalculator;
import com.threeamigos.mandelbrot.interfaces.PointsInfo;

public class MultithreadedMandelbrotCalculator implements MandelbrotCalculator {

	private int calculationNumber = 0;

	private long drawTime;

	int cores;
	private Thread[] threads;
	private MandelbrotSliceCalculator[] calculators;

	boolean running;

	public MultithreadedMandelbrotCalculator() {
		cores = Runtime.getRuntime().availableProcessors();
		threads = new Thread[cores];
		calculators = new MandelbrotSliceCalculator[cores];
	}

	@Override
	public int getNumberOfThreads() {
		return cores;
	}

	@Override
	public void calculate(PointsInfo pointsInfo, DataBuffer dataBuffer) {

		calculationNumber++;

		running = true;

		dataBuffer.clear();

		List<Slice> slices = prepareSlices(pointsInfo);

		long startMillis = System.currentTimeMillis();

		while (running && !slices.isEmpty()) {
			for (int i = 0; i < cores && !slices.isEmpty(); i++) {
				Thread thread = threads[i];
				if (thread == null || !thread.isAlive()) {
					Slice slice = slices.remove(0); // NOSONAR
					calculators[i] = new MandelbrotSliceCalculator(pointsInfo, slice.startX, slice.startY, slice.endX,
							slice.endY, dataBuffer);
					thread = new Thread(calculators[i]);
					thread.setName("SliceCalculatorThread-" + calculationNumber + "-" + i);
					threads[i] = thread;
					thread.start();
				}
			}
		}

		joinThreads();

		long endMillis = System.currentTimeMillis();

		for (int i = 0; i < cores; i++) {
			threads[i] = null;
		}

		drawTime = (endMillis - startMillis);

	}

	private List<Slice> prepareSlices(PointsInfo pointsInfo) {
		int horizontalSlices = 8;
		int verticalSlices = 8;

		int width = pointsInfo.getWidth();
		int height = pointsInfo.getHeight();

		int sliceWidth = width / horizontalSlices;
		int sliceHeight = height / verticalSlices;

		List<Slice> slices = new LinkedList<>();
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
				slices.add(new Slice(startX, startY, endX, endY));
			}
		}
		return slices;
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

	private class Slice {
		int startX;
		int startY;
		int endX;
		int endY;

		Slice(int startX, int startY, int endX, int endY) {
			this.startX = startX;
			this.startY = startY;
			this.endX = endX;
			this.endY = endY;
		}
	}
}
