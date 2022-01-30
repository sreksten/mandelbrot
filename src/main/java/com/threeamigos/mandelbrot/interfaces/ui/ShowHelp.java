package com.threeamigos.mandelbrot.interfaces.ui;

import com.threeamigos.mandelbrot.interfaces.service.ImageProducerService;

public interface ShowHelp extends ShowSomething {

	void setCurrentPointOfInterestIndex(Integer currentPointOfInterestIndex);

	void setImageProducerService(ImageProducerService imageProducerService);

}
