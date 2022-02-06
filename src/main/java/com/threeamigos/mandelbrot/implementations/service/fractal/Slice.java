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

	public List<Slice> split() {

		List<Slice> list = new ArrayList<>();

		int width = endX - startX;
		int height = endY - startY;
		int halfX = startX + width / 2;
		int halfY = startY + height / 2;

		Slice s1 = new Slice(startX, startY, halfX, halfY);
		s1.symmetricity = symmetricity;
		s1.originX = originX;
		s1.originY = originY;

		Slice s2 = new Slice(halfX, startY, endX, halfY);
		s2.symmetricity = symmetricity;
		s2.originX = originX;
		s2.originY = originY;

		Slice s3 = new Slice(startX, halfY, endX, endY);
		s3.symmetricity = symmetricity;
		s3.originX = originX;
		s3.originY = originY;

		Slice s4 = new Slice(halfX, halfY, endX, endY);
		s4.symmetricity = symmetricity;
		s4.originX = originX;
		s4.originY = originY;

		list.add(s1);
		list.add(s2);
		list.add(s3);
		list.add(s4);

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
				pixelBuffer.setPixel(startX + i, currentDestinationY - j, pixelBuffer.getIterations(startX + i, startY + j));
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

	static enum SimmetricityType {
		X_AXIS,
		ORIGIN;
	}

}
