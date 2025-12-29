package com.threeamigos.mandelbrot.implementations.service.fractal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

class SliceDeque {

	private final ConcurrentLinkedDeque<Slice> deque = new ConcurrentLinkedDeque<>();

	void add(Slice slice) {
		deque.addFirst(slice);
	}

	boolean isEmpty() {
		return deque.isEmpty();
	}

	Slice remove() {
		return deque.remove();
	}

	void clear() {
		deque.clear();
	}

	List<Slice> getSlices() {
		List<Slice> list;
		synchronized (deque) {
            list = new ArrayList<>(deque);
		}
		return list;
	}

}
