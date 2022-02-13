package com.threeamigos.mandelbrot.implementations.service.fractal;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;
import com.threeamigos.mandelbrot.interfaces.service.CalculationType;
import com.threeamigos.mandelbrot.interfaces.service.FractalService;
import com.threeamigos.mandelbrot.interfaces.service.Points;
import com.threeamigos.mandelbrot.interfaces.service.SchedulerService;
import com.threeamigos.mandelbrot.interfaces.ui.Resolution;

public class MultithreadedFractalService implements FractalService {

	private final SchedulerService schedulerService;
	private final PropertyChangeSupport propertyChangeSupport;

	private long calculationTime;
	private CalculationService calculationService;

	public MultithreadedFractalService(SchedulerService schedulerService) {
		this.schedulerService = schedulerService;
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	@Override
	public void calculate(Points points, Resolution resolution, CalculationParameters calculationParameters) {

		interruptCalculation();

		int maxThreads = calculationParameters.getMaxThreads();
		int maxIterations = calculationParameters.getMaxIterations();
		CalculationType calculationType = calculationParameters.getCalculationType();

		calculationService = new CalculationService(maxThreads, maxIterations, points.copy(), schedulerService,
				calculationType.getPriority());

		calculationService.startCalculation();

		while (calculationService.isRunning()) {
			if (calculationService.shouldUpdatePercentage()) {
				propertyChangeSupport.firePropertyChange(calculationType.getCalculationInProgressEvent(), null,
						calculationService.getPercentage());
			}
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		calculationTime = calculationService.getCalculationTime();

		if (!calculationService.isInterrupted()) {
			propertyChangeSupport.firePropertyChange(calculationType.getCalculationCompleteEvent(), null, this);
		}
	}

	@Override
	public void interruptCalculation() {
		if (calculationService != null) {
			calculationService.stopCalculation();
		}
	}

	@Override
	public boolean isCalculating() {
		return calculationService.isRunning();
	}

	@Override
	public int getPercentage() {
		return calculationService.getPercentage();
	}

	@Override
	public long getCalculationTime() {
		return calculationTime;
	}

	@Override
	public int[] getIterations() {
		return calculationService.iterationsBuffer.getIterations();
	}

	@Override
	public int getIterations(int x, int y) {
		return calculationService.iterationsBuffer.getIterations(x, y);
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
