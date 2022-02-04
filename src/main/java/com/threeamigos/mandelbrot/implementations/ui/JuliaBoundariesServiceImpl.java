package com.threeamigos.mandelbrot.implementations.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import com.threeamigos.mandelbrot.interfaces.service.FractalType;
import com.threeamigos.mandelbrot.interfaces.service.PointOfInterest;
import com.threeamigos.mandelbrot.interfaces.service.Points;
import com.threeamigos.mandelbrot.interfaces.service.PointsOfInterestService;
import com.threeamigos.mandelbrot.interfaces.ui.RenderableConsumer;

public class JuliaBoundariesServiceImpl implements RenderableConsumer {

	final Points points;
	final PointsOfInterestService pointsOfInterestService;
	final int maxWidth;
	final int maxHeight;
	final int centerX;
	final int centerY;
	final int diameter;
	final int radius;
	final int crossWidth;

	private boolean active;
	private Integer cursorX;
	private Integer cursorY;

	public JuliaBoundariesServiceImpl(Points points, PointsOfInterestService pointsOfInterestService) {
		this.points = points;
		this.pointsOfInterestService = pointsOfInterestService;
		maxWidth = points.getWidth();
		maxHeight = points.getHeight();
		centerX = maxWidth / 2;
		centerY = maxHeight / 2;
		diameter = (maxHeight * 80 / 100 + 19) / 20 * 20;
		radius = diameter / 2;
		crossWidth = radius * 2 + 20;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// We won't follow this event
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (active) {
			recalcCAndStartCalculation();
			e.consume();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// We won't follow this event
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// We won't follow this event
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// We won't follow this event
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (active) {
			calculateCursorCoordinates(e.getX(), e.getY());
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (active) {
			cursorX = null;
			cursorY = null;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (active) {
			calculateCursorCoordinates(e.getX(), e.getY());
			recalcCAndStartCalculation();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// We won't follow this event
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_T) {
			active = !active;
			e.consume();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// We won't follow this event
	}

	@Override
	public void paint(Graphics2D graphics) {
		if (!active) {
			return;
		}
		graphics.setColor(Color.WHITE);
		graphics.drawOval(centerX - radius, centerY - radius, diameter, diameter);
		graphics.drawLine(centerX - crossWidth / 2, centerY, centerX + crossWidth / 2, centerY);
		graphics.drawLine(centerX, centerY - crossWidth / 2, centerX, centerY + crossWidth / 2);

		for (PointOfInterest pointOfInterest : pointsOfInterestService.getElements()) {
			if (pointOfInterest.getFractalType() == FractalType.JULIA) {
				double cr = pointOfInterest.getJuliaCReal();
				double ci = pointOfInterest.getJuliaCImaginary();
				int halfRadius = radius / 2;
				int pointX = centerX + (int) (halfRadius * cr);
				int pointY = centerY + (int) (halfRadius * ci);
				graphics.drawLine(pointX - 5, pointY - 5, pointX + 5, pointY + 5);
				graphics.drawLine(pointX + 5, pointY - 5, pointX - 5, pointY + 5);
			}
		}

		if (cursorX != null) {
			graphics.drawLine(cursorX - 5, cursorY - 5, cursorX + 5, cursorY + 5);
			graphics.drawLine(cursorX + 5, cursorY - 5, cursorX - 5, cursorY + 5);
		}
	}

	private void calculateCursorCoordinates(int x, int y) {
		int dx = centerX - x;
		int dy = centerY - y;
		double distance = Math.sqrt(dx * dx + dy * dy);
		if (distance > radius) {
			double sin = (y - centerY) / distance;
			double cos = (x - centerX) / distance;
			cursorY = centerY + (int) (sin * radius);
			cursorX = centerX + (int) (cos * radius);
		} else {
			cursorX = x;
			cursorY = y;
		}
	}

	private void recalcCAndStartCalculation() {
		double cr = 2d * (cursorX - centerX) / radius;
		double ci = 2d * (cursorY - centerY) / radius;
		points.setFractalType(FractalType.JULIA);
		points.setJuliaC(cr, ci);
		points.requestRecalculation();
	}

}
