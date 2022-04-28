package com.threeamigos.mandelbrot.interfaces.ui;

import java.awt.Graphics2D;

/**
 * An object whose status can be rendered and that is able to consume user
 * inputs. The idea is that RenderableConsumers are added to the main window so
 * that each one can process its own inputs.
 *
 * @author Stefano Reksten
 *
 */
public interface RenderableConsumer extends InputConsumer {

	public void paint(Graphics2D graphics);

}
