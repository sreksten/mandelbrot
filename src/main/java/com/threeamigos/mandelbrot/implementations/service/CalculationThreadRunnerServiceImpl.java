package com.threeamigos.mandelbrot.implementations.service;

import com.threeamigos.mandelbrot.interfaces.service.CalculationThreadRunnerService;
import com.threeamigos.mandelbrot.interfaces.service.MandelbrotService;
import com.threeamigos.mandelbrot.interfaces.service.Points;

public class CalculationThreadRunnerServiceImpl implements Runnable, CalculationThreadRunnerService {

	private MandelbrotService mandelbrotService;
	private Thread calculationThread;
	private boolean calculationThreadRunning = false;
	private Points pointsInfo;
	private long lastDrawTime;

	public CalculationThreadRunnerServiceImpl(MandelbrotService mandelbrotService, Points pointsInfo) {
		this.mandelbrotService = mandelbrotService;
		this.pointsInfo = pointsInfo;
	}

	@Override
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

	@Override
	public long getLastDrawTime() {
		return lastDrawTime;
	}

}
