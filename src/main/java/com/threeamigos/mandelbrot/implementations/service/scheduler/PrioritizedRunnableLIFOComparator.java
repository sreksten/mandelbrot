package com.threeamigos.mandelbrot.implementations.service.scheduler;

import java.util.Comparator;

public class PrioritizedRunnableLIFOComparator implements Comparator<PrioritizedRunnable> {

	@Override
	public int compare(PrioritizedRunnable o1, PrioritizedRunnable o2) {
		int priorityResult = o1.getPriority().compareTo(o2.getPriority());
		if (priorityResult == 0) {
			return o2.getThreadId().compareTo(o1.getThreadId());
		}
		return priorityResult;
	}

}
