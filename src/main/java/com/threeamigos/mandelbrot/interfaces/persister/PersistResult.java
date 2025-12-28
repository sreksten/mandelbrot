package com.threeamigos.mandelbrot.interfaces.persister;

/**
 * Result of a load or save operation
 *
 * @author Stefano Reksten
 *
 */
public interface PersistResult {

	boolean isSuccessful();

	String getFilename();

	String getError();

}