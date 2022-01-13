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
	public List<PointOfInterest> getPointsOfInterest() {
		return pointsOfInterest;
	}

}
