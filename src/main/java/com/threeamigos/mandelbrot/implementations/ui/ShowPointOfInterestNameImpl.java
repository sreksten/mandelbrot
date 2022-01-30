package com.threeamigos.mandelbrot.implementations.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import com.threeamigos.mandelbrot.Resolution;
import com.threeamigos.mandelbrot.interfaces.service.PointOfInterest;
import com.threeamigos.mandelbrot.interfaces.service.PointsOfInterestService;
import com.threeamigos.mandelbrot.interfaces.ui.ShowPointOfInterestName;

public class ShowPointOfInterestNameImpl extends ShowSomethingImpl implements ShowPointOfInterestName {

	private final PointsOfInterestService pointsOfInterestService;

	private Integer currentPointOfInterestIndex;

	public ShowPointOfInterestNameImpl(Resolution resolution, PointsOfInterestService pointsOfInterestService) {
		super(resolution);
		this.pointsOfInterestService = pointsOfInterestService;
	}

	@Override
	public int paint(Graphics2D graphics, int xCoord, int yCoord) {
		if (isActive() && currentPointOfInterestIndex != null) {
			PointOfInterest pointOfInterest = pointsOfInterestService.getElements()
					.get(currentPointOfInterestIndex - 1);
			int fontHeight = getHeight() / 20;
			Font font = new Font(FONT_NAME, Font.BOLD | Font.ITALIC, fontHeight);
			graphics.setFont(font);
			FontMetrics fontMetrics = graphics.getFontMetrics();
			drawString(graphics, pointOfInterest.getName(),
					getWidth() - 40 - fontMetrics.stringWidth(pointOfInterest.getName()), getHeight() - 40 - fontHeight,
					Color.YELLOW);
		}
		return yCoord;
	}

	@Override
	public void setCurrentPointOfInterestIndex(Integer currentPointOfInterestIndex) {
		this.currentPointOfInterestIndex = currentPointOfInterestIndex;
	}

}
