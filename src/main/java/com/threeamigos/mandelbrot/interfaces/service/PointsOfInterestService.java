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

	public void setMessageNotifier(MessageNotifier messageNotifier);

	public PersistResult loadPointsOfInterest();

	public PersistResult savePointsOfInterest();

	public String getFilename();

	public int getCount();

	public PersistResult add(PointOfInterest pointOfInterest);

	public void remove(int index);

	public List<PointOfInterest> getElements();

}
