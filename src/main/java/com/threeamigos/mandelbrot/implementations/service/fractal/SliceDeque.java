package com.threeamigos.mandelbrot.implementations.service.fractal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

class SliceDeque {

	private ConcurrentLinkedDeque<Slice> deque = new ConcurrentLinkedDeque<>();

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
		List<Slice> list = new ArrayList<>();
		synchronized (deque) {
			deque.stream().forEach(list::add);
		}
		return list;
	}

}
