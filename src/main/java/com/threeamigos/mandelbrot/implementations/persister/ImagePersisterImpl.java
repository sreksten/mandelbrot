package com.threeamigos.mandelbrot.implementations.persister;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.threeamigos.common.util.implementations.persistence.file.FilePersistResultBuilder;
import com.threeamigos.common.util.interfaces.persistence.file.FilePersistResult;
import com.threeamigos.mandelbrot.interfaces.persister.ImagePersister;

public class ImagePersisterImpl implements ImagePersister {

	@Override
	public FilePersistResult saveImage(Image image, String filename) {
		try {
			File outputFile = new File(filename);

			final BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
					BufferedImage.TYPE_3BYTE_BGR);
			final Graphics2D g2 = bufferedImage.createGraphics();
			g2.drawImage(image, null, null);
			g2.dispose();

			ImageIO.write(bufferedImage, "png", outputFile);

			return FilePersistResultBuilder.successful("Snapshot", filename);

		} catch (IOException e) {
			return FilePersistResultBuilder.error("Snapshot", filename, e.getMessage());
		}
	}

}
