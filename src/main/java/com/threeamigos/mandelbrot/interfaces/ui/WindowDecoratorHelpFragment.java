package com.threeamigos.mandelbrot.interfaces.ui;

import com.threeamigos.mandelbrot.interfaces.service.ImageProducerService;

public interface WindowDecoratorHelpFragment extends WindowDecoratorFragment {

	void setCurrentPointOfInterestIndex(Integer currentPointOfInterestIndex);

	void setImageProducerService(ImageProducerService imageProducerService);

}
