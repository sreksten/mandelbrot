package com.threeamigos.mandelbrot.interfaces;

import com.threeamigos.mandelbrot.implementations.DirectColorModelImageProducer;
import com.threeamigos.mandelbrot.implementations.IndexColorModelImageProducer;

public interface Registry {

	public int getWidth();

	public int getHeight();

	public PointsInfo getPointsInfo();

	public DataBuffer getDataBuffer();

	public MandelbrotCalculator getCalculator();

	public DirectColorModelImageProducer getDirectColorModelImageProducer();

	public IndexColorModelImageProducer getIndexColorModelImageProducer();

	public PointsOfInterest getPointsOfInterest();

}
