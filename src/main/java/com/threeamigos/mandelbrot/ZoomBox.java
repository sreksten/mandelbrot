package com.threeamigos.mandelbrot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.threeamigos.mandelbrot.interfaces.service.Points;

public class ZoomBox {

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

	public ZoomBox(Points points) {
		this.points = points;
		maxWidth = points.getWidth();
		maxHeight = points.getHeight();
		ratio = (double) maxWidth / (double) maxHeight;
	}

	public boolean mousePressed(MouseEvent e) {
		startX = e.getX();
		startY = e.getY();
		dragging = true;
		return false;
	}

	public boolean mouseDragged(MouseEvent e) {
		if (dragging) {
			endX = e.getX();
			if (endX < 0) {
				endX = 0;
			} else if (endX >= maxWidth - 1) {
				endX = maxWidth - 1;
			}
			endY = e.getY();
			if (endY < 0) {
				endY = 0;
			} else if (endY >= maxHeight - 1) {
				endY = maxHeight - 1;
			}

			if (startX < endX) {

				rectangleStartX = startX;
				rectangleWidth = endX - startX;

				if (startY < endY) {

					rectangleStartY = startY;
					rectangleHeight = (int) (rectangleWidth / ratio);
					int maxRectangleHeight = maxHeight - startY;
					if (rectangleHeight > maxRectangleHeight) {
						rectangleHeight = maxRectangleHeight;
						rectangleWidth = (int) (rectangleHeight * ratio);
					}

				} else {

					rectangleHeight = (int) (rectangleWidth / ratio);
					int maxRectangleHeight = startY;
					if (rectangleHeight > maxRectangleHeight) {
						rectangleHeight = maxRectangleHeight;
						rectangleWidth = (int) (rectangleHeight * ratio);
					}

					rectangleStartY = startY - rectangleHeight;
				}

			} else {

				rectangleStartX = endX;
				rectangleWidth = startX - endX;

				if (startY < endY) {

					rectangleStartY = startY;
					rectangleHeight = (int) (rectangleWidth / ratio);
					int maxRectangleHeight = maxHeight - startY;
					if (rectangleHeight > maxRectangleHeight) {
						rectangleHeight = maxRectangleHeight;
						rectangleWidth = (int) (rectangleHeight * ratio);
					}

				} else {

					rectangleHeight = (int) (rectangleWidth / ratio);
					int maxRectangleHeight = startY;
					if (rectangleHeight > maxRectangleHeight) {
						rectangleHeight = maxRectangleHeight;
						rectangleWidth = (int) (rectangleHeight * ratio);
					}

					rectangleStartY = startY - rectangleHeight;

				}

				rectangleStartX = startX - rectangleWidth;

			}

			if (rectangleWidth > 10 || rectangleHeight > 10) {
				hasValidRectangle = true;
			}

			return true;

		} else {
			reset();
			return false;
		}
	}

	public boolean mouseReleased(MouseEvent e) {
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
			reset();
			return true;
		} else {
			return false;
		}

	}

	public void reset() {
		startX = endX = startY = endY = rectangleWidth = rectangleHeight = 0;
		dragging = false;
		hasValidRectangle = false;
	}

	public boolean hasValidRectangle() {
		return hasValidRectangle;
	}

	public void draw(Graphics2D graphics) {
		if (hasValidRectangle) {
			graphics.setColor(Color.WHITE);
			graphics.drawRect(rectangleStartX, rectangleStartY, rectangleWidth, rectangleHeight);
		}
	}

	public boolean keyTyped(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			reset();
			return true;
		}
		return false;
	}

}
