package com.threeamigos.mandelbrot.implementations.service.backgroundexecution;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.*;

import com.threeamigos.common.util.implementations.collections.BlockingPriorityDequeWrapper;
import com.threeamigos.mandelbrot.interfaces.service.BackgroundExecutionService;
import com.threeamigos.common.util.implementations.collections.BucketedPriorityDeque;
import com.threeamigos.common.util.interfaces.collections.PriorityDeque;

/**
 * Modern implementation of BackgroundExecutionService that leverages the Java
 * Executor framework.
 *
 * It uses a custom BlockingPriorityDequeWrapper to bridge the existing project
 * PriorityDeque logic with the standard ThreadPoolExecutor.
 */
public class BackgroundExecutionServiceExecutorImpl implements BackgroundExecutionService{

    /** The executor service that manages the pool of worker threads */
    private final ExecutorService executor;

    /**
     * A thread-safe set used to keep track of tasks currently being executed.
     * This allows us to find and interrupt specific running tasks by caller.
     */
    private final Set<PrioritizedRunnable> activeTasks;

    /**
     * Initializes the service by creating a fixed thread pool sized to the
     * number of available processors.
     */
    public BackgroundExecutionServiceExecutorImpl() {
        int cores = Runtime.getRuntime().availableProcessors();

        // 1. Create our existing project-specific PriorityDeque
        PriorityDeque<PrioritizedRunnable> deque = new BucketedPriorityDeque<>(1, PriorityDeque.Policy.LIFO);

        // 2. Wrap it so it can be used by the ThreadPoolExecutor as a BlockingQueue
        // Note: BlockingPriorityDequeWrapper must be implemented to delegate to 'deque'
        BlockingQueue<Runnable> blockingQueue = new BlockingPriorityDequeWrapper(deque);

        // 3. Create the executor. It will automatically 'take()' from our wrapper,
        // which in turn will 'wait()' on the deque if it's empty.
        this.executor = new ThreadPoolExecutor(
                cores,
                cores,
                0L, TimeUnit.MILLISECONDS,
                blockingQueue
        );

        this.activeTasks = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    /**
     * Schedules a task for background execution.
     * The task is wrapped to track its 'active' status so it can be interrupted if needed.
     */
    @Override
    public void schedule(Thread caller, Runnable runnable, int priority, boolean interruptCallerWhenFinished, String threadName) {
        // We create the PrioritizedRunnable. The 'manager' thread reference is passed
        // as the current thread, though the Executor now handles the heavy lifting.
        PrioritizedRunnable task = new PrioritizedRunnable(
                caller, priority, runnable, interruptCallerWhenFinished, threadName, Thread.currentThread()
        );

        // We submit a wrapper to the executor to maintain our activeTasks set
        executor.execute(() -> {
            activeTasks.add(task);
            try {
                task.run();
            } finally {
                activeTasks.remove(task);
            }
        });
    }

    /**
     * Shuts down the executor and interrupts all tasks currently in progress.
     */
    @Override
    public void terminate() {
        // Stop accepting new tasks and attempt to stop existing ones
        executor.shutdownNow();

        // Explicitly interrupt our custom PrioritizedRunnable objects to ensure
        // they update their internal 'completed' state and signal callers.
        for (PrioritizedRunnable task : activeTasks) {
            task.interrupt();
        }
        activeTasks.clear();
    }

    /**
     * Finds active tasks associated with the given caller thread and interrupts them.
     */
    @Override
    public void interrupt(Thread caller) {
        // Logic for interrupting tasks currently running in the thread pool
        activeTasks.stream()
                .filter(task -> task.getCaller().equals(caller))
                .forEach(PrioritizedRunnable::interrupt);

        // Note: To remove tasks still waiting in the queue, your
        // BlockingPriorityDequeWrapper should implement the 'removeIf'
        // or 'clear' logic delegating to the underlying PriorityDeque.
    }
}