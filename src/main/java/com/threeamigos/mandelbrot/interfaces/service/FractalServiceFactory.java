package com.threeamigos.mandelbrot.interfaces.service;

/**
 * A factory used to obtain {@link FractalService}s
 *
 * @author Stefano Reksten
 *
 */
public interface FractalServiceFactory {

	public FractalService createInstance();

}
