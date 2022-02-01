package com.threeamigos.mandelbrot.implementations.service.scheduler;

import java.util.concurrent.atomic.AtomicBoolean;

public class Runner implements Runnable {

	private static int runnerCounter;

	private final Thread thread;
	private final AtomicBoolean running;

	private PrioritizedRunnable prioritizedRunnable;

	public Runner(AtomicBoolean running) {
		this.running = running;
		runnerCounter++;
		thread = new Thread(null, this, "Runner-" + runnerCounter);
		thread.setDaemon(true);
		thread.start();
	}

	public void setPrioritizedRunnable(PrioritizedRunnable runnable) {
		this.prioritizedRunnable = runnable;
		thread.setName(runnable.getThreadName());
		thread.interrupt();
	}

	public void terminate() {
		running.set(false);
	}

	public boolean isAvailable() {
		return prioritizedRunnable == null;
	}

	public PrioritizedRunnable getPrioritizedRunnable() {
		return prioritizedRunnable;
	}

	@Override
	public void run() {
		while (running.get()) {
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
			}
			if (prioritizedRunnable != null) {
				prioritizedRunnable.run();
				prioritizedRunnable = null;
			}
		}
	}

}
