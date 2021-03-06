package com.threeamigos.mandelbrot.implementations.service.imageproducer;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;

public class IndexColorModelImageProducer implements SingleColorModelImageProducer {

	private final byte[] colorMapR;
	private final byte[] colorMapG;
	private final byte[] colorMapB;
	private final IndexColorModel indexColorModel;

	public IndexColorModelImageProducer() {
		int length = 256;
		colorMapR = new byte[length];
		colorMapG = new byte[length];
		colorMapB = new byte[length];
		int index = 0;
		for (int i = 0; i < 256; i++) {
			if (i % 2 == 0) {
				colorMapR[index] = (byte) (256 - i);
			}
			colorMapG[index] = (byte) i;
			if (i % 2 == 1) {
				colorMapB[index] = (byte) (256 - i);
			}
			index++;
		}
		indexColorModel = new IndexColorModel(8, length, colorMapR, colorMapG, colorMapB, 0);
	}

	@Override
	public Image produceImage(int width, int height, int[] pixels) {
		return Toolkit.getDefaultToolkit()
				.createImage(new MemoryImageSource(width, height, indexColorModel, pixels, 0, width));
	}

	@Override
	public String getName() {
		return "Indexed";
	}
}
