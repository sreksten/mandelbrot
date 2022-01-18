package com.threeamigos.mandelbrot.implementations.service.imageproducer;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;

import com.threeamigos.mandelbrot.interfaces.service.MandelbrotService;

class DirectColorModelImageProducer implements SingleColorModelImageProducer {

	private static final int MAX_ITERATIONS = (int) Math.pow(2, MandelbrotService.MAX_ITERATIONS_EXPONENT);

	private final DirectColorModel directColorModel;

	private int maxIterations;

	private final int[] map;

	public DirectColorModelImageProducer(int maxIterations) {

		this.maxIterations = maxIterations;

		int[] r = new int[MAX_ITERATIONS];
		int[] g = new int[MAX_ITERATIONS];
		int[] b = new int[MAX_ITERATIONS];

		final int redStartingPoint = 0;
		final int greenStartingPoint = MAX_ITERATIONS / 3;
		final int blueStartingPoint = 2 * MAX_ITERATIONS / 3;

		int third = MAX_ITERATIONS / 3;

		for (int i = 0; i < third; i++) {
			int value = 255 - (256 * i / third);
			r[mod(redStartingPoint + i)] = value;
			r[mod(redStartingPoint - i)] = value;
			g[mod(greenStartingPoint + i)] = value;
			g[mod(greenStartingPoint - i)] = value;
			b[mod(blueStartingPoint + i)] = value;
			b[mod(blueStartingPoint - i)] = value;
		}

		map = new int[MAX_ITERATIONS];
		for (int i = 0; i < MAX_ITERATIONS; i++) {
			map[i] = (r[i] << 16) | (g[i] << 8) | b[i];
		}

		directColorModel = new DirectColorModel(8 * 6, 0x00ff0000, 0x0000ff00, 0x000000ff);
	}

	private int mod(int value) {
		if (value >= MAX_ITERATIONS) {
			value = value % MAX_ITERATIONS;
		}
		if (value < 0) {
			value = MAX_ITERATIONS + value;
		}
		return value;
	}

	@Override
	public Image produceImage(int width, int height, int[] pixels) {
		int[] translatedValues = new int[pixels.length];
		int length = pixels.length;
		int multiplier = (int) Math.pow(2, MandelbrotService.MAX_ITERATIONS_EXPONENT) / maxIterations;
		for (int i = 0; i < length; i++) {
			int currentIterations = pixels[i];
			if (currentIterations == maxIterations || currentIterations == MandelbrotService.ITERATION_NOT_CALCULATED) {
				translatedValues[i] = 0;
			} else {
				translatedValues[i] = map[currentIterations * multiplier];
			}
		}
		return Toolkit.getDefaultToolkit()
				.createImage(new MemoryImageSource(width, height, directColorModel, translatedValues, 0, width));
	}

}
