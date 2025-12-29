package com.threeamigos.mandelbrot.implementations.service.backgroundexecution;

import java.util.Comparator;

public class PrioritizedRunnableFIFOComparator implements Comparator<PrioritizedRunnable> {

    @Override
    public int compare(PrioritizedRunnable o1, PrioritizedRunnable o2) {
        int priorityResult = o1.getPriority().compareTo(o2.getPriority());
        if (priorityResult == 0) {
            return o1.getThreadId().compareTo(o2.getThreadId());
        }
        return priorityResult;
    }

}
