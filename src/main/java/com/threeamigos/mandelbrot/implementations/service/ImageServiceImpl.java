package com.threeamigos.mandelbrot.implementations.service;

import java.awt.Image;

import com.threeamigos.mandelbrot.implementations.persister.ImagePersisterImpl;
import com.threeamigos.mandelbrot.interfaces.persister.ImagePersister;
import com.threeamigos.mandelbrot.interfaces.persister.PersistResult;
import com.threeamigos.mandelbrot.interfaces.service.ImageService;
import com.threeamigos.mandelbrot.interfaces.service.Notifier;

public class ImageServiceImpl implements ImageService {

	private ImagePersister imagePersister = new ImagePersisterImpl();

	@Override
	public PersistResult saveImage(Notifier notifier, Image image, String filename) {

		PersistResult result = imagePersister.saveImage(image, filename);
		if (result.isSuccessful()) {
			notifier.notify("File saved in " + filename);
		} else {
			notifier.notify("Error while saving image: " + result.getError());
		}
		return result;
	}

}
