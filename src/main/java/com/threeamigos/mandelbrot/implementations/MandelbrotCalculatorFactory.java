package com.threeamigos.mandelbrot.implementations;

import com.threeamigos.mandelbrot.interfaces.MandelbrotCalculator;
import com.threeamigos.mandelbrot.interfaces.MandelbrotCalculatorProducer;

public class MandelbrotCalculatorFactory implements MandelbrotCalculatorProducer {

	@Override
	public MandelbrotCalculator createInstance() {
		return new MultithreadedMandelbrotCalculator();
	}

}
