package com.threeamigos.mandelbrot.implementations;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;

import com.threeamigos.mandelbrot.interfaces.DataBuffer;
import com.threeamigos.mandelbrot.interfaces.ImageProducer;
import com.threeamigos.mandelbrot.interfaces.MandelbrotCalculator;

public class DirectColorModelImageProducer implements ImageProducer {

	private final DirectColorModel directColorModel;

	private final int[] map;

	public DirectColorModelImageProducer() {
		int[] r = new int[MandelbrotCalculator.MAX_ITERATIONS];
		int[] g = new int[MandelbrotCalculator.MAX_ITERATIONS];
		int[] b = new int[MandelbrotCalculator.MAX_ITERATIONS];

		final int redStartingPoint = 0;
		final int greenStartingPoint = MandelbrotCalculator.MAX_ITERATIONS / 3;
		final int blueStartingPoint = 2 * MandelbrotCalculator.MAX_ITERATIONS / 3;

		int third = MandelbrotCalculator.MAX_ITERATIONS / 3;

		for (int i = 0; i < third; i++) {
			int value = 255 - (256 * i / third);
			r[mod(redStartingPoint + i)] = value;
			r[mod(redStartingPoint - i)] = value;
			g[mod(greenStartingPoint + i)] = value;
			g[mod(greenStartingPoint - i)] = value;
			b[mod(blueStartingPoint + i)] = value;
			b[mod(blueStartingPoint - i)] = value;
		}

		map = new int[MandelbrotCalculator.MAX_ITERATIONS];
		for (int i = 0; i < MandelbrotCalculator.MAX_ITERATIONS; i++) {
			map[i] = (r[i] << 16) | (g[i] << 8) | b[i];
		}

		directColorModel = new DirectColorModel(8 * 6, 0x00ff0000, 0x0000ff00, 0x000000ff);
	}

	private int mod(int value) {
		if (value >= MandelbrotCalculator.MAX_ITERATIONS) {
			value = value % MandelbrotCalculator.MAX_ITERATIONS;
		}
		if (value < 0) {
			value = MandelbrotCalculator.MAX_ITERATIONS + value;
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
			if (currentIterations == MandelbrotCalculator.MAX_ITERATIONS) {
				translatedValues[i] = 0;
			} else {
				translatedValues[i] = map[currentIterations];
			}
		}
		return Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(dataBuffer.getWidth(),
				dataBuffer.getHeight(), directColorModel, translatedValues, 0, dataBuffer.getWidth()));
	}

}
