package com.threeamigos.mandelbrot.interfaces.service;

import java.awt.Image;

import com.threeamigos.mandelbrot.interfaces.persister.PersistResult;
import com.threeamigos.mandelbrot.interfaces.ui.MessageNotifier;

/**
 * An object that stores an image to disk
 *
 * @author Stefano Reksten
 *
 */
public interface ImagePersisterService {

	public void setMessageNotifier(MessageNotifier messageNotifier);

	public PersistResult saveImage(Image image, String filename);

}
