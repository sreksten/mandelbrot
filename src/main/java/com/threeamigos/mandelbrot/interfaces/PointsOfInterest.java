package com.threeamigos.mandelbrot.interfaces;

import java.io.PrintWriter;
import java.util.List;

public interface PointsOfInterest {

	public List<PointOfInterest> getPointsOfInterest();

	public void store(PointOfInterest pointOfInterest, PrintWriter printWriter);

}
