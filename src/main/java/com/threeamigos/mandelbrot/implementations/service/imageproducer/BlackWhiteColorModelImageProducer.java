package com.threeamigos.mandelbrot.implementations.service.imageproducer;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;

public class BlackWhiteColorModelImageProducer implements SingleColorModelImageProducer {

	private final byte[] colorMapR;
	private final byte[] colorMapG;
	private final byte[] colorMapB;
	private final IndexColorModel indexColorModel;

	public BlackWhiteColorModelImageProducer() {
		colorMapR = new byte[2];
		colorMapG = new byte[2];
		colorMapB = new byte[2];
		colorMapR[1] = (byte) 0xFF;
		colorMapG[1] = (byte) 0xFF;
		colorMapB[1] = (byte) 0xFF;
		indexColorModel = new IndexColorModel(8, 2, colorMapR, colorMapG, colorMapB, 0);
	}

	@Override
	public Image produceImage(int width, int height, int[] pixels) {
		return Toolkit.getDefaultToolkit()
				.createImage(new MemoryImageSource(width, height, indexColorModel, pixels, 0, width));
	}

	@Override
	public String getName() {
		return "Black and White";
	}

}
