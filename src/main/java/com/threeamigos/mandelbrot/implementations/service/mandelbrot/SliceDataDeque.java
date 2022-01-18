package com.threeamigos.mandelbrot.implementations.service.mandelbrot;

import java.util.concurrent.ConcurrentLinkedDeque;

class SliceDataDeque {

	private static final SliceDataDeque instance = new SliceDataDeque();

	private ConcurrentLinkedDeque<SliceData> deque = new ConcurrentLinkedDeque<>();

	private SliceDataDeque() {
	}

	public static final SliceDataDeque getInstance() {
		return instance;
	}

	public void add(SliceData dataSlice) {
		deque.addFirst(dataSlice);
	}

	public boolean isEmpty() {
		return deque.isEmpty();
	}

	public SliceData remove() {
		return deque.remove();
	}

	public void clear() {
		deque.clear();
	}

}
