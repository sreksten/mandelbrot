package com.threeamigos.mandelbrot.interfaces.persister;

import com.threeamigos.common.util.interfaces.persistence.file.FilePersistResult;

import java.awt.Image;

/**
 * An object that stores an Image to disk
 *
 * @author Stefano Reksten
 *
 */
public interface ImagePersister {

	FilePersistResult saveImage(Image image, String filename);

}
