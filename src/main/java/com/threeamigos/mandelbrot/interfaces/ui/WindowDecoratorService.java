package com.threeamigos.mandelbrot.interfaces.ui;

import java.awt.Graphics2D;

import com.threeamigos.mandelbrot.interfaces.service.ImageProducerService;

/**
 * An object that decorates the canvas with help, point of interests' name,
 * info, etc, using various {@link WindowDecoratorFragment}s
 *
 * @author Stefano Reksten
 *
 */
public interface WindowDecoratorService {

	void setPercentage(Integer percentage);

	void setCurrentPointOfInterestIndex(Integer pointOfInterestIndex);

	void setImageProducerService(ImageProducerService imageProducerService);

	void toggleShowInfo();

	boolean isShowInfoActive();

	void toggleShowHelp();

	boolean isShowHelpActive();

	void toggleShowPointOfInterestName();

	boolean isShowPointOfInterestNameActive();

	void toggleShowSnapshotServiceStatus();

	boolean isShowSnapshotServiceStatusActive();

	void paint(Graphics2D graphics, int x, int y);

}
