package com.threeamigos.mandelbrot.interfaces.service;

public class CalculationThreadRunnerService implements Runnable {

	private MandelbrotService mandelbrotService;
	private Thread calculationThread;
	private boolean calculationThreadRunning = false;
	private PointsInfo pointsInfo;
	private long lastDrawTime;

	public CalculationThreadRunnerService(MandelbrotService mandelbrotService, PointsInfo pointsInfo) {
		this.mandelbrotService = mandelbrotService;
		this.pointsInfo = pointsInfo;
	}

	public void start() {
		if (calculationThreadRunning) {
			calculationThread.interrupt();
			try {
				calculationThread.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		calculationThreadRunning = true;
		calculationThread = new Thread(this);
		calculationThread.setDaemon(true);
		calculationThread.start();
	}

	@Override
	public void run() {
		mandelbrotService.calculate(pointsInfo);
		lastDrawTime = mandelbrotService.getDrawTime();
		calculationThreadRunning = false;
	}

	public long getLastDrawTime() {
		return lastDrawTime;
	}

}
