package com.threeamigos.mandelbrot.interfaces.service;

import java.awt.Image;
import java.util.List;

public interface ImageProducerService {

	public Image produceImage(int width, int height, int[] pixels);

	public void cycleColorModel();

	public void switchColorModel(String modeName);

	public List<String> getColorModeNames();

	public String getCurrentColorModelName();

}
