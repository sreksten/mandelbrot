package com.threeamigos.mandelbrot.implementations.service.backgroundexecution;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import com.threeamigos.common.util.implementations.collections.BucketedPriorityDeque;
import com.threeamigos.common.util.interfaces.collections.PriorityDeque;
import com.threeamigos.mandelbrot.interfaces.service.BackgroundExecutionService;

public class BackgroundExecutionServiceImpl implements BackgroundExecutionService, Runnable {

	private static final int SLOT_NOT_AVAILABLE = -1;

	private final AtomicBoolean running = new AtomicBoolean();

	private final Thread mainThread;
	private final PrioritizedRunnable[] activeRunnables;
	private final PriorityDeque<PrioritizedRunnable> waitingRunnables;

	public BackgroundExecutionServiceImpl(BackgroundExecutionPolicy backgroundExecutionPolicy) {
		Comparator<PrioritizedRunnable> comparator;
		switch (backgroundExecutionPolicy) {
			case FIFO:
				comparator = new PrioritizedRunnableFIFOComparator();
				break;
			case LIFO:
				comparator = new PrioritizedRunnableLIFOComparator();
				break;
			default:
				throw new IllegalArgumentException("Invalid execution policy");
		}
		activeRunnables = new PrioritizedRunnable[Runtime.getRuntime().availableProcessors()];
		waitingRunnables = new BucketedPriorityDeque<>(31);

		running.set(true);
		mainThread = new Thread(null, this, "BackgroundExecutionService");
		mainThread.setDaemon(true);
		mainThread.start();
	}

	@Override
	public void schedule(Thread caller, Runnable runnable, int priority, boolean interruptCallerAtEnd,
			String threadName) {
		PrioritizedRunnable prioritizedRunnable = new PrioritizedRunnable(caller, priority, runnable,
				interruptCallerAtEnd, threadName, mainThread);
		waitingRunnables.add(prioritizedRunnable, priority);
		mainThread.interrupt();
	}

	@Override
	public void terminate() {
		running.set(false);
		for (PrioritizedRunnable runnable : activeRunnables) {
			if (runnable != null && !runnable.isCompleted()) {
				runnable.interrupt();
			}
		}
		PrioritizedRunnable runnable;
		while ((runnable = waitingRunnables.pollFifo()) != null) {
			runnable.interrupt();
		}
		waitingRunnables.clear();
		mainThread.interrupt();
	}

	@Override
	public void interrupt(Thread caller) {
		waitingRunnables.clear(r -> r.getCaller().equals(caller));
		for (PrioritizedRunnable runnable : activeRunnables) {
			if (runnable.getCaller().equals(caller)) {
				runnable.interrupt();
			}
		}
	}

	@Override
	public void run() {
		while (running.get()) {
			if (!waitingRunnables.isEmpty()) {
				executeNextRunnableIfPossible();
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private void executeNextRunnableIfPossible() {
		int availableSlot = getAvailableSlot();
		if (availableSlot != SLOT_NOT_AVAILABLE) {
			PrioritizedRunnable runnable = waitingRunnables.pollLifo();
			activeRunnables[availableSlot] = runnable;
			runnable.start();
		}
	}

	private int getAvailableSlot() {
		for (int i = 0; i < activeRunnables.length; i++) {
			PrioritizedRunnable currentRunnable = activeRunnables[i];
			if (currentRunnable == null || currentRunnable.isCompleted()) {
				return i;
			}
		}
		return SLOT_NOT_AVAILABLE;
	}

}
