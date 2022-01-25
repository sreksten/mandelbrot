package com.threeamigos.mandelbrot.implementations.service.imageproducer;

import java.awt.Image;

public interface SingleColorModelImageProducer {

	public Image produceImage(int width, int height, int[] pixels);

	public String getName();

}
