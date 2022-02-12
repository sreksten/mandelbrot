package com.threeamigos.mandelbrot.interfaces.service;

public interface SchedulerService {

	public void schedule(Thread caller, Runnable runnable, int priority, boolean interruptCallerWhenFinished,
			String threadName);

	public void terminate();

	public void interrupt(Thread caller);

}
