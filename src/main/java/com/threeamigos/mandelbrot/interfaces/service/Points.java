package com.threeamigos.mandelbrot.interfaces.service;

import com.threeamigos.common.util.interfaces.ui.Resolution;

import java.beans.PropertyChangeListener;

/**
 * An object that keeps track of the real and imaginary intervals of interest
 * (used to calculate the fractal image) and transforms them to screen pixel
 * coordinates, or zooms in and out, etc
 *
 * @author Stefano Reksten
 *
 */
public interface Points {

	Points adaptToResolution(Resolution resolution);

	void setResolution(Resolution resolution);

	int getWidth();

	int getHeight();

	void setMinX(double minX);

	double getMinX();

	void setMaxX(double maxX);

	double getMaxX();

	double getCentralX();

	void setMinY(double minY);

	double getMinY();

	void setMaxY(double maxY);

	double getMaxY();

	PointOfInterest getPointOfInterest(int maxIterations);

	void setPointOfInterest(PointOfInterest pointOfInterest);

	void reset();

	void changeCenterTo(int x, int y);

	int zoomOutSegmentInPixel(int width);

	boolean zoomIn(int x, int y);

	boolean zoomOut(int x, int y);

	int getZoomCount();

	double getZoomFactor();

	void updatePointerCoordinates(Integer x, Integer y);

	int getPointerXCoordinate();

	int getPointerYCoordinate();

	Double getPointerRealCoordinate();

	Double getPointerImaginaryCoordinate();

	double toCReal(int x);

	double toCImaginary(int y);

	Points copy();

	void setFractalType(FractalType fractalType);

	FractalType getFractalType();

	void setJuliaC(double juliaCReal, double juliaCImaginary);

	double getJuliaCReal();

	double getJuliaCImaginary();

	boolean isJuliaConnected();

	void addPropertyChangeListener(PropertyChangeListener pcl);

	void removePropertyChangeListener(PropertyChangeListener pcl);

	void requestRecalculation();

}
