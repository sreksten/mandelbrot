package com.threeamigos.mandelbrot.implementations.service.fractal;

class SliceData {

	final int startX;
	final int startY;
	final int endX;
	final int endY;

	SliceData(int startX, int startY, int endX, int endY) {
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
	}

}
