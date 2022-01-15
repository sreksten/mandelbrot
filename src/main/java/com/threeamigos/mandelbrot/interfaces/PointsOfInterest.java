package com.threeamigos.mandelbrot.interfaces;

import java.util.List;

public interface PointsOfInterest {

	public int count();

	public void add(PointOfInterest pointOfInterest);

	public void remove(int index);

	public List<PointOfInterest> getElements();

}
