package com.threeamigos.mandelbrot.implementations.service.scheduler;

class PrioritizedRunnable implements Runnable {

	private static long globalTID = 0;

	private final Thread client;
	private final Integer priority;
	private final Runnable runnable;
	private final boolean interruptClientAtEnd;
	private final String threadName;
	private final Long threadId;
	private final Thread schedulerServiceMainThread;

	private Thread thread;
	private boolean completed = false;

	PrioritizedRunnable(Thread caller, int priority, Runnable runnable, boolean interruptClientAtEnd, String threadName,
			Thread schedulerServiceMainThread) {
		this.client = caller;
		this.priority = Integer.valueOf(priority);
		this.runnable = runnable;
		this.interruptClientAtEnd = interruptClientAtEnd;
		this.threadName = threadName;
		this.threadId = ++globalTID;
		this.schedulerServiceMainThread = schedulerServiceMainThread;
	}

	public Thread getCaller() {
		return client;
	}

	public Integer getPriority() {
		return priority;
	}

	public Runnable getRunnable() {
		return runnable;
	}

	public boolean isInterruptingCallerAtEnd() {
		return interruptClientAtEnd;
	}

	public String getThreadName() {
		return threadName;
	}

	public Long getThreadId() {
		return threadId;
	}

	public Thread getThread() {
		return thread;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void start() {
		thread = new Thread(this);
		thread.setDaemon(true);
		thread.setName(threadName);
		thread.start();
	}

	public void interrupt() {
		if (thread != null) {
			thread.interrupt();
		}
		if (interruptClientAtEnd) {
			client.interrupt();
		}
		completed = true;
	}

	@Override
	public void run() {
		runnable.run();
		if (interruptClientAtEnd) {
			client.interrupt();
		}
		completed = true;
		schedulerServiceMainThread.interrupt();
	}

}
