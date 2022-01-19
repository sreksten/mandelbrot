package com.threeamigos.mandelbrot.implementations.persister;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.threeamigos.mandelbrot.interfaces.persister.ImagePersister;
import com.threeamigos.mandelbrot.interfaces.persister.PersistResult;

public class ImagePersisterImpl implements ImagePersister {

	@Override
	public PersistResult saveImage(Image image, String filename) {
		try {
			File outputFile = new File(filename);

			final BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
					BufferedImage.TYPE_3BYTE_BGR);
			final Graphics2D g2 = bufferedImage.createGraphics();
			g2.drawImage(image, null, null);
			g2.dispose();

			ImageIO.write(bufferedImage, "png", outputFile);

			PersistResultImpl result = new PersistResultImpl();
			result.setFilename(filename);
			return result;

		} catch (IOException e) {
			return new PersistResultImpl("Error while saving image: " + e.getMessage());
		}
	}

}
