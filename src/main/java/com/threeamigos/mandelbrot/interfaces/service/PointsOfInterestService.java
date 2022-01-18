package com.threeamigos.mandelbrot.interfaces.service;

import java.util.List;

import com.threeamigos.mandelbrot.interfaces.PointOfInterest;
import com.threeamigos.mandelbrot.interfaces.persister.PersistResult;

public interface PointsOfInterestService {

	public PersistResult loadPointsOfInterest(Notifier notifier);

	public PersistResult savePointsOfInterest(Notifier notifier);

	public int getCount();

	public void add(PointOfInterest pointOfInterest);

	public void remove(int index);

	public List<PointOfInterest> getElements();

}
