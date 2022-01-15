package com.threeamigos.mandelbrot;

public enum Resolution {

	ULTRA_HD("Ultra HD", 3840, 2160),
	QUAD_HD("Quad HD", 2560, 1440),
	FULL_HD("Full HD", 1920, 1080),
	SXGA("SXGA", 1280, 1024),
	HD("HD", 1280, 720),
	SD("SD", 640, 480);

	private String name;
	private int width;
	private int height;

	Resolution(String name, int width, int height) {
		this.name = name;
		this.width = width;
		this.height = height;
	}

	public String getName() {
		return name;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public String toString() {
		return String.format("%s (%d x %d)", name, width, height);
	}

}
