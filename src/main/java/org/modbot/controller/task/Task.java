package org.modbot.controller.task;

import java.util.concurrent.TimeUnit;

/**
 * Represents a task that may be ran at a given interval.
 * @author Michael Bull
 * @author Cube
 */
public abstract class Task {
	private final long interval;
	private final TimeUnit timeUnit;
	private boolean running = true;

	protected Task(long interval, TimeUnit timeUnit) {
		this.interval = interval;
		this.timeUnit = timeUnit;
	}

	public long getInterval() {
		return interval;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public boolean isRunning() {
		return running;
	}

	public void stop() {
		running = false;
	}

	public abstract void execute();
}
