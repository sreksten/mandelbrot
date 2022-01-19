package com.threeamigos.mandelbrot.implementations.persister;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.threeamigos.mandelbrot.implementations.service.PointOfInterestImpl;
import com.threeamigos.mandelbrot.interfaces.persister.PersistResult;
import com.threeamigos.mandelbrot.interfaces.persister.PointsOfInterestPersister;
import com.threeamigos.mandelbrot.interfaces.service.PointOfInterest;

public class PointsOfInterestPersisterImpl implements PointsOfInterestPersister {

	public static final String POINTS_OF_INTEREST_FILENAME = "points_of_interest.txt";

	private static final String SEPARATOR = "|";

	private List<PointOfInterest> pointsOfInterest;

	@Override
	public PersistResult savePointsOfInterest(List<PointOfInterest> pointsOfInterest) {
		String filename = getFilename();
		try (PrintWriter printWriter = new PrintWriter(new File(filename))) {
			for (PointOfInterest pointOfInterest : pointsOfInterest) {
				printWriter.println(toString(pointOfInterest));
			}
			PersistResultImpl result = new PersistResultImpl();
			result.setFilename(filename);
			return result;

		} catch (IOException e) {
			return new PersistResultImpl("Error while saving points of interest: " + e.getMessage());
		}
	}

	@Override
	public PersistResult loadPointsOfInterest() {
		try (InputStream inputStream = getInputStream(POINTS_OF_INTEREST_FILENAME)) {
			if (inputStream == null) {
				return new PersistResultImpl("Cannot access " + POINTS_OF_INTEREST_FILENAME);
			}
			List<PointOfInterest> points = new ArrayList<>();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
				String line;
				while ((line = reader.readLine()) != null) {
					if (!line.isBlank()) {
						PointOfInterest point = parsePointOfInterest(line);
						points.add(point);
					}
				}
			}
			pointsOfInterest = points;
			return new PersistResultImpl();
		} catch (Exception e) {
			return new PersistResultImpl("Error reading " + POINTS_OF_INTEREST_FILENAME + ": " + e.getMessage());
		}
	}

	@Override
	public List<PointOfInterest> getPointsOfInterest() {
		return pointsOfInterest;
	}

	private InputStream getInputStream(String filename) {
		String path = new StringBuilder(getPointsOfInterestPath()).append(File.separatorChar).append(filename)
				.toString();
		InputStream inputStream = null;
		File inputFile = new File(path);
		if (inputFile.exists() && inputFile.canRead()) {
			try {
				inputStream = new FileInputStream(inputFile);
			} catch (FileNotFoundException e) {
				return null;
			}
		} else {
			inputStream = this.getClass().getResourceAsStream("/" + filename);
		}
		return inputStream;
	}

	private String getPointsOfInterestPath() {
		String path = new StringBuilder(System.getProperty("user.home")).append(File.separatorChar)
				.append(".com.threeamigos.mandelbrot").toString();
		new File(path).mkdirs();
		return path;
	}

	@Override
	public String getFilename() {
		return new StringBuilder(getPointsOfInterestPath()).append(File.separatorChar)
				.append(POINTS_OF_INTEREST_FILENAME).toString();
	}

	private final PointOfInterest parsePointOfInterest(String line) {
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

	private final String toString(PointOfInterest pointOfInterest) {
		return new StringBuilder(pointOfInterest.getName()).append(SEPARATOR).append(pointOfInterest.getMinImaginary())
				.append(SEPARATOR).append(pointOfInterest.getMaxImaginary()).append(SEPARATOR)
				.append(pointOfInterest.getCentralReal()).append(SEPARATOR).append(pointOfInterest.getZoomCount())
				.toString();
	}

}
