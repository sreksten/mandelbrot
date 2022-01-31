package com.threeamigos.mandelbrot.interfaces.service;

public interface SchedulerService {

	public void schedule(Thread caller, Runnable runnable, int priority, boolean interruptCallerAtEnd,
			String threadName);

	public void terminate();

	public void interrupt(Thread caller);

}
