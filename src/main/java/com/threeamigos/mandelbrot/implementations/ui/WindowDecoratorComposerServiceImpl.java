package com.threeamigos.mandelbrot.implementations.ui;

import java.awt.Graphics2D;

import com.threeamigos.mandelbrot.interfaces.service.ImageProducerService;
import com.threeamigos.mandelbrot.interfaces.ui.ShowHelp;
import com.threeamigos.mandelbrot.interfaces.ui.ShowInfo;
import com.threeamigos.mandelbrot.interfaces.ui.ShowPointOfInterestName;
import com.threeamigos.mandelbrot.interfaces.ui.WindowDecoratorComposerService;

public class WindowDecoratorComposerServiceImpl implements WindowDecoratorComposerService {

	private ShowInfo showInfo;
	private ShowHelp showHelp;
	private ShowPointOfInterestName showPointOfInterestName;

	public WindowDecoratorComposerServiceImpl(ShowInfo showInfo, ShowHelp showHelp,
			ShowPointOfInterestName showPointOfInterestName) {
		this.showInfo = showInfo;
		this.showHelp = showHelp;
		this.showPointOfInterestName = showPointOfInterestName;
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
	public void paint(Graphics2D graphics, int x, int y) {
		y = showInfo.paint(graphics, x, y);
		showHelp.paint(graphics, x, y);
		showPointOfInterestName.paint(graphics, -1, -1);
	}

}
