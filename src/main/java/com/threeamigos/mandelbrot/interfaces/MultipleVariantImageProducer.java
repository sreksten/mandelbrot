package com.threeamigos.mandelbrot.interfaces;

public interface MultipleVariantImageProducer extends ImageProducer {

	public boolean isUsingIndexColorModel();

	public boolean isUsingDirectColorModel();

	public void useIndexColorModel();

	public void useDirectColorModel();

}
