package com.threeamigos.mandelbrot.implementations.service;

import com.threeamigos.mandelbrot.implementations.service.imageproducer.ImageProducerImpl;
import com.threeamigos.mandelbrot.interfaces.service.ImageProducerService;
import com.threeamigos.mandelbrot.interfaces.service.ImageProducerServiceFactory;

public class ImageProducerServiceFactoryImpl implements ImageProducerServiceFactory {

	@Override
	public ImageProducerService createInstance(int maxIterations) {
		return new ImageProducerImpl(maxIterations);
	}

}
