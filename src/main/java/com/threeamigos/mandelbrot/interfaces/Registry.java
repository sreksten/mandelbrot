package com.threeamigos.mandelbrot.interfaces;

public interface Registry {

	public int getWidth();

	public int getHeight();

	public PointsInfo getPointsInfo();

	public DataBuffer getDataBuffer();

	public MandelbrotCalculator getCalculator();

	public MultipleVariantImageProducer getImageProducer();

	public PointsOfInterest getPointsOfInterest();

	public DataPersister getDataPersister();

}
