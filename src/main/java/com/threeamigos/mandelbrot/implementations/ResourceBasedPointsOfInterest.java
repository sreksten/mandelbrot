package com.threeamigos.mandelbrot.implementations;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import com.threeamigos.mandelbrot.interfaces.PointOfInterest;
import com.threeamigos.mandelbrot.interfaces.PointsOfInterest;

public class ResourceBasedPointsOfInterest implements PointsOfInterest {

	private static final String SEPARATOR = "|";

	private List<PointOfInterest> pointsOfInterest;

	public ResourceBasedPointsOfInterest(String resourceName) {
		pointsOfInterest = new ArrayList<>();
		try (InputStream inputStream = this.getClass().getResourceAsStream(resourceName)) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ((line = reader.readLine()) != null) {
				PointOfInterest pointOfInterest = parsePointOfInterest(line);
				pointsOfInterest.add(pointOfInterest);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error reading " + resourceName + ": " + e.getMessage());
		}
	}

	private PointOfInterest parsePointOfInterest(String line) {
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

	@Override
	public List<PointOfInterest> getPointsOfInterest() {
		return pointsOfInterest;
	}

	@Override
	public void store(PointOfInterest pointOfInterest, PrintWriter printWriter) {
		printWriter.print(pointOfInterest.getName());
		printWriter.print(SEPARATOR);
		printWriter.print(pointOfInterest.getMinReal());
		printWriter.print(SEPARATOR);
		printWriter.print(pointOfInterest.getMaxReal());
		printWriter.print(SEPARATOR);
		printWriter.print(pointOfInterest.getMinImaginary());
		printWriter.print(SEPARATOR);
		printWriter.print(pointOfInterest.getMaxImaginary());
		printWriter.print(SEPARATOR);
		printWriter.print(pointOfInterest.getZoomCount());
	}

}
