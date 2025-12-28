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

import com.threeamigos.common.util.implementations.persistence.file.FilePersistResultBuilder;
import com.threeamigos.common.util.interfaces.persistence.PersistResult;
import com.threeamigos.common.util.interfaces.persistence.file.FilePersistResult;
import com.threeamigos.mandelbrot.implementations.service.PointOfInterestImpl;
import com.threeamigos.mandelbrot.interfaces.persister.PointsOfInterestPersister;
import com.threeamigos.mandelbrot.interfaces.service.FractalType;
import com.threeamigos.mandelbrot.interfaces.service.PointOfInterest;
import org.jspecify.annotations.NonNull;

public class PointsOfInterestPersisterImpl implements PointsOfInterestPersister {

	private static final String POINTS_OF_INTEREST_DESCRIPTION = "Points of interest";
	private static final String POINTS_OF_INTEREST_FILENAME = "points_of_interest.txt";

	private static final String SEPARATOR = "|";

	private List<PointOfInterest> pointsOfInterest;

	@Override
	public FilePersistResult savePointsOfInterest(List<PointOfInterest> pointsOfInterest) {
		String filename = getFilename();
		try (PrintWriter printWriter = new PrintWriter(new File(filename))) {
			for (PointOfInterest pointOfInterest : pointsOfInterest) {
				printWriter.println(toString(pointOfInterest));
			}
			printWriter.println("");
			return FilePersistResultBuilder.successful(POINTS_OF_INTEREST_DESCRIPTION, filename);
		} catch (IOException e) {
			return FilePersistResultBuilder.error(POINTS_OF_INTEREST_DESCRIPTION, POINTS_OF_INTEREST_FILENAME, e.getMessage());
		}
	}

	@Override
	public PersistResult loadPointsOfInterest() {
		try (InputStream inputStream = getInputStream()) {
			if (inputStream == null) {
				return FilePersistResultBuilder.notReadable(POINTS_OF_INTEREST_DESCRIPTION, POINTS_OF_INTEREST_FILENAME);
			}
			List<PointOfInterest> points = new ArrayList<>();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
				String line;
				while ((line = reader.readLine()) != null) {
					if (!line.trim().isEmpty()) {
						PointOfInterest point = parsePointOfInterest(line);
						points.add(point);
					}
				}
			}
			pointsOfInterest = points;
			return FilePersistResultBuilder.successful(POINTS_OF_INTEREST_DESCRIPTION, POINTS_OF_INTEREST_FILENAME);
		} catch (Exception e) {
			return FilePersistResultBuilder.error(POINTS_OF_INTEREST_DESCRIPTION, POINTS_OF_INTEREST_FILENAME, e.getMessage());
		}
	}

	@Override
	public List<PointOfInterest> getPointsOfInterest() {
		return pointsOfInterest;
	}

	private InputStream getInputStream() {
		String path = getPointsOfInterestPath() + File.separatorChar + POINTS_OF_INTEREST_FILENAME;
		InputStream inputStream = null;
		File inputFile = new File(path);
		if (inputFile.exists() && inputFile.canRead()) {
			try {
				inputStream = new FileInputStream(inputFile);
			} catch (FileNotFoundException e) {
				return null;
			}
		} else {
			inputStream = this.getClass().getResourceAsStream("/" + POINTS_OF_INTEREST_FILENAME);
		}
		return inputStream;
	}

	private String getPointsOfInterestPath() {
		String path = System.getProperty("user.home") + File.separatorChar +
                ".com.threeamigos.mandelbrot";
		new File(path).mkdirs();
		return path;
	}

	@Override
	public String getFilename() {
		return getPointsOfInterestPath() + File.separatorChar +
                POINTS_OF_INTEREST_FILENAME;
	}

	private final PointOfInterest parsePointOfInterest(String line) {
		StringTokenizer st = new StringTokenizer(line, SEPARATOR);
		String name = st.nextToken();
		double minImaginary = Double.parseDouble(st.nextToken());
		double maxImaginary = Double.parseDouble(st.nextToken());
		double centralReal = Double.parseDouble(st.nextToken());
		int zoomCount = Integer.parseInt(st.nextToken());
		int maxIterations = Integer.parseInt(st.nextToken());
		if (!st.hasMoreTokens()) {
			return new PointOfInterestImpl(name, minImaginary, maxImaginary, centralReal, zoomCount, maxIterations);
		} else {
			double juliaCReal = Double.parseDouble(st.nextToken());
			double juliaCImaginary = Double.parseDouble(st.nextToken());
			return new PointOfInterestImpl(name, minImaginary, maxImaginary, centralReal, zoomCount, maxIterations,
					juliaCReal, juliaCImaginary);
		}
	}

	private final String toString(PointOfInterest pointOfInterest) {
		StringBuilder sb = new StringBuilder(pointOfInterest.getName()).append(SEPARATOR)
				.append(pointOfInterest.getMinImaginary()).append(SEPARATOR).append(pointOfInterest.getMaxImaginary())
				.append(SEPARATOR).append(pointOfInterest.getCentralReal()).append(SEPARATOR)
				.append(pointOfInterest.getZoomCount()).append(SEPARATOR).append(pointOfInterest.getMaxIterations());
		if (pointOfInterest.getFractalType() == FractalType.JULIA) {
			sb.append(SEPARATOR).append(pointOfInterest.getJuliaCReal()).append(SEPARATOR)
					.append(pointOfInterest.getJuliaCImaginary());
		}
		return sb.toString();
	}

}
