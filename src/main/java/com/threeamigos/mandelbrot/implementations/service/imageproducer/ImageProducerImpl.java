package com.threeamigos.mandelbrot.implementations.service.imageproducer;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import com.threeamigos.mandelbrot.interfaces.service.ImageProducerService;

public class ImageProducerImpl implements ImageProducerService {

	private SingleColorModelImageProducer[] imageProducers;
	private List<String> imageProducerNames;

	private int currentImageProducer;

	public ImageProducerImpl(int maxIterations) {
		createImageProducers(maxIterations);
		createImageProducerNames();
	}

	private void createImageProducers(int maxIterations) {
		imageProducers = new SingleColorModelImageProducer[3];
		imageProducers[0] = new IndexColorModelImageProducer();
		imageProducers[1] = new Mode1DirectColorModelImageProducer(maxIterations);
		imageProducers[2] = new Mode2DirectColorModelImageProducer(maxIterations);

		currentImageProducer = 1;
	}

	private void createImageProducerNames() {
		imageProducerNames = new ArrayList();
		for (SingleColorModelImageProducer imageProducer : imageProducers) {
			imageProducerNames.add(imageProducer.getName());
		}
	}

	@Override
	public Image produceImage(int width, int height, int[] pixels) {
		return imageProducers[currentImageProducer].produceImage(width, height, pixels);
	}

	@Override
	public void cycleColorModel() {
		currentImageProducer = (currentImageProducer + 1) % imageProducers.length;
	}

	@Override
	public void switchColorModel(String modeName) {
		for (int i = 0; i < imageProducers.length; i++) {
			if (imageProducers[i].getName().equals(modeName)) {
				currentImageProducer = i;
				break;
			}
		}
	}

	@Override
	public List<String> getColorModeNames() {
		return imageProducerNames;
	}

	@Override
	public String getCurrentColorModelName() {
		return imageProducers[currentImageProducer].getName();
	}
}
