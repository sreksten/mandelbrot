package com.threeamigos.mandelbrot.interfaces.service;

/**
 * A factory to create {@link ImageProducerService}s
 *
 * @author Stefano Reksten
 *
 */
public interface ImageProducerServiceFactory {

	public ImageProducerService createInstance(int maxIterations);

}
