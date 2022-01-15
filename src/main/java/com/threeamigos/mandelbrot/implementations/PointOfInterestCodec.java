package com.threeamigos.mandelbrot.implementations;

import java.util.StringTokenizer;

import com.threeamigos.mandelbrot.interfaces.PointOfInterest;

public class PointOfInterestCodec {

	private static final String SEPARATOR = "|";

	private PointOfInterestCodec() {
	}

	public static final PointOfInterest parsePointOfInterest(String line) {
		String name;
		double minImaginary;
		double maxImaginary;
		double centralReal;
		int zoomCount;
		StringTokenizer st = new StringTokenizer(line, SEPARATOR);
		name = st.nextToken();
		minImaginary = Double.parseDouble(st.nextToken());
		maxImaginary = Double.parseDouble(st.nextToken());
		centralReal = Double.parseDouble(st.nextToken());
		zoomCount = Integer.parseInt(st.nextToken());
		return new PointOfInterestImpl(name, minImaginary, maxImaginary, centralReal, zoomCount);
	}

	public static final String toString(PointOfInterest pointOfInterest) {
		return new StringBuilder(pointOfInterest.getName()).append(SEPARATOR).append(pointOfInterest.getMinImaginary())
				.append(SEPARATOR).append(pointOfInterest.getMaxImaginary()).append(SEPARATOR)
				.append(pointOfInterest.getCentralReal()).append(SEPARATOR).append(pointOfInterest.getZoomCount())
				.toString();
	}

}
