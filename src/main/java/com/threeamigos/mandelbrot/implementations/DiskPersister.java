package com.threeamigos.mandelbrot.implementations;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
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

import javax.imageio.ImageIO;

import com.threeamigos.mandelbrot.interfaces.DataPersister;
import com.threeamigos.mandelbrot.interfaces.PointOfInterest;
import com.threeamigos.mandelbrot.interfaces.PointsOfInterest;

public class DiskPersister implements DataPersister {

	public static final String POINTS_OF_INTEREST_FILENAME = "points_of_interest.txt";

	private static final String SEPARATOR = "|";

	@Override
	public PersistResult saveImage(Image image, String filename) {
		try {
			File outputFile = new File(filename);

			final BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
					BufferedImage.TYPE_3BYTE_BGR);
			final Graphics2D g2 = bufferedImage.createGraphics();
			g2.drawImage(image, null, null);
			g2.dispose();

			ImageIO.write(bufferedImage, "png", outputFile);

			return new PersistResultImpl();

		} catch (IOException e) {
			return new PersistResultImpl("Error while saving image: " + e.getMessage());
		}
	}

	@Override
	public PersistResult savePointsOfInterest(PointsOfInterest pointsOfInterest) {
		String path = new StringBuilder(getPointsOfInterestPath()).append(File.separatorChar)
				.append(POINTS_OF_INTEREST_FILENAME).toString();
		try (PrintWriter printWriter = new PrintWriter(new File(path))) {
			for (PointOfInterest pointOfInterest : pointsOfInterest.getElements()) {
				printWriter.println(toString(pointOfInterest));
			}
			return new PersistResultImpl();
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
			List<PointOfInterest> pointsOfInterest = new ArrayList<>();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
				String line;
				while ((line = reader.readLine()) != null) {
					if (!line.isBlank()) {
						PointOfInterest pointOfInterest = parsePointOfInterest(line);
						pointsOfInterest.add(pointOfInterest);
					}
				}
			}
			return new PersistResultImpl(pointsOfInterest);
		} catch (Exception e) {
			return new PersistResultImpl("Error reading " + POINTS_OF_INTEREST_FILENAME + ": " + e.getMessage());
		}
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

	public class PersistResultImpl implements PersistResult {

		private final boolean successful;

		private String error;

		private List<PointOfInterest> pointsOfInterest;

		PersistResultImpl() {
			successful = true;
		}

		PersistResultImpl(String error) {
			successful = false;
			this.error = error;
		}

		PersistResultImpl(List<PointOfInterest> pointsOfInterest) {
			successful = true;
			this.pointsOfInterest = pointsOfInterest;
		}

		@Override
		public boolean isSuccessful() {
			return successful;
		}

		@Override
		public String getError() {
			return error;
		}

		@Override
		public List<PointOfInterest> getPointsOfInterest() {
			return pointsOfInterest;
		}

	}

}
