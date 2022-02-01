package com.threeamigos.mandelbrot.interfaces.ui;

import java.awt.Graphics2D;

public interface WindowDecoratorFragment {

	void setActive(boolean active);

	boolean isActive();

	void toggleActive();

	int paint(Graphics2D graphics, int xCoord, int yCoord);

}
