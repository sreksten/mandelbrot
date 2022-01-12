package com.threeamigos.mandelbrot.implementations;

import com.threeamigos.mandelbrot.interfaces.PointOfInterest;
import com.threeamigos.mandelbrot.interfaces.PointsInfo;

public class PointsInfoImpl implements PointsInfo {

	private int width;
	private int height;

	private static final double DEFAULT_MIN_X = -2.0d;
	private static final double DEFAULT_MAX_X = 0.47d;

	private static final double DEFAULT_MIN_Y = -1.12d;
	private static final double DEFAULT_MAX_Y = 1.12d;

	private double minX = DEFAULT_MIN_X;
	private double maxX = DEFAULT_MAX_X;

	private double minY = DEFAULT_MIN_Y;
	private double maxY = DEFAULT_MAX_Y;

	private double stepX;
	private double stepY;

	private int zoomCount = 0;
	private double zoomFactor = 1.0d;

	private int xCoordinateUnderPointer;
	private int yCoordinateUnderPointer;

	private Double realCoordinateUnderPointer;
	private Double imaginaryCoordinateUnderPointer;

	@Override
	public void setDimensions(int width, int height) {
		this.width = width;
		calculateStepX();
		this.height = height;
		calculateStepY();
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void setMinX(double minX) {
		this.minX = minX;
		calculateStepX();
	}

	@Override
	public double getMinX() {
		return minX;
	}

	@Override
	public void setMaxX(double maxX) {
		this.maxX = maxX;
		calculateStepX();
	}

	@Override
	public double getMaxX() {
		return maxX;
	}

	@Override
	public void setMinY(double minY) {
		this.minY = minY;
		calculateStepY();
	}

	@Override
	public double getMinY() {
		return minY;
	}

	@Override
	public void setMaxY(double maxY) {
		this.maxY = maxY;
		calculateStepY();
	}

	@Override
	public double getMaxY() {
		return maxY;
	}

	@Override
	public int getZoomCount() {
		return zoomCount;
	}

	@Override
	public double getZoomFactor() {
		return zoomFactor;
	}

	private void calculateStepX() {
		stepX = (maxX - minX) / width;
	}

	private void calculateStepY() {
		stepY = (maxY - minY) / height;
	}

	@Override
	public void setPointOfInterest(PointOfInterest pointOfInterest) {
		this.minX = pointOfInterest.getMinReal();
		this.maxX = pointOfInterest.getMaxReal();
		calculateStepX();
		this.minY = pointOfInterest.getMinImaginary();
		this.maxY = pointOfInterest.getMaxImaginary();
		calculateStepY();
		this.zoomCount = pointOfInterest.getZoomCount();
		this.zoomFactor = calculateZoomFactor(zoomCount);
	}

	@Override
	public void reset() {
		minX = DEFAULT_MIN_X;
		maxX = DEFAULT_MAX_X;
		calculateStepX();

		minY = DEFAULT_MIN_Y;
		maxY = DEFAULT_MAX_Y;
		calculateStepY();

		zoomCount = 0;
		zoomFactor = 1.0d;
	}

	@Override
	public void changeCenterTo(int x, int y) {
		double intervalX = maxX - minX;
		double percentageX = (double) x / (double) width;
		double newCenterX = minX + (intervalX * percentageX);
		minX = newCenterX - intervalX / 2.0d;
		maxX = minX + intervalX;

		double intervalY = maxY - minY;
		double percentageY = (double) y / (double) height;
		double newCenterY = minY + (intervalY * percentageY);
		minY = newCenterY - intervalY / 2.0d;
		maxY = minY + intervalY;

		checkInterval();
	}

	@Override
	public void zoom(int x, int y, double zoomFactor) {

		if (zoomCount >= 275 && zoomFactor < 1.0d || zoomCount <= -6 && zoomFactor > 1.0d) {
			return;
		}

		if (zoomFactor > 1.0d) {
			zoomCount--;
		} else {
			zoomCount++;
		}

		this.zoomFactor = calculateZoomFactor(zoomCount);

		double percentageX = (double) x / (double) width;
		double intervalWidth = maxX - minX;
		double pointRealCoord = minX + intervalWidth * percentageX;
		double newIntervalWidth = intervalWidth * zoomFactor;
		minX = pointRealCoord - newIntervalWidth * percentageX;
		maxX = minX + newIntervalWidth;
		calculateStepX();

		double percentageY = (double) y / (double) height;
		double intervalHeight = maxY - minY;
		double pointImaginaryCoord = minY + intervalHeight * percentageY;
		double newIntervalHeight = intervalHeight * zoomFactor;
		minY = pointImaginaryCoord - newIntervalHeight * percentageY;
		maxY = minY + newIntervalHeight;
		calculateStepY();

		checkInterval();
	}

	private double calculateZoomFactor(int zoomCount) {
		return zoomCount < 0 ? Math.pow(0.9d, -zoomCount) : Math.pow(1.1d, zoomCount);
	}

	private void checkInterval() {
		if (minX < -2.0d) {
			double interval = maxX - minX;
			minX = -2.0d;
			maxX = minX + interval;
		} else if (maxX > 2.0d) {
			double interval = maxX - minX;
			maxX = 2.0d;
			minX = maxX - interval;
		}

		if (minY < -2.0d) {
			double interval = maxY - minY;
			minY = -2.0d;
			maxY = minY + interval;
		} else if (maxY > 2.0d) {
			double interval = maxY - minY;
			maxY = 2.0d;
			minY = maxY - interval;
		}
	}

	@Override
	public void updatePointerCoordinates(Integer x, Integer y) {
		if (x == null || y == null) {
			realCoordinateUnderPointer = null;
			imaginaryCoordinateUnderPointer = null;
		} else {
			xCoordinateUnderPointer = x;
			yCoordinateUnderPointer = y;
			realCoordinateUnderPointer = getRealCoordinate(x);
			imaginaryCoordinateUnderPointer = getImaginaryCoordinate(y);
		}
	}

	private double getRealCoordinate(int x) {
		double intervalX = maxX - minX;
		double percentageX = (double) x / (double) width;
		return minX + intervalX * percentageX;
	}

	private double getImaginaryCoordinate(int y) {
		double intervalY = maxY - minY;
		double percentageY = (double) y / (double) height;
		return maxY - intervalY * percentageY;
	}

	@Override
	public int getPointerXCoordinate() {
		return xCoordinateUnderPointer;
	}

	@Override
	public int getPointerYCoordinate() {
		return yCoordinateUnderPointer;
	}

	@Override
	public Double getPointerRealcoordinate() {
		return realCoordinateUnderPointer;
	}

	@Override
	public Double getPointerImaginaryCoordinate() {
		return imaginaryCoordinateUnderPointer;
	}

	@Override
	public double toCReal(int x) {
		return minX + stepX * x;
	}

	@Override
	public double toCImaginary(int y) {
		return minY + stepY * y;
	}

	@Override
	public boolean isCardioidVisible() {
		return intervalsOverlap(minX, maxX, -0.75d, 0.375d) && intervalsOverlap(minY, maxY, -0.65d, 0.65d);
	}

	@Override
	public boolean isCardioidVisible(int fromX, int toX, int fromY, int toY) {
		double fromCReal = toCReal(fromX);
		double toCReal = toCReal(toX);
		double fromCImaginary = toCImaginary(fromY);
		double toCImaginary = toCImaginary(toY);
		return intervalsOverlap(fromCReal, toCReal, -0.75d, 0.375d)
				&& intervalsOverlap(fromCImaginary, toCImaginary, -0.65d, 0.65d);
	}

	@Override
	public boolean isPeriod2BulbVisible() {
		return intervalsOverlap(minX, maxX, -1.25d, -0.75d) && intervalsOverlap(minY, maxY, -0.25d, 0.25d);
	}

	@Override
	public boolean isPeriod2BulbVisible(int fromX, int toX, int fromY, int toY) {
		double fromCReal = toCReal(fromX);
		double toCReal = toCReal(toX);
		double fromCImaginary = toCImaginary(fromY);
		double toCImaginary = toCImaginary(toY);
		return intervalsOverlap(fromCReal, toCReal, -1.25d, -0.75d)
				&& intervalsOverlap(fromCImaginary, toCImaginary, -0.25d, 0.25d);
	}

	private boolean intervalsOverlap(double firstStart, double firstEnd, double secondStart, double secondEnd) {
		return firstEnd >= secondStart && firstStart <= secondEnd;

	}
}
