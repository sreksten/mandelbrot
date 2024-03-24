package com.threeamigos.mandelbrot.implementations.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;

import com.threeamigos.common.util.interfaces.ui.FontService;
import com.threeamigos.mandelbrot.interfaces.service.FractalType;
import com.threeamigos.mandelbrot.interfaces.service.PointOfInterest;
import com.threeamigos.mandelbrot.interfaces.service.Points;
import com.threeamigos.mandelbrot.interfaces.service.PointsOfInterestService;
import com.threeamigos.mandelbrot.interfaces.ui.RenderableConsumer;

public class JuliaBoundariesServiceImpl implements RenderableConsumer {

	private final Points points;
	private final PointsOfInterestService pointsOfInterestService;
	private final int maxWidth;
	private final int maxHeight;
	private final int centerX;
	private final int centerY;
	private final int diameter;
	private final int radius;
	private final int halfRadius;
	private final int crossWidth;

	private final int fontHeight;
	private final Font font;

	private Image image;

	private boolean active;
	private Integer cursorX;
	private Integer cursorY;
	private double cr;
	private double ci;

	public JuliaBoundariesServiceImpl(Points points, FontService fontService,
			PointsOfInterestService pointsOfInterestService) {
		this.points = points;
		this.pointsOfInterestService = pointsOfInterestService;
		maxWidth = points.getWidth();
		maxHeight = points.getHeight();
		centerX = maxWidth / 2;
		centerY = maxHeight / 2;
		diameter = (maxHeight * 80 / 100 + 19) / 20 * 20;
		radius = diameter >> 1;
		halfRadius = diameter >> 2;
		crossWidth = radius * 2 + 20;

		fontHeight = maxWidth == ResolutionEnum.SD.getWidth() ? 12 : 16;
		font = fontService.getFont(FontService.STANDARD_FONT_NAME, Font.BOLD, fontHeight);
	}

	public int getDiameter() {
		return diameter;
	}

	public void setImage(Image image) {
		this.image = image;
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
			e.consume();
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
			if (active && cursorX != null) {
				recalcCAndStartCalculation();
			}
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

		Composite composite = graphics.getComposite();
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f);
		graphics.setComposite(ac);
		graphics.drawImage(image, centerX - radius, centerY - radius, null);
		graphics.setComposite(composite);

		graphics.setColor(points.isJuliaConnected() ? Color.WHITE : Color.YELLOW);
		graphics.drawOval(centerX - radius, centerY - radius, diameter, diameter);
		graphics.drawLine(centerX - crossWidth / 2, centerY, centerX + crossWidth / 2, centerY);
		graphics.drawLine(centerX, centerY - crossWidth / 2, centerX, centerY + crossWidth / 2);

		graphics.setFont(font);
		graphics.drawString("-2", centerX - radius, centerY + fontHeight);
		graphics.drawString("-1", centerX - halfRadius, centerY + fontHeight);
		graphics.drawString("0", centerX, centerY + fontHeight);
		graphics.drawString("+1", centerX + halfRadius, centerY + fontHeight);
		graphics.drawString("+2", centerX + radius, centerY + fontHeight);

		graphics.drawString("-2i", centerX, centerY + radius);
		graphics.drawString("-i", centerX, centerY + halfRadius);
		graphics.drawString("+i", centerX, centerY - halfRadius);
		graphics.drawString("+2i", centerX, centerY - radius);

		for (PointOfInterest pointOfInterest : pointsOfInterestService.getElements()) {
			if (pointOfInterest.getFractalType() == FractalType.JULIA) {
				int pointX = centerX + (int) (halfRadius * pointOfInterest.getJuliaCReal());
				int pointY = centerY + (int) (halfRadius * pointOfInterest.getJuliaCImaginary());
				graphics.drawLine(pointX - 5, pointY - 5, pointX + 5, pointY + 5);
				graphics.drawLine(pointX + 5, pointY - 5, pointX - 5, pointY + 5);
			}
		}

		if (cursorX != null) {
			String number = String.format("(%1.3f,%1.3fi)", cr, ci);
			int numberX;
			if (cursorX > centerX) {
				numberX = cursorX + 4;
			} else {
				FontMetrics fontMetrics = graphics.getFontMetrics();
				numberX = cursorX - 4 - fontMetrics.stringWidth(number);
			}
			int numberY;
			if (cursorY < centerY) {
				numberY = cursorY - 4;
			} else {
				numberY = cursorY + fontHeight + 4;
			}
			graphics.drawString(number, numberX, numberY);

			drawArrow(graphics);
		}
	}

	private void calculateCursorCoordinates(int x, int y) {
		int dx = centerX - x;
		int dy = centerY - y;
		double distance = Math.sqrt((double) dx * dx + (double) dy * dy);
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

	private void drawArrow(Graphics2D graphics) {
		int dx = centerX - cursorX;
		int dy = centerY - cursorY;
		double distance = Math.sqrt((double) dx * dx + (double) dy * dy);
		if (distance > radius) {
			distance = radius;
		}

		if (distance > 10) {
			final int sz = 5;
			final int sz2 = sz * 2;
			final int diag = (int) Math.sqrt((sz * sz) << 1);
			final int distance_minus_diag = (int) distance - diag;

			Point[] vertexes = new Point[7];
			vertexes[0] = new Point(-sz, -sz);
			vertexes[1] = new Point(distance_minus_diag, -sz);
			vertexes[2] = new Point(distance_minus_diag, -sz2);
			vertexes[3] = new Point((int) distance, 0);
			vertexes[4] = new Point(distance_minus_diag, sz2);
			vertexes[5] = new Point(distance_minus_diag, sz);
			vertexes[6] = new Point(-sz, sz);

			Polygon arrow = new Polygon();
			for (int i = 0; i < vertexes.length; i++) {
				Point vertex = vertexes[i];
				arrow.addPoint(centerX + (int) vertex.getX(), centerY + (int) vertex.getY());
			}

			AffineTransform transform = new AffineTransform();
			transform.rotate(getAngle(distance), centerX, centerY);
			graphics.fill(transform.createTransformedShape(arrow));
		} else {
			graphics.drawLine(cursorX - 5, cursorY - 5, cursorX + 5, cursorY + 5);
			graphics.drawLine(cursorX + 5, cursorY - 5, cursorX - 5, cursorY + 5);
		}
	}

	private double getAngle(double distance) {
		double sin = Math.abs(centerY - cursorY) / distance;
		double angle = Math.asin(sin);
		if (cursorX > centerX) {
			if (cursorY < centerY) {
				angle = 2 * Math.PI - angle;
			}
		} else {
			if (cursorY > centerY) {
				angle = Math.PI - angle;
			} else {
				angle = Math.PI + angle;
			}
		}
		return angle;
	}

	private void recalcCAndStartCalculation() {
		cr = 2d * (cursorX - centerX) / radius;
		ci = 2d * (cursorY - centerY) / radius;
		points.setFractalType(FractalType.JULIA);
		points.setJuliaC(cr, ci);
		points.requestRecalculation();
	}

}
