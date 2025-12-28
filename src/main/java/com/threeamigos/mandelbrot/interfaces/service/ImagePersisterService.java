package com.threeamigos.mandelbrot.interfaces.service;

import java.awt.Image;

import com.threeamigos.common.util.interfaces.persistence.PersistResult;
import com.threeamigos.mandelbrot.interfaces.ui.MessageNotifier;

/**
 * An object that stores an image to a disk
 *
 * @author Stefano Reksten
 *
 */
public interface ImagePersisterService {

	void setMessageNotifier(MessageNotifier messageNotifier);

	PersistResult saveImage(Image image, String filename);

}
