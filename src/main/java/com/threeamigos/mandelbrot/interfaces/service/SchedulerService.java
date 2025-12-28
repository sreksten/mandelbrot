package com.threeamigos.mandelbrot.interfaces.service;

/**
 * An object that handles schedulation of Runnable objects, used to split
 * fractal calculation in multiple threads
 *
 * @author Stefano Reksten
 *
 */
public interface SchedulerService {

	void schedule(Thread caller, Runnable runnable, int priority, boolean interruptCallerWhenFinished,
			String threadName);

	void terminate();

	void interrupt(Thread caller);

}
