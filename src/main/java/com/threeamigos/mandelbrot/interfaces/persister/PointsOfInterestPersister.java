package com.threeamigos.mandelbrot.interfaces.persister;

import java.util.List;

import com.threeamigos.mandelbrot.interfaces.service.PointOfInterest;

/**
 * An object that loads and saves points of interest from/to disk
 *
 * @author Stefano Reksten
 *
 */
public interface PointsOfInterestPersister {

	public PersistResult savePointsOfInterest(List<PointOfInterest> pointsOfInterest);

	public PersistResult loadPointsOfInterest();

	public List<PointOfInterest> getPointsOfInterest();

	public String getFilename();

}
