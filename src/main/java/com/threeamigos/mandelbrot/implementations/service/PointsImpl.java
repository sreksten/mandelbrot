package com.threeamigos.mandelbrot.implementations.service;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.threeamigos.mandelbrot.interfaces.service.FractalService;
import com.threeamigos.mandelbrot.interfaces.service.FractalType;
import com.threeamigos.mandelbrot.interfaces.service.PointOfInterest;
import com.threeamigos.mandelbrot.interfaces.service.Points;
import com.threeamigos.mandelbrot.interfaces.ui.Resolution;

public class PointsImpl implements Points {

	private static final double JUNCTION_BETWEEN_CARDIOID_AND_PERIOD2BULB = -0.75d;

	private static final double DEFAULT_MIN_X = -2.0d;
	private static final double DEFAULT_MAX_X = 0.47d;

	private static final double DEFAULT_MIN_Y = -1.12d;
	private static final double DEFAULT_MAX_Y = 1.12d;

	private static final double ZOOM_OUT_FACTOR = 1.1111111d;
	private static final double ZOOM_IN_FACTOR = 0.9d;

	private final PropertyChangeSupport propertyChangeSupport;

	private int width;
	private int height;

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

	private FractalType fractalType = FractalType.MANDELBROT;

	private double juliaCReal = 0.3d;
	private double juliaCImaginary = -0.01;
	private boolean juliaConnected;

	private PointsImpl() {
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}

	public PointsImpl(Resolution resolution) {
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		setResolution(resolution);
	}

	@Override
	public Points adaptToResolution(Resolution resolution) {
		PointsImpl newPoint = new PointsImpl();
		newPoint.width = resolution.getWidth();
		newPoint.height = resolution.getHeight();

		newPoint.minY = minY;
		newPoint.maxY = maxY;
		newPoint.calculateStepY();

		double halfWidth = (newPoint.stepY * resolution.getWidth()) / 2.0d;
		double centerX = (maxX - minX) / 2.0d + minX;

		newPoint.minX = centerX - halfWidth;
		newPoint.maxX = centerX + halfWidth;
		newPoint.calculateStepX();

		return newPoint;
	}

	@Override
	public void setResolution(Resolution resolution) {
		this.width = resolution.getWidth();
		this.height = resolution.getHeight();

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
	public PointOfInterest getPointOfInterest(int maxIterations) {
		if (fractalType == FractalType.MANDELBROT) {
			return new PointOfInterestImpl("New point", getMinY(), getMaxY(), getCentralX(), getZoomCount(),
					maxIterations);
		} else {
			return new PointOfInterestImpl("New point", getMinY(), getMaxY(), getCentralX(), getZoomCount(),
					maxIterations, juliaCReal, juliaCImaginary);
		}
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

		zoomCount = pointOfInterest.getZoomCount();
		zoomFactor = calculateZoomFactor(zoomCount);

		fractalType = pointOfInterest.getFractalType();

		juliaCReal = pointOfInterest.getJuliaCReal();
		juliaCImaginary = pointOfInterest.getJuliaCImaginary();
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
	public int zoomOutSegmentInPixel(int length) {
		return (int) (length * ZOOM_IN_FACTOR);
	}

	@Override
	public boolean zoomIn(int x, int y) {
		return zoom(x, y, ZOOM_IN_FACTOR);
	}

	@Override
	public boolean zoomOut(int x, int y) {
		return zoom(x, y, ZOOM_OUT_FACTOR);
	}

	private boolean zoom(int x, int y, double ratio) {

		if (zoomCount >= 275 && ratio < 1.0d || zoomCount <= -6 && ratio > 1.0d) {
			return false;
		}

		if (ratio > 1.0d) {
			zoomCount--;
		} else {
			zoomCount++;
		}

		this.zoomFactor = calculateZoomFactor(zoomCount);

		double percentageX = (double) x / (double) width;
		double newIntervalWidth = (startingMaxX - startingMinX) * this.zoomFactor;
		minX = getRealCoordinate(x) - newIntervalWidth * percentageX;
		maxX = minX + newIntervalWidth;
		calculateStepX();

		double percentageY = (double) y / (double) height;
		double newIntervalHeight = (startingMaxY - startingMinY) * this.zoomFactor;
		double pointImaginaryCoord = minY + (maxY - minY) * percentageY;
		minY = pointImaginaryCoord - newIntervalHeight * percentageY;
		maxY = minY + newIntervalHeight;
		calculateStepY();

		return true;
	}

	private double calculateZoomFactor(int zoomCount) {
		return Math.pow(ZOOM_IN_FACTOR, zoomCount);
	}

	@Override
	public void updatePointerCoordinates(Integer x, Integer y) {
		if (x == null || x < 0 || x >= width || y == null || y < 0 || y >= height) {
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
		double percentageX = (double) x / (double) width;
		double intervalX = maxX - minX;
		return minX + intervalX * percentageX;
	}

	private double getImaginaryCoordinate(int y) {
		double percentageY = (double) y / (double) height;
		double intervalY = maxY - minY;
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
	public Points copy() {
		PointsImpl copy = new PointsImpl();
		copy.height = height;
		copy.maxX = maxX;
		copy.maxY = maxY;
		copy.minX = minX;
		copy.minY = minY;
		copy.stepX = stepX;
		copy.stepY = stepY;
		copy.width = width;
		copy.zoomCount = zoomCount;
		copy.zoomFactor = zoomFactor;
		copy.fractalType = fractalType;
		copy.juliaCReal = juliaCReal;
		copy.juliaCImaginary = juliaCImaginary;
		return copy;
	}

	@Override
	public void setFractalType(FractalType fractalType) {
		this.fractalType = fractalType;
	}

	@Override
	public FractalType getFractalType() {
		return fractalType;
	}

	@Override
	public void setJuliaC(double juliaCReal, double juliaCImaginary) {
		this.juliaCReal = juliaCReal;
		this.juliaCImaginary = juliaCImaginary;
		this.juliaConnected = belongsToMandelbrotSet(juliaCReal, juliaCImaginary, 8192);
	}

	@Override
	public double getJuliaCReal() {
		return juliaCReal;
	}

	@Override
	public double getJuliaCImaginary() {
		return juliaCImaginary;
	}

	@Override
	public boolean isJuliaConnected() {
		return juliaConnected;
	}

	private boolean belongsToMandelbrotSet(double cReal, double cImaginary, int maxIterations) {
		double real = 0;
		double imaginary = 0;
		double tempReal;

		double real2;
		double imaginary2;

		double realOld = 0.0d;
		double imaginaryOld = 0.0d;
		int period = 0;

		for (int iteration = 0; iteration < maxIterations; iteration++) {
			real2 = real * real;
			imaginary2 = imaginary * imaginary;

			if (real2 + imaginary2 > 4.0) {
				return false;
			}

			// Z=Z*Z+C
			tempReal = real2 - imaginary2 + cReal;
			imaginary = (real + real) * imaginary + cImaginary; // 2.0 * real * imaginary
			real = tempReal;

			if (real == realOld && imaginary == imaginaryOld) {
				return true;
			}

			period++;
			if (period > 20) {
				period = 0;
				realOld = real;
				imaginaryOld = imaginary;
			}
		}

		return true;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		propertyChangeSupport.addPropertyChangeListener(pcl);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		propertyChangeSupport.removePropertyChangeListener(pcl);
	}

	@Override
	public void requestRecalculation() {
		propertyChangeSupport.firePropertyChange(FractalService.CALCULATION_RESTART_REQUIRED_PROPERTY_CHANGE, null,
				null);
	}
}
