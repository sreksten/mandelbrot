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

	public void setCurrentPointOfInterestIndex(Integer pointOfInterestIndex);

	void setImageProducerService(ImageProducerService imageProducerService);

	public void toggleShowInfo();

	public boolean isShowInfoActive();

	public void toggleShowHelp();

	public boolean isShowHelpActive();

	public void toggleShowPointOfInterestName();

	public boolean isShowPointOfInterestNameActive();

	public void toggleShowSnapshotServiceStatus();

	public boolean isShowSnapshotServiceStatusActive();

	public void paint(Graphics2D graphics, int x, int y);

}
