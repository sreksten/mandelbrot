package com.threeamigos.mandelbrot.interfaces.persister;

import java.awt.Image;

public interface ImagePersister {

	public PersistResult saveImage(Image image, String filename);

}
