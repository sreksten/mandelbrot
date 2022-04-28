package com.threeamigos.mandelbrot.interfaces.ui;

import java.awt.Graphics2D;

/**
 * A component that renders a tiny bit of the main canvas.
 *
 * @author Stefano Reksten
 *
 */
public interface WindowDecoratorFragment {

	void setActive(boolean active);

	boolean isActive();

	void toggleActive();

	int paint(Graphics2D graphics, int xCoord, int yCoord);

}
