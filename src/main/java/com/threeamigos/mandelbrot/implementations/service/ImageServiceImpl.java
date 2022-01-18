package com.threeamigos.mandelbrot.implementations.service;

import java.awt.Image;

import com.threeamigos.mandelbrot.implementations.persister.ImagePersisterImpl;
import com.threeamigos.mandelbrot.interfaces.persister.ImagePersister;
import com.threeamigos.mandelbrot.interfaces.persister.PersistResult;
import com.threeamigos.mandelbrot.interfaces.service.ImageService;

public class ImageServiceImpl implements ImageService {

	private ImagePersister imagePersister = new ImagePersisterImpl();

	@Override
	public PersistResult saveImage(Image image, String filename) {
		return imagePersister.saveImage(image, filename);
	}

}
