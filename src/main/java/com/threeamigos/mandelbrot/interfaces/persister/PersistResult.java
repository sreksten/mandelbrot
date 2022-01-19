package com.threeamigos.mandelbrot.interfaces.persister;

public interface PersistResult {

	public boolean isSuccessful();

	public String getFilename();

	public String getError();

}