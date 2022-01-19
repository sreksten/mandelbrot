package com.threeamigos.mandelbrot.interfaces.service;

import java.awt.Image;

import com.threeamigos.mandelbrot.interfaces.persister.PersistResult;

public interface ImagePersisterService {

	public PersistResult saveImage(Image image, String filename);

}
