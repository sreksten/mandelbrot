package com.threeamigos.mandelbrot.implementations.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import com.threeamigos.mandelbrot.Resolution;
import com.threeamigos.mandelbrot.interfaces.service.ImageProducerService;
import com.threeamigos.mandelbrot.interfaces.service.PointOfInterest;
import com.threeamigos.mandelbrot.interfaces.service.PointsOfInterestService;
import com.threeamigos.mandelbrot.interfaces.ui.FontService;
import com.threeamigos.mandelbrot.interfaces.ui.WindowDecoratorHelpFragment;

public class WindowDecoratorHelpFragmentImpl extends WindowDecoratorFragmentImpl
		implements WindowDecoratorHelpFragment {

	private final PointsOfInterestService pointsOfInterestService;

	private ImageProducerService imageProducerService;

	private Integer currentPointOfInterestIndex;

	public WindowDecoratorHelpFragmentImpl(Resolution resolution, FontService fontService,
			PointsOfInterestService pointsOfInterestService) {
		super(resolution);
		this.pointsOfInterestService = pointsOfInterestService;

		fontHeight = getWidth() == Resolution.SD.getWidth() ? 12 : 16;
		vSpacing = fontHeight + 4;
		font = fontService.getFont(FontService.STANDARD_FONT_NAME, Font.BOLD, fontHeight);
	}

	@Override
	public void setCurrentPointOfInterestIndex(Integer currentPointOfInterestIndex) {
		this.currentPointOfInterestIndex = currentPointOfInterestIndex;
	}

	@Override
	public void setImageProducerService(ImageProducerService imageProducerService) {
		this.imageProducerService = imageProducerService;
	}

	@Override
	public int paint(Graphics2D graphics, int xCoord, int yCoord) {
		if (isActive()) {
			graphics.setFont(font);
			if (pointsOfInterestService.getCount() < 10) {
				drawString(graphics, "A - add point of interest", xCoord, yCoord);
				yCoord += vSpacing;
			}
			drawString(graphics, String.format("C - change color model (current: %s)",
					imageProducerService.getCurrentColorModelName()), xCoord, yCoord);
			yCoord += vSpacing;
			if (currentPointOfInterestIndex != null) {
				drawString(graphics, "D - delete current point of interest", xCoord, yCoord);
				yCoord += vSpacing;
				drawString(graphics, "P - show or hide point of interest's name", xCoord, yCoord);
				yCoord += vSpacing;
			}
			drawString(graphics, "H - hide or show help", xCoord, yCoord);
			yCoord += vSpacing;
			drawString(graphics, "I - hide or show info", xCoord, yCoord);
			yCoord += vSpacing;
			drawString(graphics, "S - save snapshot", xCoord, yCoord);
			yCoord += vSpacing;
			drawString(graphics, "T - enter/exit target mode (for Julia fractals)", xCoord, yCoord);
			yCoord += vSpacing;
			drawString(graphics, "Mouse wheel - zoom in/out", xCoord, yCoord);
			yCoord += vSpacing;
			drawString(graphics, "Mouse click - change center (or drag to zoom, ESC to quit)", xCoord, yCoord);
			yCoord += vSpacing;
			drawString(graphics, "Double click - back to zoom level 0", xCoord, yCoord);
			yCoord += vSpacing;
			drawString(graphics, "Arrow up/down - double/halve max iterations", xCoord, yCoord);
			yCoord += vSpacing;
			drawString(graphics, "Arrow left/right - more/less threads", xCoord, yCoord);
			yCoord += vSpacing;

			int index = 1;
			for (PointOfInterest pointOfInterest : pointsOfInterestService.getElements()) {
				String description = String.format("%d - %s (%d)", index == 10 ? 0 : index, pointOfInterest.getName(),
						pointOfInterest.getMaxIterations());
				if (currentPointOfInterestIndex != null && currentPointOfInterestIndex == index) {
					drawString(graphics, description, xCoord, yCoord, Color.YELLOW);
				} else {
					drawString(graphics, description, xCoord, yCoord);
				}
				index++;
				yCoord += vSpacing;
				if (index > 10) {
					// Since we ran out of numeric keys..
					break;
				}
			}
		}
		return yCoord;
	}

}
