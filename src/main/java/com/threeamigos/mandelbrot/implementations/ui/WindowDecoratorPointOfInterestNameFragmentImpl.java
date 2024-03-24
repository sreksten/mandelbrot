package com.threeamigos.mandelbrot.implementations.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import com.threeamigos.common.util.interfaces.ui.FontService;
import com.threeamigos.mandelbrot.interfaces.service.PointOfInterest;
import com.threeamigos.mandelbrot.interfaces.service.PointsOfInterestService;
import com.threeamigos.mandelbrot.interfaces.ui.Resolution;
import com.threeamigos.mandelbrot.interfaces.ui.WindowDecoratorPointOfInterestNameFragment;

public class WindowDecoratorPointOfInterestNameFragmentImpl extends WindowDecoratorFragmentImpl
		implements WindowDecoratorPointOfInterestNameFragment {

	private final PointsOfInterestService pointsOfInterestService;

	private Integer currentPointOfInterestIndex;

	public WindowDecoratorPointOfInterestNameFragmentImpl(Resolution resolution, FontService fontService,
			PointsOfInterestService pointsOfInterestService) {
		super(resolution);
		this.pointsOfInterestService = pointsOfInterestService;

		fontHeight = getHeight() / 20;
		font = fontService.getFont(FontService.STANDARD_FONT_NAME, Font.BOLD | Font.ITALIC, fontHeight);
	}

	@Override
	public int paint(Graphics2D graphics, int xCoord, int yCoord) {
		if (isActive() && currentPointOfInterestIndex != null) {
			PointOfInterest pointOfInterest = pointsOfInterestService.getElements()
					.get(currentPointOfInterestIndex - 1);
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
