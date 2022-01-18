package com.threeamigos.mandelbrot.implementations;

import com.threeamigos.mandelbrot.interfaces.PointOfInterest;
import com.threeamigos.mandelbrot.interfaces.PointsInfo;

public class PointsInfoImpl implements PointsInfo {

	private int width;
	private int height;

	private static final double JUNCTION_BETWEEN_CARDIOID_AND_PERIOD2BULB = -0.75d;

	private static final double DEFAULT_MIN_X = -2.0d;
	private static final double DEFAULT_MAX_X = 0.47d;

	private static final double DEFAULT_MIN_Y = -1.12d;
	private static final double DEFAULT_MAX_Y = 1.12d;

	private double startingMinX;
	private double startingMaxX;

	private double startingMinY;
	private double startingMaxY;

	private double minX;
	private double maxX;

	private double minY;
	private double maxY;

	private double stepX;
	private double stepY;

	private int zoomCount = 0;
	private double zoomFactor = 1.0d;

	private int xCoordinateUnderPointer;
	private int yCoordinateUnderPointer;

	private Double realCoordinateUnderPointer;
	private Double imaginaryCoordinateUnderPointer;

	@Override
	public PointsInfo adaptToDimensions(int width, int height) {
		PointsInfoImpl newPoint = new PointsInfoImpl();
		newPoint.width = width;
		newPoint.height = height;

		newPoint.minY = minY;
		newPoint.maxY = maxY;
		newPoint.calculateStepY();

		double halfWidth = (newPoint.stepY * width) / 2.0d;
		double centerX = (maxX - minX) / 2.0d + minX;

		newPoint.minX = centerX - halfWidth;
		newPoint.maxX = centerX + halfWidth;
		newPoint.calculateStepX();

		return newPoint;
	}

	@Override
	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;

		startingMinY = DEFAULT_MIN_Y;
		startingMaxY = DEFAULT_MAX_Y;

		minY = startingMinY;
		maxY = startingMaxY;
		calculateStepY();

		double halfWidth = (stepY * width) / 2.0d;
		startingMinX = JUNCTION_BETWEEN_CARDIOID_AND_PERIOD2BULB - halfWidth;
		startingMaxX = JUNCTION_BETWEEN_CARDIOID_AND_PERIOD2BULB + halfWidth;

		minX = startingMinX;
		maxX = startingMaxX;
		calculateStepX();
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
	public double getCentralX() {
		return (maxX - minX) / 2.0d + minX;
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
	public PointOfInterest getPointOfInterest(String name) {
		return new PointOfInterestImpl(name, getMinY(), getMaxY(), getCentralX(), getZoomCount());
	}

	@Override
	public void setPointOfInterest(PointOfInterest pointOfInterest) {
		minY = pointOfInterest.getMinImaginary();
		maxY = pointOfInterest.getMaxImaginary();
		calculateStepY();

		double halfWidth = (stepY * width) / 2.0d;
		minX = pointOfInterest.getCentralReal() - halfWidth;
		maxX = pointOfInterest.getCentralReal() + halfWidth;
		calculateStepX();

		this.zoomCount = pointOfInterest.getZoomCount();
		this.zoomFactor = calculateZoomFactor(zoomCount);
	}

	@Override
	public void reset() {
		minX = startingMinX;
		maxX = startingMaxX;
		calculateStepX();

		minY = startingMinY;
		maxY = startingMaxY;
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
	}

	private double calculateZoomFactor(int zoomCount) {
		return zoomCount < 0 ? Math.pow(0.9d, -zoomCount) : Math.pow(1.1d, zoomCount);
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
