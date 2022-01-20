package com.threeamigos.mandelbrot.implementations.service;

import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.threeamigos.mandelbrot.Resolution;
import com.threeamigos.mandelbrot.interfaces.persister.PersistResult;
import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;
import com.threeamigos.mandelbrot.interfaces.service.ImagePersisterService;
import com.threeamigos.mandelbrot.interfaces.service.ImageProducerService;
import com.threeamigos.mandelbrot.interfaces.service.ImageProducerServiceFactory;
import com.threeamigos.mandelbrot.interfaces.service.MandelbrotService;
import com.threeamigos.mandelbrot.interfaces.service.MandelbrotServiceFactory;
import com.threeamigos.mandelbrot.interfaces.service.PointsInfo;
import com.threeamigos.mandelbrot.interfaces.service.SnapshotService;
import com.threeamigos.mandelbrot.interfaces.ui.CalculationParametersRequester;
import com.threeamigos.mandelbrot.interfaces.ui.MessageNotifier;

public class SnapshotServiceImpl implements SnapshotService {

	private CalculationParametersRequester calculationParametersRequester;
	private MandelbrotServiceFactory mandelbrotServiceFactory;
	private ImageProducerServiceFactory imageProducerServiceFactory;
	private ImagePersisterService imagePersisterService;
	private MessageNotifier messageNotifier;

	public SnapshotServiceImpl(CalculationParametersRequester calculationParametersRequester,
			MandelbrotServiceFactory mandelbrotServiceFactory, ImageProducerServiceFactory imageProducerServiceFactory,
			ImagePersisterService imageService) {
		this.calculationParametersRequester = calculationParametersRequester;
		this.mandelbrotServiceFactory = mandelbrotServiceFactory;
		this.imageProducerServiceFactory = imageProducerServiceFactory;
		this.imagePersisterService = imageService;
	}

	@Override
	public PersistResult saveSnapshot(PointsInfo pointsInfo, boolean usingDirectColorModel, Image bufferedImage,
			Component parentComponent) {

		CalculationParameters tempCalculationParameters = calculationParametersRequester
				.getCalculationParameters(parentComponent);
		if (tempCalculationParameters == null) {
			return null;
		}

		Image imageToSave = bufferedImage;

		Resolution tempResolution = tempCalculationParameters.getResolution();
		if (tempResolution.getWidth() != bufferedImage.getWidth(null)
				|| tempResolution.getHeight() != bufferedImage.getHeight(null)) {
			MandelbrotService tempCalculator = mandelbrotServiceFactory.createInstance(tempCalculationParameters);
			PointsInfo tempPointsInfo = pointsInfo.adaptToResolution(tempResolution);
			tempCalculator.calculate(tempPointsInfo);
			ImageProducerService tempImageProducer = imageProducerServiceFactory
					.createInstance(tempCalculationParameters.getMaxIterations());
			if (!usingDirectColorModel) {
				tempImageProducer.useIndexColorModel();
			}
			imageToSave = tempImageProducer.produceImage(tempPointsInfo.getWidth(), tempPointsInfo.getHeight(),
					tempCalculator.getIterations());
		}

		String filename = new StringBuilder().append(System.getProperty("user.home")).append(File.separatorChar)
				.append("3AM_Mandelbrot_").append(new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()))
				.append(".png").toString();

		return imagePersisterService.saveImage(imageToSave, filename);
	}

}
