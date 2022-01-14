package com.threeamigos.mandelbrot;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JOptionPane;

public class ResolutionChooser {

	public Resolution chooseResolution() {

		Resolution defaultValue = matchScreenResolution();
		if (defaultValue == null) {
			defaultValue = Resolution.FULL_HD;
		}

		return (Resolution) JOptionPane.showInputDialog(null, "Choose resolution:", "3AM Mandelbrot",
				JOptionPane.PLAIN_MESSAGE, null, Resolution.values(), defaultValue);

	}

	private Resolution matchScreenResolution() {
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		for (Resolution res : Resolution.values()) {
			if (res.getWidth() == screenDimension.getWidth() && res.getHeight() == screenDimension.getHeight()) {
				return res;
			}
		}
		return null;
	}

}
