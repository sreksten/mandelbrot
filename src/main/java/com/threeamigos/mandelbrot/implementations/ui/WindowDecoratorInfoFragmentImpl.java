package com.threeamigos.mandelbrot.implementations.ui;

import java.awt.Font;
import java.awt.Graphics2D;

import com.threeamigos.mandelbrot.Resolution;
import com.threeamigos.mandelbrot.interfaces.service.MandelbrotService;
import com.threeamigos.mandelbrot.interfaces.service.Points;
import com.threeamigos.mandelbrot.interfaces.ui.WindowDecoratorInfoFragment;

public class WindowDecoratorInfoFragmentImpl extends WindowDecoratorFragmentImpl implements WindowDecoratorInfoFragment {

	private MandelbrotService mandelbrotService;
	private Points points;
	private Integer percentage;

	public WindowDecoratorInfoFragmentImpl(Resolution resolution, MandelbrotService mandelbrotService, Points points) {
		super(resolution);
		this.mandelbrotService = mandelbrotService;
		this.points = points;
	}

	@Override
	public final void setPercentage(Integer percentage) {
		this.percentage = percentage;
	}

	@Override
	public final int paint(Graphics2D graphics, int xCoord, int yCoord) {
		if (isActive()) {
			int fontHeight = getWidth() == Resolution.SD.getWidth() ? 16 : 24;
			Font font = new Font(FONT_NAME, Font.BOLD, fontHeight);
			graphics.setFont(font);

			int vSpacing = fontHeight + 4;
			drawString(graphics, String.format("Zoom factor: %.2f - count: %d", 1.0d / points.getZoomFactor(),
					points.getZoomCount()), xCoord, yCoord);
			yCoord += vSpacing;
			if (percentage != null) {
				drawString(graphics,
						String.format("Percentage: %d (%d threads, %d iterations max)", percentage,
								mandelbrotService.getNumberOfThreads(), mandelbrotService.getMaxIterations()),
						xCoord, yCoord);
			} else {
				drawString(graphics,
						String.format("Draw time: %d ms (%d threads, %d iterations max)",
								mandelbrotService.getDrawTime(), mandelbrotService.getNumberOfThreads(),
								mandelbrotService.getMaxIterations()),
						xCoord, yCoord);
			}
			yCoord += vSpacing;
			drawString(graphics, String.format("Real interval: [%1.14f,%1.14f]", points.getMinX(), points.getMaxX()),
					xCoord, yCoord);
			yCoord += vSpacing;
			drawString(graphics,
					String.format("Imaginary interval: [%1.14f,%1.14f]", points.getMinY(), points.getMaxY()), xCoord,
					yCoord);
			yCoord += vSpacing;
			drawString(graphics, String.format("Optimizations: %s", getOptimizationsDescription()), xCoord, yCoord);
			yCoord += vSpacing;
			Double realCoordinateUnderPointer = points.getPointerRealcoordinate();
			if (realCoordinateUnderPointer != null) {
				Double imaginaryCoordinateUnderPointer = points.getPointerImaginaryCoordinate();
				if (imaginaryCoordinateUnderPointer != null) {
					drawString(graphics,
							String.format("Current point: [%d,%d] [%1.14f,%1.14f]", points.getPointerXCoordinate(),
									points.getPointerYCoordinate(), realCoordinateUnderPointer.floatValue(),
									imaginaryCoordinateUnderPointer.floatValue()),
							xCoord, yCoord);
					yCoord += vSpacing;
					drawString(graphics,
							String.format("Current point iterations: %d", mandelbrotService
									.getIterations(points.getPointerXCoordinate(), points.getPointerYCoordinate())),
							xCoord, yCoord);
					yCoord += vSpacing;
				}
			}
		}
		return yCoord;
	}

	private final String getOptimizationsDescription() {
		String optimizations;
		if (points.isCardioidVisible()) {
			if (points.isPeriod2BulbVisible()) {
				optimizations = "Cardioid, Period2Bulb, Period";
			} else {
				optimizations = "Cardioid, Period";
			}
		} else {
			if (points.isPeriod2BulbVisible()) {
				optimizations = "Period2Bulb, Period";
			} else {
				optimizations = "Period";
			}
		}
		return optimizations;
	}

}
