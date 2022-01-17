package com.threeamigos.mandelbrot.implementations;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;

import com.threeamigos.mandelbrot.interfaces.DataBuffer;
import com.threeamigos.mandelbrot.interfaces.ImageProducer;

class DirectColorModelImageProducer implements ImageProducer {

	private final DirectColorModel directColorModel;

	private int maxIterations;

	private final int[] map;

	public DirectColorModelImageProducer(int maxIterations) {
		this.maxIterations = maxIterations;
		int[] r = new int[maxIterations];
		int[] g = new int[maxIterations];
		int[] b = new int[maxIterations];

		final int redStartingPoint = 0;
		final int greenStartingPoint = maxIterations / 3;
		final int blueStartingPoint = 2 * maxIterations / 3;

		int third = maxIterations / 3;

		for (int i = 0; i < third; i++) {
			int value = 255 - (256 * i / third);
			r[mod(redStartingPoint + i)] = value;
			r[mod(redStartingPoint - i)] = value;
			g[mod(greenStartingPoint + i)] = value;
			g[mod(greenStartingPoint - i)] = value;
			b[mod(blueStartingPoint + i)] = value;
			b[mod(blueStartingPoint - i)] = value;
		}

		map = new int[maxIterations];
		for (int i = 0; i < maxIterations; i++) {
			map[i] = (r[i] << 16) | (g[i] << 8) | b[i];
		}

		directColorModel = new DirectColorModel(8 * 6, 0x00ff0000, 0x0000ff00, 0x000000ff);
	}

	private int mod(int value) {
		if (value >= maxIterations) {
			value = value % maxIterations;
		}
		if (value < 0) {
			value = maxIterations + value;
		}
		return value;
	}

	@Override
	public Image produceImage(DataBuffer dataBuffer) {
		int[] pixels = dataBuffer.getPixels();
		int[] translatedValues = new int[pixels.length];
		int length = pixels.length;
		for (int i = 0; i < length; i++) {
			int currentIterations = pixels[i];
			if (currentIterations == maxIterations || currentIterations == DataBuffer.NOT_CALCULATED) {
				translatedValues[i] = 0;
			} else {
				translatedValues[i] = map[currentIterations];
			}
		}
		return Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(dataBuffer.getWidth(),
				dataBuffer.getHeight(), directColorModel, translatedValues, 0, dataBuffer.getWidth()));
	}

}
