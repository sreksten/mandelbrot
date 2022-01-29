package com.threeamigos.mandelbrot.implementations.service.mandelbrot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

class SliceDataDeque {

	private static final SliceDataDeque instance = new SliceDataDeque();

	private ConcurrentLinkedDeque<SliceData> deque = new ConcurrentLinkedDeque<>();

	SliceDataDeque() {
	}

	static final SliceDataDeque getInstance() {
		return instance;
	}

	void add(SliceData dataSlice) {
		synchronized (this) {
			deque.addFirst(dataSlice);
		}
	}

	boolean isEmpty() {
		synchronized (this) {
			return deque.isEmpty();
		}
	}

	SliceData remove() {
		synchronized (this) {
			return deque.remove();
		}
	}

	void clear() {
		synchronized (this) {
			deque.clear();
		}
	}

	List<SliceData> getDataSlices() {
		List<SliceData> list = new ArrayList();
		synchronized (this) {
			deque.stream().forEach(list::add);
		}
		return list;
	}

}
