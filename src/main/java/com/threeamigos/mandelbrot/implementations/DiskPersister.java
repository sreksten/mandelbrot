package com.threeamigos.mandelbrot.implementations;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.threeamigos.mandelbrot.interfaces.DataPersister;
import com.threeamigos.mandelbrot.interfaces.PointOfInterest;
import com.threeamigos.mandelbrot.interfaces.PointsOfInterest;

public class DiskPersister implements DataPersister {

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

	private String getPointsOfInterestPath() {
		String path = new StringBuilder(System.getProperty("user.home")).append(File.separatorChar)
				.append(".com.threeamigos.mandelbrot").toString();
		new File(path).mkdirs();
		return path;
	}

	@Override
	public PersistResult savePointsOfInterest(PointsOfInterest pointsOfInterest, String filename) {
		try {
			String path = new StringBuilder(getPointsOfInterestPath()).append(File.separatorChar).append(filename)
					.toString();
			File outputFile = new File(path);
			PrintWriter printWriter = new PrintWriter(outputFile);

			for (PointOfInterest pointOfInterest : pointsOfInterest.getPointsOfInterest()) {
				printWriter.println(PointOfInterestCodec.toString(pointOfInterest));
			}

			printWriter.flush();
			printWriter.close();

			return new PersistResultImpl();

		} catch (IOException e) {
			return new PersistResultImpl("Error while saving points of interest: " + e.getMessage());
		}
	}

	@Override
	public PersistResult loadPointsOfInterest(String filename) {
		InputStream inputStream = null;
		try {
			String path = new StringBuilder(getPointsOfInterestPath()).append(File.separatorChar).append(filename)
					.toString();
			File inputFile = new File(path);
			if (inputFile.exists()) {
				inputStream = new FileInputStream(inputFile);
			} else {
				inputStream = this.getClass().getResourceAsStream("/" + filename);
			}

			if (inputStream == null) {
				return new PersistResultImpl("Cannot find " + filename);
			}

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
				String line;
				List<PointOfInterest> pointsOfInterest = new ArrayList<>();
				while ((line = reader.readLine()) != null) {
					PointOfInterest pointOfInterest = PointOfInterestCodec.parsePointOfInterest(line);
					pointsOfInterest.add(pointOfInterest);
				}
				return new PersistResultImpl(pointsOfInterest);
			}

		} catch (Exception e) {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e2) {
				}
			}
			return new PersistResultImpl("Error reading " + filename + ": " + e.getMessage());
		}
	}

	public class PersistResultImpl implements PersistResult {

		private boolean successful;

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
