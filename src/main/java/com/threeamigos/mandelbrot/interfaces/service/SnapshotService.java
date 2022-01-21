package com.threeamigos.mandelbrot.interfaces.service;

import java.awt.Component;
import java.awt.Image;

import com.threeamigos.mandelbrot.interfaces.persister.PersistResult;

public interface SnapshotService {

	public PersistResult saveSnapshot(PointsInfo pointsInfo, int maxIterations, boolean usingDirectColorModel,
			Image bufferedImage, Component parentComponent);

}
