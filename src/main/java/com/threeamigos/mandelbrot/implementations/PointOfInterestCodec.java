package com.threeamigos.mandelbrot.implementations;

import java.util.StringTokenizer;

import com.threeamigos.mandelbrot.interfaces.PointOfInterest;

public class PointOfInterestCodec {

	private static final String SEPARATOR = "|";

	public static final PointOfInterest parsePointOfInterest(String line) {
		String name;
		double minReal;
		double maxReal;
		double minImaginary;
		double maxImaginary;
		int zoomCount;
		StringTokenizer st = new StringTokenizer(line, SEPARATOR);
		name = st.nextToken();
		minReal = Double.parseDouble(st.nextToken());
		maxReal = Double.parseDouble(st.nextToken());
		minImaginary = Double.parseDouble(st.nextToken());
		maxImaginary = Double.parseDouble(st.nextToken());
		zoomCount = Integer.parseInt(st.nextToken());
		return new PointOfInterestImpl(name, minReal, maxReal, minImaginary, maxImaginary, zoomCount);
	}

	public static final String toString(PointOfInterest pointOfInterest) {
		return new StringBuilder(pointOfInterest.getName()).append(SEPARATOR).append(pointOfInterest.getMinReal())
				.append(SEPARATOR).append(pointOfInterest.getMaxReal()).append(SEPARATOR)
				.append(pointOfInterest.getMinImaginary()).append(SEPARATOR).append(pointOfInterest.getMaxImaginary())
				.append(SEPARATOR).append(pointOfInterest.getZoomCount()).toString();
	}

}
