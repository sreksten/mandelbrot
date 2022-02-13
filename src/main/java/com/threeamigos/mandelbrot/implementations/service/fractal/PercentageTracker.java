package com.threeamigos.mandelbrot.implementations.service.fractal;

public class PercentageTracker {

	private static final int INTERVAL_IN_MILLIS = 1000;

	private int totalPointsToCalculate;
	private long lastNotificationMillis;
	private int percentage;

	public PercentageTracker(int width, int height) {
		totalPointsToCalculate = width * height;
		lastNotificationMillis = System.currentTimeMillis();
	}

	public boolean shouldUpdatePercentage(SliceDeque deque) {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastNotificationMillis >= INTERVAL_IN_MILLIS) {
			lastNotificationMillis = currentTime;
			percentage = getPercentage(deque);
			return true;
		}
		return false;
	}

	private int getPercentage(SliceDeque deque) {
		// Since a slice may be split up in parts, the percentage floats around the
		// actual percentage.
		// It is uselessly time consuming to calculate the correct percentage due to all
		// optimizations done in the calculation (symmetry, slice enlarging), so we
		// return just an estimate percentage.
		int totalMissingPoints = 0;
		for (Slice slice : deque.getSlices()) {
			totalMissingPoints += (slice.endX - slice.startX) * (slice.endY - slice.startY);
		}
		int p = 100 - (int) (100.0d * ((double) totalMissingPoints / totalPointsToCalculate));
		if (p > percentage) {
			percentage = p;
		}
		return percentage;
	}

	public int getPercentage() {
		return percentage;
	}

}
