package org.modbot.controller.task;

import java.util.concurrent.Future;

/**
 * @author Cube
 */
public abstract class FutureRunnable implements Runnable {
	private Future<?> future;

	public void setFuture(Future<?> future) {
		this.future = future;
	}

	public Future<?> getFuture() {
		return future;
	}
}