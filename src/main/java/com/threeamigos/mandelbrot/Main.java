package com.threeamigos.mandelbrot;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class Main {

	private boolean fullScreen;

	public Main(boolean fullScreen) {
		this.fullScreen = fullScreen;
		SwingUtilities.invokeLater(this::createAndShowGUI);
	}

	private void createAndShowGUI() {

		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();

		JFrame jframe = new JFrame("3AM Mandelbrot");
		jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		int width;
		int height;

		if (fullScreen) {
			width = screenDimension.width;
			height = screenDimension.height;
		} else {
			width = 1280;
			height = 1024;
		}

		jframe.setLayout(null);
		Container c = jframe.getContentPane();
		c.setPreferredSize(new Dimension(width, height));

		MandelbrotCanvas displayableCanvas = new MandelbrotCanvas(width, height);
		jframe.add(displayableCanvas);
		displayableCanvas.setLocation(0, 0);

		jframe.pack();
		jframe.setResizable(false);
		jframe.setLocation((screenDimension.width - jframe.getSize().width) / 2,
				(screenDimension.height - jframe.getSize().height) / 2);
		jframe.setVisible(true);

	}

	public static void main(String[] args) {
		new Main(false);
	}

}
