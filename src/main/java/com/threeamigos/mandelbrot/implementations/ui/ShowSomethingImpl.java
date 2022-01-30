package com.threeamigos.mandelbrot.implementations.ui;

import java.awt.Color;
import java.awt.Graphics2D;

import com.threeamigos.mandelbrot.Resolution;
import com.threeamigos.mandelbrot.interfaces.ui.ShowSomething;

abstract class ShowSomethingImpl implements ShowSomething {

	protected static final String FONT_NAME = "Serif";

	private boolean active = true;

	private int width;
	private int height;

	ShowSomethingImpl(Resolution resolution) {
		width = resolution.getWidth();
		height = resolution.getHeight();
	}

	@Override
	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void toggleActive() {
		active = !active;
	}

	protected int getWidth() {
		return width;
	}

	protected int getHeight() {
		return height;
	}

	protected void drawString(Graphics2D graphics, String s, int x, int y) {
		drawString(graphics, s, x, y, Color.WHITE);
	}

	protected void drawString(Graphics2D graphics, String s, int x, int y, Color color) {
		graphics.setColor(Color.BLACK);
		for (int i = x - 1; i <= x + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++) {
				graphics.drawString(s, i, j);
			}
		}
		graphics.setColor(color);
		graphics.drawString(s, x, y);
	}

}
