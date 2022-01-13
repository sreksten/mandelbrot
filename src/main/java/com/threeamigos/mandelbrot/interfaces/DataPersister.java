package com.threeamigos.mandelbrot.interfaces;

import java.awt.Image;
import java.util.List;

public interface DataPersister {

	public PersistResult saveImage(Image image, String filename);

	public PersistResult savePointsOfInterest(PointsOfInterest pointsOfInterest, String filename);

	public PersistResult loadPointsOfInterest(String filename);

	public interface PersistResult {

		public boolean isSuccessful();

		public String getError();

		public List<PointOfInterest> getPointsOfInterest();

	}

}
