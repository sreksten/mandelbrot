package com.threeamigos.mandelbrot.implementations.service.mandelbrot;

public class PercentageTracker {

	private static final int INTERVAL_IN_MILLIS = 1000;

	private int totalPointsToCalculate;
	private long lastNotificationMillis;

	public PercentageTracker(int width, int height) {
		totalPointsToCalculate = width * height;
		lastNotificationMillis = System.currentTimeMillis();
	}

	public boolean shouldUpdatePercentage() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastNotificationMillis >= INTERVAL_IN_MILLIS) {
			lastNotificationMillis = currentTime;
			return true;
		}
		return false;
	}

	public int getPercentage(SliceDataDeque deque) {
		int totalMissingPoints = 0;
		for (SliceData slice : deque.getDataSlices()) {
			totalMissingPoints += (slice.endX - slice.startX) * (slice.endY - slice.startY);
		}
		return 100 - 100 * totalMissingPoints / totalPointsToCalculate;
	}

}
