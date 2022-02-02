package com.threeamigos.mandelbrot.implementations.service.imageproducer;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;

import com.threeamigos.mandelbrot.interfaces.service.FractalService;

class Mode2DirectColorModelImageProducer implements SingleColorModelImageProducer {

	private static final int MAX_ITERATIONS = (int) Math.pow(2, FractalService.MAX_ITERATIONS_EXPONENT);

	private final DirectColorModel directColorModel;

	private int maxIterations;

	private final int[] map;

	public Mode2DirectColorModelImageProducer(int maxIterations) {

		this.maxIterations = maxIterations;

		int[] gr = new int[MAX_ITERATIONS];
		int[] gg = new int[MAX_ITERATIONS];
		int[] gb = new int[MAX_ITERATIONS];

		for (int iteration = FractalService.MIN_ITERATIONS_EXPONENT; iteration <= FractalService.MAX_ITERATIONS_EXPONENT; iteration++) {
			int minValue = (iteration == FractalService.MIN_ITERATIONS_EXPONENT ? 0
					: (int) Math.pow(2, iteration - 1));
			int maxValue = (int) Math.pow(2, iteration) - 1;

			int localMaxIterations = maxValue - minValue + 1;

			int[] r = new int[localMaxIterations];
			int[] g = new int[localMaxIterations];
			int[] b = new int[localMaxIterations];

			final int redStartingPoint = 0;
			final int greenStartingPoint = localMaxIterations / 3;
			final int blueStartingPoint = 2 * localMaxIterations / 3;

			int third = (localMaxIterations + 2) / 3;

			for (int i = 0; i < third; i++) {
				int value = 255 - (256 * i / third);
				r[mod(redStartingPoint + i, localMaxIterations)] = value;
				r[mod(redStartingPoint - i, localMaxIterations)] = value;
				g[mod(greenStartingPoint + i, localMaxIterations)] = value;
				g[mod(greenStartingPoint - i, localMaxIterations)] = value;
				b[mod(blueStartingPoint + i, localMaxIterations)] = value;
				b[mod(blueStartingPoint - i, localMaxIterations)] = value;
			}

			for (int i = 0; i < localMaxIterations; i++) {
				gr[minValue + i] = r[i];
				gg[minValue + i] = g[i];
				gb[minValue + i] = b[i];
			}

		}

		map = new int[MAX_ITERATIONS];
		for (int i = 0; i < MAX_ITERATIONS; i++) {
			map[i] = (gr[i] << 16) | (gg[i] << 8) | gb[i];
		}

		directColorModel = new DirectColorModel(8 * 6, 0x00ff0000, 0x0000ff00, 0x000000ff);
	}

	private int mod(int value, int localMaxIterations) {
		if (value >= localMaxIterations) {
			value = value % localMaxIterations;
		}
		if (value < 0) {
			value = localMaxIterations + value;
		}
		return value;
	}

	@Override
	public Image produceImage(int width, int height, int[] pixels) {
		int[] translatedValues = new int[pixels.length];
		int length = pixels.length;
		for (int i = 0; i < length; i++) {
			int currentIterations = pixels[i];
			if (currentIterations == maxIterations || currentIterations == FractalService.ITERATION_NOT_CALCULATED) {
				translatedValues[i] = 0;
			} else {
				if (currentIterations < map.length) {
					translatedValues[i] = map[currentIterations];
				}
			}
		}
		return Toolkit.getDefaultToolkit()
				.createImage(new MemoryImageSource(width, height, directColorModel, translatedValues, 0, width));
	}

	@Override
	public String getName() {
		return "Mode 2";
	}

}
