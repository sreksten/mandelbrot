package com.threeamigos.mandelbrot.implementations.service.mandelbrot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

class SliceDataDeque {

	private ConcurrentLinkedDeque<SliceData> deque = new ConcurrentLinkedDeque<>();

	void add(SliceData dataSlice) {
		deque.addFirst(dataSlice);
	}

	boolean isEmpty() {
		return deque.isEmpty();
	}

	SliceData remove() {
		return deque.remove();
	}

	void clear() {
		deque.clear();
	}

	List<SliceData> getDataSlices() {
		List<SliceData> list = new ArrayList<>();
		synchronized (deque) {
			deque.stream().forEach(list::add);
		}
		return list;
	}

}
