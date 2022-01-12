package com.threeamigos.mandelbrot;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.threeamigos.mandelbrot.implementations.RegistryImpl;
import com.threeamigos.mandelbrot.interfaces.Registry;

public class Main {

	public Main(boolean fullScreen) {
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();

		int width;
		int height;

		if (fullScreen) {
			width = screenDimension.width;
			height = screenDimension.height;
		} else {
			width = 1280;
			height = 1024;
		}

		Registry registry = new RegistryImpl(width, height);

		JFrame jframe = new JFrame("3AM Mandelbrot");
		jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		jframe.setLayout(null);
		Container c = jframe.getContentPane();
		c.setPreferredSize(new Dimension(width, height));

		MandelbrotCanvas displayableCanvas = new MandelbrotCanvas(registry);
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
