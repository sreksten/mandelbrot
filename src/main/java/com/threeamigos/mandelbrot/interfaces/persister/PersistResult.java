package com.threeamigos.mandelbrot.interfaces.persister;

/**
 * Result of a load or save operation
 *
 * @author Stefano Reksten
 *
 */
public interface PersistResult {

	public boolean isSuccessful();

	public String getFilename();

	public String getError();

}