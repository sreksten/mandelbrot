package com.threeamigos.mandelbrot.interfaces.service;

import java.awt.Component;
import java.awt.Image;
import java.beans.PropertyChangeListener;

/**
 * An object that handles rendering and saving of screen snapshots in background
 *
 * @author Stefano Reksten
 *
 */
public interface SnapshotService {

	public void saveSnapshot(Points points, int maxIterations, String colorModelName, Image bufferedImage,
			Component parentComponent);

	public int getQueuedSnapshots();

	public Integer getCurrentSnapshotPercentage();

	public void addPropertyChangeListener(PropertyChangeListener pcl);

	public void removePropertyChangeListener(PropertyChangeListener pcl);

}
