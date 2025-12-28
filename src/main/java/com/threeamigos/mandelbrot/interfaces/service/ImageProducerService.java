package com.threeamigos.mandelbrot.interfaces.service;

import java.awt.Image;
import java.util.List;

/**
 * An object that renders an image starting from an array of calculation results
 *
 * @author Stefano Reksten
 *
 */
public interface ImageProducerService {

	Image produceImage(int width, int height, int[] pixels);

	void cycleColorModel();

	void switchColorModel(String modeName);

	List<String> getColorModeNames();

	String getCurrentColorModelName();

}
