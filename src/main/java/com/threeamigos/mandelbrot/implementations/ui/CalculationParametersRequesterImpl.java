package com.threeamigos.mandelbrot.implementations.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;

import com.threeamigos.mandelbrot.Resolution;
import com.threeamigos.mandelbrot.interfaces.CalculationParameters;
import com.threeamigos.mandelbrot.interfaces.MandelbrotCalculator;
import com.threeamigos.mandelbrot.interfaces.ui.CalculationParametersRequester;

public class CalculationParametersRequesterImpl implements CalculationParametersRequester {

	@Override
	public CalculationParameters getCalculationParameters(Component component) {

		Box panel = Box.createVerticalBox();

		JLabel resolutionLabel = new JLabel("Resolution:");
		resolutionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(resolutionLabel);

		JComboBox<Resolution> resolutionComboBox = new JComboBox<>(Resolution.values());
		Integer defaultValue = matchScreenResolution();
		resolutionComboBox.setSelectedIndex(defaultValue);
		resolutionComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(resolutionComboBox);

		panel.add(Box.createVerticalStrut(5));

		int cores = Runtime.getRuntime().availableProcessors();

		JLabel threadsLabel = new JLabel("Threads: " + cores);
		threadsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(threadsLabel);

		JSlider threadsSlider = new JSlider(1, cores, cores);
		threadsSlider.setMajorTickSpacing(4);
		threadsSlider.setMinorTickSpacing(2);
		threadsSlider.setPaintTicks(true);
		threadsSlider.addChangeListener(event -> {
			JSlider source = (JSlider) event.getSource();
			threadsLabel.setText("Threads: " + source.getValue());
		});
		threadsSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(threadsSlider);

		panel.add(Box.createVerticalStrut(5));

		int defaultMaxIterationsExponent = 5;
		int actualExponent = MandelbrotCalculator.MIN_CALCULATIONS_EXPONENT + defaultMaxIterationsExponent;
		int defaultMaxIterations = (int) Math.pow(2, actualExponent);

		JLabel iterationsLabel = new JLabel("Max iterations: " + defaultMaxIterations);
		iterationsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(iterationsLabel);

		JSlider iterationsSlider = new JSlider(0,
				MandelbrotCalculator.MAX_CALCULATIONS_EXPONENT - MandelbrotCalculator.MIN_CALCULATIONS_EXPONENT,
				defaultMaxIterationsExponent);
		iterationsSlider.setMajorTickSpacing(4);
		iterationsSlider.setMinorTickSpacing(2);
		iterationsSlider.setPaintTicks(true);
		iterationsSlider.addChangeListener(event -> {
			JSlider source = (JSlider) event.getSource();
			int exponent = MandelbrotCalculator.MIN_CALCULATIONS_EXPONENT + source.getValue();
			iterationsLabel.setText("Max iterations: " + (int) Math.pow(2, exponent));
		});
		iterationsSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(iterationsSlider);

		int result = JOptionPane.showOptionDialog(component, panel, "3AM Mandelbrot", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, null, null);
		if (result == JOptionPane.OK_OPTION) {
			Resolution resolution = Resolution.values()[resolutionComboBox.getSelectedIndex()];
			int maxThreads = threadsSlider.getValue();
			int exponent = MandelbrotCalculator.MIN_CALCULATIONS_EXPONENT + iterationsSlider.getValue();
			int maxIterations = (int) Math.pow(2, exponent);
			return new CalculationParametersImpl(resolution, maxThreads, maxIterations);
		}

		return null;
	}

	private int matchScreenResolution() {
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		for (Resolution res : Resolution.values()) {
			if (res.getWidth() == screenDimension.getWidth() && res.getHeight() == screenDimension.getHeight()) {
				return res.ordinal();
			}
		}
		return Resolution.FULL_HD.ordinal();
	}

	private class CalculationParametersImpl implements CalculationParameters {

		private Resolution resolution;
		private int maxThreads;
		private int maxIterations;

		CalculationParametersImpl(Resolution resolution, int maxThreads, int maxIterations) {
			this.resolution = resolution;
			this.maxThreads = maxThreads;
			this.maxIterations = maxIterations;
		}

		@Override
		public Resolution getResolution() {
			return resolution;
		}

		@Override
		public int getMaxThreads() {
			return maxThreads;
		}

		@Override
		public int getMaxIterations() {
			return maxIterations;
		}

	}

}
