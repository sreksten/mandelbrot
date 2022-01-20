package com.threeamigos.mandelbrot.implementations.service;

import java.awt.Image;

import com.threeamigos.mandelbrot.implementations.persister.ImagePersisterImpl;
import com.threeamigos.mandelbrot.interfaces.persister.ImagePersister;
import com.threeamigos.mandelbrot.interfaces.persister.PersistResult;
import com.threeamigos.mandelbrot.interfaces.service.ImagePersisterService;
import com.threeamigos.mandelbrot.interfaces.ui.MessageNotifier;

public class ImagePersisterServiceImpl implements ImagePersisterService {

	private ImagePersister imagePersister = new ImagePersisterImpl();
	private MessageNotifier messageNotifier;

	@Override
	public void setMessageNotifier(MessageNotifier messageNotifier) {
		this.messageNotifier = messageNotifier;
	}

	@Override
	public PersistResult saveImage(Image image, String filename) {
		PersistResult persistResult = imagePersister.saveImage(image, filename);
		if (messageNotifier != null) {
			if (persistResult.isSuccessful()) {
				messageNotifier.notify("File saved in " + persistResult.getFilename());
			} else {
				messageNotifier.notify("Error while saving image: " + persistResult.getError());
			}
		}
		return persistResult;
	}

}
