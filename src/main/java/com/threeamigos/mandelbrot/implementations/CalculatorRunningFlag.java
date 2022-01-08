package com.threeamigos.mandelbrot.implementations;

public class CalculatorRunningFlag {

	private static boolean running = false;

	private CalculatorRunningFlag() {
	}

	public static final void startRunning() {
		running = true;
	}

	public static final void stopRunning() {
		running = false;
	}

	public static final boolean isRunning() {
		return running;
	}

}
