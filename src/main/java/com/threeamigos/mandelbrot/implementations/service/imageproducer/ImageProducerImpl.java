package com.threeamigos.mandelbrot.implementations.service.imageproducer;

import java.awt.Image;

import com.threeamigos.mandelbrot.interfaces.service.MultipleColorModelImageProducerService;

public class MultipleColorModelImageProducerImpl implements MultipleColorModelImageProducerService {

	private SingleColorModelImageProducer indexColorModelImageProducer;
	private SingleColorModelImageProducer directColorModelImageProducer;

	private SingleColorModelImageProducer currentImageProducer;

	public MultipleColorModelImageProducerImpl(int maxIterations) {
		indexColorModelImageProducer = new IndexColorModelImageProducer();
		directColorModelImageProducer = new DirectColorModelImageProducer(maxIterations);

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
	public Image produceImage(int width, int height, int[] pixels) {
		return currentImageProducer.produceImage(width, height, pixels);
	}

}
