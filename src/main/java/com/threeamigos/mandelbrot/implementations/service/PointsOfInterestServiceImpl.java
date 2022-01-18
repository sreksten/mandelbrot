package com.threeamigos.mandelbrot.implementations.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.threeamigos.mandelbrot.implementations.persister.PointsOfInterestPersisterImpl;
import com.threeamigos.mandelbrot.interfaces.PointOfInterest;
import com.threeamigos.mandelbrot.interfaces.persister.PersistResult;
import com.threeamigos.mandelbrot.interfaces.persister.PointsOfInterestPersister;
import com.threeamigos.mandelbrot.interfaces.service.Notifier;
import com.threeamigos.mandelbrot.interfaces.service.PointsOfInterestService;

public class PointsOfInterestServiceImpl implements PointsOfInterestService {

	private List<PointOfInterest> pointsOfInterest;

	PointsOfInterestPersister pointsOfInterestPersister = new PointsOfInterestPersisterImpl();

	@Override
	public PersistResult loadPointsOfInterest(Notifier notifier) {
		PersistResult persistResult = pointsOfInterestPersister.loadPointsOfInterest();
		if (persistResult.isSuccessful()) {
			pointsOfInterest = pointsOfInterestPersister.getPointsOfInterest();
		} else {
			pointsOfInterest = new ArrayList<>();
			notifier.notify(persistResult.getError());
		}
		return persistResult;
	}

	@Override
	public PersistResult savePointsOfInterest(Notifier notifier) {
		PersistResult persistResult = pointsOfInterestPersister.savePointsOfInterest(pointsOfInterest);
		if (persistResult.isSuccessful()) {
			notifier.notify("Points of interest updated");
		} else {
			notifier.notify("Error while saving points of interest: " + persistResult.getError());
		}
		return persistResult;
	}

	@Override
	public int getCount() {
		return pointsOfInterest.size();
	}

	@Override
	public void add(PointOfInterest pointOfInterest) {
		pointsOfInterest.add(pointOfInterest);
	}

	@Override
	public void remove(int index) {
		pointsOfInterest.remove(index);
	}

	@Override
	public List<PointOfInterest> getElements() {
		return Collections.unmodifiableList(pointsOfInterest);
	}

}
