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
import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;
import com.threeamigos.mandelbrot.interfaces.service.MandelbrotService;
import com.threeamigos.mandelbrot.interfaces.ui.CalculationParametersRequester;

public class CalculationParametersRequesterImpl implements CalculationParametersRequester {

	private static final int MAX_ITERATIONS_NOT_SPECIFIED = -1;

	@Override
	public CalculationParameters getCalculationParameters(Component component) {
		return getCalculationParameters(MAX_ITERATIONS_NOT_SPECIFIED, component);
	}

	@Override
	public CalculationParameters getCalculationParameters(int maxIterations, Component component) {

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

		int defaultMaxIterationsExponent;
		int defaultMaxIterations;

		if (maxIterations == MAX_ITERATIONS_NOT_SPECIFIED) {
			defaultMaxIterationsExponent = 5;
			int actualExponent = MandelbrotService.MIN_ITERATIONS_EXPONENT + defaultMaxIterationsExponent;
			defaultMaxIterations = (int) Math.pow(2, actualExponent);
		} else {
			defaultMaxIterationsExponent = ((int) (Math.log(maxIterations) / Math.log(2)))
					- MandelbrotService.MIN_ITERATIONS_EXPONENT;
			defaultMaxIterations = maxIterations;
		}

		JLabel iterationsLabel = new JLabel("Max iterations: " + defaultMaxIterations);
		iterationsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(iterationsLabel);

		JSlider iterationsSlider = new JSlider(0,
				MandelbrotService.MAX_ITERATIONS_EXPONENT - MandelbrotService.MIN_ITERATIONS_EXPONENT,
				defaultMaxIterationsExponent);
		iterationsSlider.setMajorTickSpacing(4);
		iterationsSlider.setMinorTickSpacing(2);
		iterationsSlider.setPaintTicks(true);
		iterationsSlider.addChangeListener(event -> {
			JSlider source = (JSlider) event.getSource();
			int exponent = MandelbrotService.MIN_ITERATIONS_EXPONENT + source.getValue();
			iterationsLabel.setText("Max iterations: " + (int) Math.pow(2, exponent));
		});
		iterationsSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(iterationsSlider);

		int result = JOptionPane.showOptionDialog(component, panel, "3AM Mandelbrot", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, null, null);
		if (result == JOptionPane.OK_OPTION) {
			Resolution resolution = Resolution.values()[resolutionComboBox.getSelectedIndex()];
			int maxThreads = threadsSlider.getValue();
			int exponent = MandelbrotService.MIN_ITERATIONS_EXPONENT + iterationsSlider.getValue();
			return new CalculationParametersImpl(resolution, maxThreads, (int) Math.pow(2, exponent));
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

		private final Resolution resolution;
		private final int maxThreads;
		private final int maxIterations;

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
