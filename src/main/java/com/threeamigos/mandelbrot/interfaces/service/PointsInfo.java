package com.threeamigos.mandelbrot.interfaces.service;

import com.threeamigos.mandelbrot.Resolution;

public interface PointsInfo {

	PointsInfo adaptToResolution(Resolution resolution);

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

	PointOfInterest getPointOfInterest();

	void setPointOfInterest(PointOfInterest pointOfInterest);

	void reset();

	void changeCenterTo(int x, int y);

	void zoom(int x, int y, double zoomFactor);

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
}
