package com.threeamigos.mandelbrot.interfaces.ui;

/**
 * An object able to request user input and display messages.
 *
 * @author Stefano Reksten
 *
 */
public interface MessageNotifier {

	// FIXME should be split in two objects
	public String request(String message);

	public void notify(String message);

}
