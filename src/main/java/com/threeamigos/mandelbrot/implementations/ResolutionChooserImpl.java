package com.threeamigos.mandelbrot.implementations;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JOptionPane;

import com.threeamigos.mandelbrot.Resolution;
import com.threeamigos.mandelbrot.interfaces.ResolutionChooser;

public class ResolutionChooserImpl implements ResolutionChooser {

	@Override
	public Resolution chooseResolution(Component component) {
		Resolution defaultValue = matchScreenResolution();
		return (Resolution) JOptionPane.showInputDialog(component, "Choose resolution:", "3AM Mandelbrot",
				JOptionPane.PLAIN_MESSAGE, null, Resolution.values(), defaultValue);
	}

	private Resolution matchScreenResolution() {
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		for (Resolution res : Resolution.values()) {
			if (res.getWidth() == screenDimension.getWidth() && res.getHeight() == screenDimension.getHeight()) {
				return res;
			}
		}
		return Resolution.FULL_HD;
	}

}
