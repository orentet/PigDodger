package com.pigdodger.modes;


public interface Mode extends AutoCloseable {
	void render();

	void start() throws Exception;
}
