package com.threeamigos.mandelbrot.implementations;

import com.threeamigos.mandelbrot.interfaces.DataBuffer;
import com.threeamigos.mandelbrot.interfaces.MandelbrotCalculator;
import com.threeamigos.mandelbrot.interfaces.PointsInfo;
import com.threeamigos.mandelbrot.interfaces.PointsOfInterest;
import com.threeamigos.mandelbrot.interfaces.Registry;

public class RegistryImpl implements Registry {

	private int width;
	private int height;
	private PointsInfo pointsInfo;
	private DataBuffer dataBuffer;
	private MandelbrotCalculator calculator;
	private DirectColorModelImageProducer directColorModelImageProducer;
	private IndexColorModelImageProducer indexColorModelImageProducer;
	private PointsOfInterest pointsOfInterest;

	public RegistryImpl(int width, int height) {

		this.width = width;
		this.height = height;

		pointsInfo = new PointsInfoImpl();
		pointsInfo.setDimensions(width, height);

		dataBuffer = new DataBufferImpl();
		dataBuffer.setDimensions(width, height);

		calculator = new MultithreadedMandelbrotCalculator();

		directColorModelImageProducer = new DirectColorModelImageProducer();
		indexColorModelImageProducer = new IndexColorModelImageProducer();

		pointsOfInterest = new ResourceBasedPointsOfInterest("/points_of_interest.txt");

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
	public PointsInfo getPointsInfo() {
		return pointsInfo;
	}

	@Override
	public DataBuffer getDataBuffer() {
		return dataBuffer;
	}

	@Override
	public MandelbrotCalculator getCalculator() {
		return calculator;
	}

	@Override
	public DirectColorModelImageProducer getDirectColorModelImageProducer() {
		return directColorModelImageProducer;
	}

	@Override
	public IndexColorModelImageProducer getIndexColorModelImageProducer() {
		return indexColorModelImageProducer;
	}

	@Override
	public PointsOfInterest getPointsOfInterest() {
		return pointsOfInterest;
	}

}
