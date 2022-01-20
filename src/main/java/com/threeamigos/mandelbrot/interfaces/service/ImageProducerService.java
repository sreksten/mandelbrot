package com.threeamigos.mandelbrot.interfaces.service;

import java.awt.Image;

public interface ImageProducerService {

	public Image produceImage(int width, int height, int[] pixels);

	public boolean isUsingIndexColorModel();

	public boolean isUsingDirectColorModel();

	public void useIndexColorModel();

	public void useDirectColorModel();

	public void switchColorModel();

}
