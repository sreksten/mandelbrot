package com.threeamigos.mandelbrot.interfaces.service;

import java.util.List;

import com.threeamigos.mandelbrot.interfaces.persister.PersistResult;
import com.threeamigos.mandelbrot.interfaces.ui.MessageNotifier;

/**
 * An object that handles storage and retrieval of points of interest
 *
 * @author Stefano Reksten
 *
 */
public interface PointsOfInterestService {

	void setMessageNotifier(MessageNotifier messageNotifier);

	PersistResult loadPointsOfInterest();

	PersistResult savePointsOfInterest();

	String getFilename();

	int getCount();

	PersistResult add(PointOfInterest pointOfInterest);

	void remove(int index);

	List<PointOfInterest> getElements();

}
