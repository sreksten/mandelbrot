package com.threeamigos.mandelbrot.interfaces.service;

import com.threeamigos.mandelbrot.Resolution;

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

	public int getPointerXCoordinate();

	public int getPointerYCoordinate();

	public Double getPointerRealcoordinate();

	public Double getPointerImaginaryCoordinate();

	public double toCReal(int x);

	public double toCImaginary(int y);

	public boolean isCardioidVisible();

	public boolean isCardioidVisible(int fromX, int toX, int fromY, int toY);

	public boolean isPeriod2BulbVisible();

	public boolean isPeriod2BulbVisible(int fromX, int toX, int fromY, int toY);

	public Points copy();
}
