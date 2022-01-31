package com.threeamigos.mandelbrot.implementations.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.threeamigos.mandelbrot.implementations.persister.PointsOfInterestPersisterImpl;
import com.threeamigos.mandelbrot.interfaces.persister.PersistResult;
import com.threeamigos.mandelbrot.interfaces.persister.PointsOfInterestPersister;
import com.threeamigos.mandelbrot.interfaces.service.PointOfInterest;
import com.threeamigos.mandelbrot.interfaces.service.PointsOfInterestService;
import com.threeamigos.mandelbrot.interfaces.ui.MessageNotifier;

public class PointsOfInterestServiceImpl implements PointsOfInterestService {

	private List<PointOfInterest> pointsOfInterest;

	private MessageNotifier messageNotifier;

	PointsOfInterestPersister pointsOfInterestPersister = new PointsOfInterestPersisterImpl();

	@Override
	public void setMessageNotifier(MessageNotifier messageNotifier) {
		this.messageNotifier = messageNotifier;
	}

	@Override
	public PersistResult loadPointsOfInterest() {
		PersistResult persistResult = pointsOfInterestPersister.loadPointsOfInterest();
		if (persistResult.isSuccessful()) {
			pointsOfInterest = pointsOfInterestPersister.getPointsOfInterest();
		} else {
			messageNotifier.notify(persistResult.getError());
			pointsOfInterest = new ArrayList<>();
		}
		return persistResult;
	}

	@Override
	public PersistResult savePointsOfInterest() {
		PersistResult persistResult = pointsOfInterestPersister.savePointsOfInterest(pointsOfInterest);
		if (messageNotifier != null) {
			if (persistResult.isSuccessful()) {
				messageNotifier.notify("Points of interest saved in " + persistResult.getFilename());
			} else {
				messageNotifier.notify("Error while saving points of interest: " + persistResult.getError());
			}
		}
		return persistResult;
	}

	@Override
	public String getFilename() {
		return pointsOfInterestPersister.getFilename();
	}

	@Override
	public int getCount() {
		return pointsOfInterest.size();
	}

	@Override
	public PersistResult add(PointOfInterest pointOfInterest) {
		if (getCount() < 100) {
			String name = messageNotifier.request("Give it a name:");
			if (name != null && name.trim().length() > 0) {
				pointOfInterest.setName(name);
				pointsOfInterest.add(pointOfInterest);
				return savePointsOfInterest();
			}
		}
		return null;
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
