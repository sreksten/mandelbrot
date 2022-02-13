package com.threeamigos.mandelbrot.interfaces.ui;

/**
 * An object that returns a screen resolution, i.e. width and height, and its
 * name. (e.g. 1920x1080: Full HD)
 *
 * @author stefano.reksten
 *
 */
public interface Resolution {

	public String getName();

	public int getWidth();

	public int getHeight();

}
