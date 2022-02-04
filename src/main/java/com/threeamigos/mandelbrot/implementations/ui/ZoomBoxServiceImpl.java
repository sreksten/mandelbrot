package com.threeamigos.mandelbrot.implementations.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import com.threeamigos.mandelbrot.interfaces.service.Points;
import com.threeamigos.mandelbrot.interfaces.ui.RenderableConsumer;

public class ZoomBoxServiceImpl implements RenderableConsumer {

	final Points points;
	final int maxWidth;
	final int maxHeight;
	final double ratio;

	private int startX = -1;
	private int startY = -1;
	private int endX = -1;
	private int endY = -1;
	private boolean dragging = false;

	private int rectangleStartX;
	private int rectangleStartY;
	private int rectangleWidth;
	private int rectangleHeight;

	boolean hasValidRectangle = false;

	public ZoomBoxServiceImpl(Points points) {
		this.points = points;
		maxWidth = points.getWidth();
		maxHeight = points.getHeight();
		ratio = (double) maxWidth / (double) maxHeight;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// We won't follow this event
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// We won't follow this event
	}

	@Override
	public void mousePressed(MouseEvent e) {
		startX = e.getX();
		startY = e.getY();
		dragging = true;
		e.consume();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!dragging) {
			reset();
			return;
		}

		endX = checkWidthBoundaries(e.getX());
		endY = checkHeightBoundaries(e.getY());

		if (startX < endX) {
			rectangleStartX = startX;
			rectangleWidth = endX - startX;
			if (startY < endY) {
				checkRectangleLowerRight();
			} else {
				checkRectangleUpperRight();
			}
		} else {
			rectangleStartX = endX;
			rectangleWidth = startX - endX;
			if (startY < endY) {
				checkRectangleLowerRight();
			} else {
				checkRectangleUpperRight();
			}
			rectangleStartX = startX - rectangleWidth;
		}

		checkIfHasValidRectangle();
		e.consume();
	}

	private void checkRectangleUpperRight() {
		rectangleHeight = (int) (rectangleWidth / ratio);
		int maxRectangleHeight = startY;
		if (rectangleHeight > maxRectangleHeight) {
			rectangleHeight = maxRectangleHeight;
			rectangleWidth = (int) (rectangleHeight * ratio);
		}
		rectangleStartY = startY - rectangleHeight;
	}

	private void checkRectangleLowerRight() {
		rectangleStartY = startY;
		rectangleHeight = (int) (rectangleWidth / ratio);
		int maxRectangleHeight = maxHeight - startY;
		if (rectangleHeight > maxRectangleHeight) {
			rectangleHeight = maxRectangleHeight;
			rectangleWidth = (int) (rectangleHeight * ratio);
		}
	}

	private void checkIfHasValidRectangle() {
		if (rectangleWidth > 10 || rectangleHeight > 10) {
			hasValidRectangle = true;
		}
	}

	private int checkWidthBoundaries(int x) {
		if (x < 0) {
			x = 0;
		} else if (x >= maxWidth - 1) {
			x = maxWidth - 1;
		}
		return x;
	}

	private int checkHeightBoundaries(int y) {
		if (y < 0) {
			y = 0;
		} else if (y >= maxHeight - 1) {
			y = maxHeight - 1;
		}
		return y;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseDragged(e);
		dragging = false;
		if (hasValidRectangle) {
			int x = rectangleStartX + (rectangleWidth / 2);
			int y = rectangleStartY + (rectangleHeight / 2);

			int zoomCount = points.getZoomCount();
			int height = maxHeight;
			while (height > rectangleHeight) {
				height = (int) (height * 0.9d);
				zoomCount++;
			}
			points.changeCenterTo(x, y);
			for (int i = points.getZoomCount(); i < zoomCount; i++) {
				points.zoomIn(maxWidth / 2, maxHeight / 2);
			}
			points.requestRecalculation();
			reset();
			e.consume();
		}
	}

	private void reset() {
		startX = endX = startY = endY = rectangleWidth = rectangleHeight = 0;
		dragging = false;
		hasValidRectangle = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// We won't follow this event
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// We won't follow this event
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// We won't follow this event
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			reset();
			e.consume();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// We won't follow this event
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// We won't follow this event
	}

	@Override
	public void paint(Graphics2D graphics) {
		if (hasValidRectangle) {
			graphics.setColor(Color.WHITE);
			graphics.drawRect(rectangleStartX, rectangleStartY, rectangleWidth, rectangleHeight);
		}
	}

}
