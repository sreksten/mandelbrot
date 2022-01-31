package com.threeamigos.mandelbrot.implementations.service.mandelbrot;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;
import com.threeamigos.mandelbrot.interfaces.service.MandelbrotService;
import com.threeamigos.mandelbrot.interfaces.service.Points;
import com.threeamigos.mandelbrot.interfaces.service.SchedulerService;

public class MultithreadedMandelbrotService implements MandelbrotService {

	private final PropertyChangeSupport propertyChangeSupport;

	private final SchedulerService schedulerService;
	private final int priority;
	private int maxThreads;
	private int maxIterations;

	private long drawTime;
	private CalculationService calculationService;

	public MultithreadedMandelbrotService(CalculationParameters calculationParameters,
			SchedulerService schedulerService, int priority) {
		this.schedulerService = schedulerService;
		this.priority = priority;
		propertyChangeSupport = new PropertyChangeSupport(this);
		maxThreads = calculationParameters.getMaxThreads();
		maxIterations = calculationParameters.getMaxIterations();
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

	@Override
	public void calculate(Points points) {

		if (calculationService != null) {
			calculationService.stopCalculation();
		}

		calculationService = new CalculationService(maxThreads, maxIterations, points.copy(), schedulerService,
				priority);

		calculationService.startCalculation();

		while (calculationService.isRunning()) {
			if (calculationService.shouldUpdatePercentage()) {
				propertyChangeSupport.firePropertyChange(CALCULATION_IN_PROGRESS_PROPERTY_CHANGE, null,
						calculationService.getPercentage());
			}
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		drawTime = calculationService.getCalculationTime();

		if (!calculationService.isInterrupted()) {
			propertyChangeSupport.firePropertyChange(CALCULATION_COMPLETE_PROPERTY_CHANGE, null, this);
		}
	}

	@Override
	public void interruptCalculation() {
		calculationService.globalRunning = false;
	}

	@Override
	public boolean isCalculating() {
		return calculationService.isRunning();
	}

	@Override
	public long getDrawTime() {
		return drawTime;
	}

	@Override
	public int[] getIterations() {
		return calculationService.pixelBuffer.getPixels();
	}

	@Override
	public int getIterations(int x, int y) {
		return calculationService.pixelBuffer.getPixel(x, y);
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
