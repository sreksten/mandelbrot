package com.threeamigos.mandelbrot.interfaces.ui;

/**
 * An object able to request user input and display messages.
 *
 * @author Stefano Reksten
 *
 */
public interface MessageNotifier {

	// FIXME should be split in two objects
	String request(String message);

	void notify(String message);

}
