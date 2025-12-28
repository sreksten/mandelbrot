package com.threeamigos.mandelbrot.interfaces.persister;

import java.util.List;

import com.threeamigos.common.util.interfaces.persistence.PersistResult;
import com.threeamigos.common.util.interfaces.persistence.file.FilePersistResult;
import com.threeamigos.mandelbrot.interfaces.service.PointOfInterest;

/**
 * An object that loads and saves points of interest from/to disk
 *
 * @author Stefano Reksten
 *
 */
public interface PointsOfInterestPersister {

	FilePersistResult savePointsOfInterest(List<PointOfInterest> pointsOfInterest);

	PersistResult loadPointsOfInterest();

	List<PointOfInterest> getPointsOfInterest();

	String getFilename();

}
