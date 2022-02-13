package com.threeamigos.mandelbrot.implementations.service.fractal;

import java.util.ArrayList;
import java.util.List;

class Slice {

	final int startX;
	final int startY;
	final int endX;
	final int endY;

	SimmetricityType symmetricity;
	int originX;
	int originY;

	Slice(int startX, int startY, int endX, int endY) {
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
	}

	public boolean hasSimmetricity() {
		return symmetricity != null;
	}

	public List<Slice> split() {

		List<Slice> list = new ArrayList<>();

		int width = endX - startX;
		int height = endY - startY;

		final int splitNumber = 4;

		int quotientX = width / splitNumber;
		int quotientY = height / splitNumber;

		for (int i = 0; i < splitNumber; i++) {
			int sizeX;
			if (i < splitNumber - 1) {
				// A small trick - as we're checking the contour of a slice, sharing it with its
				// neighbor leaves half of the calculation already done.
				sizeX = quotientX + 1;
			} else {
				sizeX = width - quotientX * (splitNumber - 1);
			}
			for (int j = 0; j < splitNumber; j++) {
				int sizeY;
				if (j < splitNumber - 1) {
					// Ditto.
					sizeY = quotientY + 1;
				} else {
					sizeY = height - quotientY * (splitNumber - 1);
				}
				Slice s = new Slice(startX + quotientX * i, startY + quotientY * j, startX + quotientX * i + sizeX,
						startY + quotientY * j + sizeY);
				s.symmetricity = symmetricity;
				s.originX = originX;
				s.originY = originY;
				list.add(s);
			}
		}

		return list;

	}

	public void replicate(IterationsBuffer pixelBuffer) {
		if (symmetricity == SimmetricityType.X_AXIS) {
			replicateXAxis(pixelBuffer);
		} else if (symmetricity == SimmetricityType.ORIGIN) {
			replicateOrigin(pixelBuffer);
		}
	}

	private void replicateXAxis(IterationsBuffer pixelBuffer) {
		int width = endX - startX;
		int height = endY - startY;
		int currentDestinationY = originY + originY - startY - 1;

		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				pixelBuffer.setPixel(startX + i, currentDestinationY - j,
						pixelBuffer.getIterations(startX + i, startY + j));
			}
		}
	}

	private void replicateOrigin(IterationsBuffer pixelBuffer) {
		int width = endX - startX;
		int height = endY - startY;
		int currentDestinationX = originX + originX - startX - 1;
		int currentDestinationY = originY + originY - startY - 1;

		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				pixelBuffer.setPixel(currentDestinationX - i, currentDestinationY - j,
						pixelBuffer.getIterations(startX + i, startY + j));
			}
		}
	}

	enum SimmetricityType {
		X_AXIS,
		ORIGIN;
	}

}
