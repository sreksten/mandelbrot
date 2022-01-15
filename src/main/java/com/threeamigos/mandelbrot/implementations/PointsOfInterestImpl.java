package com.threeamigos.mandelbrot.implementations;

import java.util.List;

import com.threeamigos.mandelbrot.interfaces.PointOfInterest;
import com.threeamigos.mandelbrot.interfaces.PointsOfInterest;

public class PointsOfInterestImpl implements PointsOfInterest {

	private List<PointOfInterest> pointsOfInterest;

	public PointsOfInterestImpl(List<PointOfInterest> pointsOfInterest) {
		this.pointsOfInterest = pointsOfInterest;
	}

	@Override
	public int count() {
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
		return pointsOfInterest;
	}

}
