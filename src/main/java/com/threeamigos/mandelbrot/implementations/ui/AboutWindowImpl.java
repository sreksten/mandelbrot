package com.threeamigos.mandelbrot.implementations.ui;

import java.awt.Component;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.threeamigos.mandelbrot.interfaces.ui.AboutWindow;

public class AboutWindowImpl implements AboutWindow {

	@Override
	public void about(Component component) {

		Box panel = Box.createVerticalBox();

		java.net.URL imgUrl = getClass().getResource("/3AM_logo.png");
		JLabel logo = new JLabel(new ImageIcon(imgUrl));
		logo.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(logo);

		panel.add(Box.createVerticalStrut(10));

		JLabel mandelbrotLabel = new JLabel("3AM Mandelbrot");
		Font font = new Font("Serif", Font.BOLD, 16);
		mandelbrotLabel.setFont(font);
		mandelbrotLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(mandelbrotLabel);

		panel.add(Box.createVerticalStrut(5));

		JLabel author = new JLabel("by Stefano Reksten - stefano.reksten@gmail.com");
		author.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(author);

		panel.add(Box.createVerticalStrut(5));

		JLabel license = new JLabel("Released under the GNU General Public License");
		license.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(license);

		JOptionPane.showOptionDialog(component, panel, "3AM Mandelbrot", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, null, null);
	}
}
