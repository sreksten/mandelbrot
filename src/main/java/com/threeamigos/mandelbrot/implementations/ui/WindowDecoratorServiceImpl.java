package com.threeamigos.mandelbrot.implementations.ui;

import java.awt.Graphics2D;

import com.threeamigos.mandelbrot.interfaces.service.ImageProducerService;
import com.threeamigos.mandelbrot.interfaces.ui.WindowDecoratorHelpFragment;
import com.threeamigos.mandelbrot.interfaces.ui.WindowDecoratorInfoFragment;
import com.threeamigos.mandelbrot.interfaces.ui.WindowDecoratorPointOfInterestNameFragment;
import com.threeamigos.mandelbrot.interfaces.ui.WindowDecoratorSnapshotServiceFragment;
import com.threeamigos.mandelbrot.interfaces.ui.WindowDecoratorService;

public class WindowDecoratorServiceImpl implements WindowDecoratorService {

	private final WindowDecoratorInfoFragment showInfo;
	private final WindowDecoratorHelpFragment showHelp;
	private final WindowDecoratorPointOfInterestNameFragment showPointOfInterestName;
	private final WindowDecoratorSnapshotServiceFragment showSnapshotServiceStatus;

	public WindowDecoratorServiceImpl(WindowDecoratorInfoFragment showInfo, WindowDecoratorHelpFragment showHelp,
			WindowDecoratorPointOfInterestNameFragment showPointOfInterestName, WindowDecoratorSnapshotServiceFragment showSnapshotServiceStatus) {
		this.showInfo = showInfo;
		this.showHelp = showHelp;
		this.showPointOfInterestName = showPointOfInterestName;
		this.showSnapshotServiceStatus = showSnapshotServiceStatus;
	}

	@Override
	public void setPercentage(Integer percentage) {
		showInfo.setPercentage(percentage);
	}

	@Override
	public void setCurrentPointOfInterestIndex(Integer pointOfInterestIndex) {
		showHelp.setCurrentPointOfInterestIndex(pointOfInterestIndex);
		showPointOfInterestName.setCurrentPointOfInterestIndex(pointOfInterestIndex);
	}

	@Override
	public void setImageProducerService(ImageProducerService imageProducerService) {
		showHelp.setImageProducerService(imageProducerService);
	}

	@Override
	public void toggleShowInfo() {
		showInfo.toggleActive();
	}

	@Override
	public boolean isShowInfoActive() {
		return showInfo.isActive();
	}

	@Override
	public void toggleShowHelp() {
		showHelp.toggleActive();
	}

	@Override
	public boolean isShowHelpActive() {
		return showHelp.isActive();
	}

	@Override
	public void toggleShowPointOfInterestName() {
		showPointOfInterestName.toggleActive();
	}

	@Override
	public boolean isShowPointOfInterestNameActive() {
		return showPointOfInterestName.isActive();
	}

	@Override
	public void toggleShowSnapshotServiceStatus() {
		showSnapshotServiceStatus.toggleActive();
	}

	@Override
	public boolean isShowSnapshotServiceStatusActive() {
		return showSnapshotServiceStatus.isActive();
	}

	@Override
	public void paint(Graphics2D graphics, int x, int y) {
		y = showInfo.paint(graphics, x, y);
		y = showSnapshotServiceStatus.paint(graphics, x, y);
		showHelp.paint(graphics, x, y);
		showPointOfInterestName.paint(graphics, -1, -1);
	}

}
