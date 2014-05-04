package org.modbot.controller.task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * Schedules {@link Task}s to run in the future.
 * @author Cube
 */
public final class TaskScheduler {
	private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

	public void start(final Task task, boolean immediate) {
		FutureRunnable runnable = new FutureRunnable() {
			@Override
			public void run() {
				if (!task.isRunning()) {
					getFuture().cancel(true);
					return;
				}
				task.execute();
			}
		};

		final ScheduledFuture<?> future = executorService.scheduleAtFixedRate(runnable, immediate ? 0 : task.getInterval(), task.getInterval(), task.getTimeUnit());
		runnable.setFuture(future);
	}
}
