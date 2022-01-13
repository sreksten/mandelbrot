package com.threeamigos.mandelbrot.implementations;

import java.util.ArrayList;

import com.threeamigos.mandelbrot.interfaces.DataBuffer;
import com.threeamigos.mandelbrot.interfaces.DataPersister;
import com.threeamigos.mandelbrot.interfaces.DataPersister.PersistResult;
import com.threeamigos.mandelbrot.interfaces.MandelbrotCalculator;
import com.threeamigos.mandelbrot.interfaces.MultipleVariantImageProducer;
import com.threeamigos.mandelbrot.interfaces.PointsInfo;
import com.threeamigos.mandelbrot.interfaces.PointsOfInterest;
import com.threeamigos.mandelbrot.interfaces.Registry;

public class RegistryImpl implements Registry {

	private int width;
	private int height;
	private PointsInfo pointsInfo;
	private DataBuffer dataBuffer;
	private MandelbrotCalculator calculator;
	private MultipleVariantImageProducer imageProducer;
	private PointsOfInterest pointsOfInterest;
	private DataPersister dataPersister;

	public RegistryImpl(int width, int height) {

		this.width = width;
		this.height = height;

		pointsInfo = new PointsInfoImpl();
		pointsInfo.setDimensions(width, height);

		dataBuffer = new DataBufferImpl();
		dataBuffer.setDimensions(width, height);

		calculator = new MultithreadedMandelbrotCalculator();

		imageProducer = new ImageProducerImpl();

		dataPersister = new DiskPersister();

		PersistResult result = dataPersister.loadPointsOfInterest(PointsOfInterest.POINTS_OF_INTEREST_FILENAME);
		if (result.isSuccessful()) {
			pointsOfInterest = new PointsOfInterestImpl(result.getPointsOfInterest());
		} else {
			pointsOfInterest = new PointsOfInterestImpl(new ArrayList<>());
		}
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
	public MultipleVariantImageProducer getImageProducer() {
		return imageProducer;
	}

	@Override
	public PointsOfInterest getPointsOfInterest() {
		return pointsOfInterest;
	}

	@Override
	public DataPersister getDataPersister() {
		return dataPersister;
	}

}
