package com.threeamigos.mandelbrot.interfaces.persister;

import java.util.List;

import com.threeamigos.mandelbrot.interfaces.service.PointOfInterest;

public interface PointsOfInterestPersister {

	public PersistResult savePointsOfInterest(List<PointOfInterest> pointsOfInterest);

	public PersistResult loadPointsOfInterest();

	public List<PointOfInterest> getPointsOfInterest();

	public String getFilename();

}
