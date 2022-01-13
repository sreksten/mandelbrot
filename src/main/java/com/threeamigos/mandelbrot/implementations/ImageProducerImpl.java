package com.threeamigos.mandelbrot.implementations;

import java.awt.Image;

import com.threeamigos.mandelbrot.interfaces.DataBuffer;
import com.threeamigos.mandelbrot.interfaces.ImageProducer;
import com.threeamigos.mandelbrot.interfaces.MultipleVariantImageProducer;

public class ImageProducerImpl implements MultipleVariantImageProducer {

	private ImageProducer indexColorModelImageProducer = new IndexColorModelImageProducer();
	private ImageProducer directColorModelImageProducer = new DirectColorModelImageProducer();

	private ImageProducer currentImageProducer;

	public ImageProducerImpl() {
		indexColorModelImageProducer = new IndexColorModelImageProducer();
		directColorModelImageProducer = new DirectColorModelImageProducer();

		currentImageProducer = directColorModelImageProducer;
	}

	@Override
	public boolean isUsingIndexColorModel() {
		return currentImageProducer == indexColorModelImageProducer;
	}

	@Override
	public boolean isUsingDirectColorModel() {
		return currentImageProducer == directColorModelImageProducer;
	}

	@Override
	public void useIndexColorModel() {
		currentImageProducer = indexColorModelImageProducer;
	}

	@Override
	public void useDirectColorModel() {
		currentImageProducer = directColorModelImageProducer;
	}

	@Override
	public Image produceImage(DataBuffer dataBuffer) {
		return currentImageProducer.produceImage(dataBuffer);
	}

}
