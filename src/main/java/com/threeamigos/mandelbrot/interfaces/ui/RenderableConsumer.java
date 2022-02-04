package com.threeamigos.mandelbrot.interfaces.ui;

import java.awt.Graphics2D;

public interface RenderableConsumer extends InputConsumer {

	public void paint(Graphics2D graphics);

}
