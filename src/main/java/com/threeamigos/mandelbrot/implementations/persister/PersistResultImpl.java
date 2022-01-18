package com.threeamigos.mandelbrot.implementations.persister;

import com.threeamigos.mandelbrot.interfaces.persister.PersistResult;

public class PersistResultImpl implements PersistResult {

	private final boolean successful;

	private String error;

	PersistResultImpl() {
		successful = true;
	}

	PersistResultImpl(String error) {
		successful = false;
		this.error = error;
	}

	@Override
	public boolean isSuccessful() {
		return successful;
	}

	@Override
	public String getError() {
		return error;
	}

}
