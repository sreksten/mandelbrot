package com.threeamigos.mandelbrot.interfaces.ui;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public interface ZoomBox {

	boolean mousePressed(MouseEvent e);

	boolean mouseDragged(MouseEvent e);

	boolean mouseReleased(MouseEvent e);

	void reset();

	boolean hasValidRectangle();

	void draw(Graphics2D graphics);

	boolean keyTyped(KeyEvent e);

}