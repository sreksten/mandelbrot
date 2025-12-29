package com.threeamigos.mandelbrot.implementations.service.backgroundexecution;

class PrioritizedRunnable implements Runnable {

	private static long globalTID = 0;

	private final Thread caller;
	private final Integer priority;
	private final Runnable runnable;
	private final boolean interruptCallerAtEnd;
	private final String threadName;
	private final Long threadId;
	private final Thread backgroundExecutionServiceMainThread;

	private Thread thread;
	private boolean completed = false;

	PrioritizedRunnable(Thread caller, int priority, Runnable runnable, boolean interruptCallerAtEnd, String threadName,
			Thread backgroundExecutionServiceMainThread) {
		this.caller = caller;
		this.priority = priority;
		this.runnable = runnable;
		this.interruptCallerAtEnd = interruptCallerAtEnd;
		this.threadName = threadName;
		this.threadId = ++globalTID;
		this.backgroundExecutionServiceMainThread = backgroundExecutionServiceMainThread;
	}

	public Thread getCaller() {
		return caller;
	}

	public Integer getPriority() {
		return priority;
	}

	public Runnable getRunnable() {
		return runnable;
	}

	public boolean isInterruptingCallerAtEnd() {
		return interruptCallerAtEnd;
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
		thread = new Thread(null, this, threadName);
		thread.setDaemon(true);
		thread.start();
	}

	public void interrupt() {
		if (thread != null) {
			thread.interrupt();
		}
		if (interruptCallerAtEnd) {
			caller.interrupt();
		}
		completed = true;
	}

	@Override
	public void run() {
		runnable.run();
		if (interruptCallerAtEnd) {
			caller.interrupt();
		}
		completed = true;
		backgroundExecutionServiceMainThread.interrupt();
	}

}
