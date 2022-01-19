package com.threeamigos.mandelbrot.implementations.persister;

import com.threeamigos.mandelbrot.interfaces.persister.PersistResult;

class PersistResultImpl implements PersistResult {

	private final boolean successful;

	private String filename;

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

	void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
	public String getFilename() {
		return filename;
	}

	@Override
	public String getError() {
		return error;
	}

}
