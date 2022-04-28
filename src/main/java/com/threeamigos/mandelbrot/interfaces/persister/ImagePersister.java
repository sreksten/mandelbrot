package com.threeamigos.mandelbrot.interfaces.persister;

import java.awt.Image;

/**
 * An object that stores an Image to disk
 *
 * @author Stefano Reksten
 *
 */
public interface ImagePersister {

	public PersistResult saveImage(Image image, String filename);

}
