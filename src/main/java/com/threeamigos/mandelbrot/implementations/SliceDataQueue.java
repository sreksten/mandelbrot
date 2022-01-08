package com.threeamigos.mandelbrot.implementations;

import java.util.concurrent.ConcurrentLinkedQueue;

public class SliceDataQueue {

	private static final SliceDataQueue instance = new SliceDataQueue();

	private ConcurrentLinkedQueue<SliceData> queue = new ConcurrentLinkedQueue();

	private SliceDataQueue() {
	}

	public static final SliceDataQueue getInstance() {
		return instance;
	}

	public void add(SliceData dataSlice) {
		queue.add(dataSlice);
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	public SliceData remove() {
		return queue.remove();
	}

	public void clear() {
		queue.clear();
	}

}
