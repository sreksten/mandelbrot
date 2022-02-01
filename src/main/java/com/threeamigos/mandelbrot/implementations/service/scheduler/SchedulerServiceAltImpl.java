package com.threeamigos.mandelbrot.implementations.service.scheduler;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import com.threeamigos.mandelbrot.interfaces.service.SchedulerService;

public class SchedulerServiceAltImpl implements SchedulerService, Runnable {

	private static final int SLOT_NOT_AVAILABLE = -1;

	private static final int CORES = Runtime.getRuntime().availableProcessors();

	private final AtomicBoolean running;

	private Thread mainThread;
	private Runner[] runners;
	private ConcurrentSkipListSet<PrioritizedRunnable> waitingRunnables;

	public SchedulerServiceAltImpl(Comparator<PrioritizedRunnable> comparator) {

		running = new AtomicBoolean();
		running.set(true);

		runners = new Runner[CORES];
		for (int i = 0; i < CORES; i++) {
			runners[i] = new Runner(running);
		}

		waitingRunnables = new ConcurrentSkipListSet<>(comparator);

		mainThread = new Thread(null, this, "SchedulerService");
		mainThread.setDaemon(true);
		mainThread.start();
	}

	@Override
	public void schedule(Thread caller, Runnable runnable, int priority, boolean interruptCallerAtEnd,
			String threadName) {
		PrioritizedRunnable prioritizedRunnable = new PrioritizedRunnable(caller, priority, runnable,
				interruptCallerAtEnd, threadName, mainThread);
		waitingRunnables.add(prioritizedRunnable);
		mainThread.interrupt();
	}

	@Override
	public void terminate() {
		running.set(false);
		for (Runner runner : runners) {
			PrioritizedRunnable runnable = runner.getPrioritizedRunnable();
			if (runnable != null && !runnable.isCompleted()) {
				runnable.interrupt();
			}
		}
		waitingRunnables.clear();
		mainThread.interrupt();
	}

	@Override
	public void interrupt(Thread caller) {
		synchronized (waitingRunnables) {
			List<PrioritizedRunnable> removables = waitingRunnables.stream().filter(r -> r.getCaller().equals(caller))
					.collect(Collectors.toList());
			for (PrioritizedRunnable removable : removables) {
				waitingRunnables.remove(removable);
			}
		}
		for (Runner runner : runners) {
			PrioritizedRunnable runnable = runner.getPrioritizedRunnable();
			if (runnable.getCaller().equals(caller)) {
				runnable.interrupt();
			}
		}
	}

	@Override
	public void run() {
		while (running.get()) {
			if (!waitingRunnables.isEmpty()) {
				scheduleNextRunnableIfPossible();
			}
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	private void scheduleNextRunnableIfPossible() {
		int availableSlot = getAvailableSlot();
		if (availableSlot != SLOT_NOT_AVAILABLE) {
			PrioritizedRunnable runnable = waitingRunnables.last();
			waitingRunnables.remove(runnable);
			runners[availableSlot].setPrioritizedRunnable(runnable);
		}
	}

	private int getAvailableSlot() {
		for (int i = 0; i < CORES; i++) {
			if (runners[i].isAvailable()) {
				return i;
			}
		}
		return SLOT_NOT_AVAILABLE;
	}

}
