package com.threeamigos.mandelbrot.interfaces.ui;

import com.threeamigos.mandelbrot.interfaces.service.ImageProducerService;

/**
 * An object that displays on-screen help
 *
 * @author Stefano Reksten
 *
 */
public interface WindowDecoratorHelpFragment extends WindowDecoratorFragment {

	void setCurrentPointOfInterestIndex(Integer currentPointOfInterestIndex);

	void setImageProducerService(ImageProducerService imageProducerService);

}
