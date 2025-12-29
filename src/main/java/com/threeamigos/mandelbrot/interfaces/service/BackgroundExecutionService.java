package com.threeamigos.mandelbrot.interfaces.service;

/**
 * An object that handles background execution of Runnable objects, used to split
 * calculation in multiple threads
 *
 * @author Stefano Reksten
 *
 */
public interface BackgroundExecutionService {

	void schedule(Thread caller, Runnable runnable, int priority, boolean interruptCallerWhenFinished,
			String threadName);

	void terminate();

	void interrupt(Thread caller);

}
