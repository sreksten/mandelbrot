package com.threeamigos.mandelbrot.interfaces.service;

public interface ImageProducerServiceFactory {

	public ImageProducerService createInstance(CalculationParameters calculationParameters);

}
