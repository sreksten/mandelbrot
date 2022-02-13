package com.threeamigos.mandelbrot.implementations.ui;

import java.awt.Font;
import java.awt.Graphics2D;

import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;
import com.threeamigos.mandelbrot.interfaces.service.FractalService;
import com.threeamigos.mandelbrot.interfaces.service.FractalType;
import com.threeamigos.mandelbrot.interfaces.service.Points;
import com.threeamigos.mandelbrot.interfaces.ui.FontService;
import com.threeamigos.mandelbrot.interfaces.ui.Resolution;
import com.threeamigos.mandelbrot.interfaces.ui.WindowDecoratorInfoFragment;

public class WindowDecoratorInfoFragmentImpl extends WindowDecoratorFragmentImpl
		implements WindowDecoratorInfoFragment {

	private final FractalService mandelbrotService;
	private final CalculationParameters calculationParameters;
	private final Points points;

	private Integer percentage;

	public WindowDecoratorInfoFragmentImpl(Resolution resolution, FontService fontService,
			FractalService mandelbrotService, CalculationParameters calculationParameters, Points points) {
		super(resolution);
		this.mandelbrotService = mandelbrotService;
		this.calculationParameters = calculationParameters;
		this.points = points;

		fontHeight = getWidth() == ResolutionEnum.SD.getWidth() ? 16 : 24;
		vSpacing = fontHeight + 4;
		font = fontService.getFont(FontService.STANDARD_FONT_NAME, Font.BOLD, fontHeight);
	}

	@Override
	public final void setPercentage(Integer percentage) {
		this.percentage = percentage;
	}

	@Override
	public final int paint(Graphics2D graphics, int xCoord, int yCoord) {
		if (isActive()) {
			graphics.setFont(font);

			drawString(graphics, String.format("Zoom factor: %.2f - count: %d", 1.0d / points.getZoomFactor(),
					points.getZoomCount()), xCoord, yCoord);
			yCoord += vSpacing;
			if (percentage != null) {
				drawString(graphics,
						String.format("Percentage: %d (%d threads, %d iterations max)", percentage,
								calculationParameters.getMaxThreads(), calculationParameters.getMaxIterations()),
						xCoord, yCoord);
			} else {
				long drawTime = mandelbrotService.getCalculationTime();
				if (drawTime >= 0) {
					drawString(graphics,
							String.format("Calculation time: %d ms (%d threads, %d iterations max)", drawTime,
									calculationParameters.getMaxThreads(), calculationParameters.getMaxIterations()),
							xCoord, yCoord);
				} else {
					drawString(graphics,
							String.format("Calculation time: - (%d threads, %d iterations max)",
									calculationParameters.getMaxThreads(), calculationParameters.getMaxIterations()),
							xCoord, yCoord);
				}
			}
			yCoord += vSpacing;
			drawString(graphics, String.format("Real interval: [%1.14f,%1.14f]", points.getMinX(), points.getMaxX()),
					xCoord, yCoord);
			yCoord += vSpacing;
			drawString(graphics,
					String.format("Imaginary interval: [%1.14f,%1.14f]", points.getMinY(), points.getMaxY()), xCoord,
					yCoord);
			yCoord += vSpacing;
			if (points.getFractalType() == FractalType.JULIA) {
				drawString(graphics, String.format("Julia - c = (%1.3f,%1.3fi)", points.getJuliaCReal(),
						points.getJuliaCImaginary()), xCoord, yCoord);
				yCoord += vSpacing;
			}
			Double realCoordinateUnderPointer = points.getPointerRealcoordinate();
			if (realCoordinateUnderPointer != null) {
				Double imaginaryCoordinateUnderPointer = points.getPointerImaginaryCoordinate();
				if (imaginaryCoordinateUnderPointer != null) {
					drawString(graphics,
							String.format("Current point: (%d,%d) (%1.14f,%1.14f)", points.getPointerXCoordinate(),
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

}
