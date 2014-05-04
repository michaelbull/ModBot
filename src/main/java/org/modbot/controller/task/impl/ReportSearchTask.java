package org.modbot.controller.task.impl;

import ch.swingfx.twinkle.event.NotificationEvent;
import ch.swingfx.twinkle.event.NotificationEventAdapter;
import org.modbot.controller.Controller;
import org.modbot.controller.notification.Notifier;
import org.modbot.controller.parser.ForumParser;
import org.modbot.util.OpenThreadException;
import org.modbot.controller.task.Task;
import org.modbot.model.Forum;
import org.modbot.model.ForumThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * A {@link Task} that searches for reported posts in the reported posts forum (typically staff only).
 * @author Michael Bull
 */
public final class ReportSearchTask extends Task {
	private static final int REPORTED_POSTS_FORUM_ID = 546;

	private static final Logger logger = LoggerFactory.getLogger(ReportSearchTask.class);

	private final Controller controller;

	public ReportSearchTask(Controller controller) {
		super(5, TimeUnit.SECONDS);
		this.controller = controller;
	}

	@Override
	public void execute() {
		Forum forum;
		try {
			forum = ForumParser.parse(controller.forumDisplay(REPORTED_POSTS_FORUM_ID));
		} catch (IOException e) {
			controller.getModel().setReportSearchTask(null);
			logger.warn("Failed to parse forum.", e);
			stop();
			return;
		}

		for (ForumThread thread : forum.getForumThreads()) {
			if (!controller.getModel().cachedReportsContains(thread)) {
				Notifier.notify("New reported post by " + thread.getAuthor().getName(), thread.getPreview(), new NotificationEventAdapter() {
					@Override
					public void clicked(NotificationEvent event) {
						super.clicked(event);
						try {
							thread.open(controller);
							controller.getModel().setOpenReport(thread);
							controller.getModel().setReportSearchTask(null);
							stop();
						} catch (OpenThreadException | IOException e) {
							logger.warn("Failed to open thread:", e);
						}
					}

					@Override
					public void closed(NotificationEvent event) {
						super.closed(event);
						controller.getModel().addMissedReport(thread);
					}
				});
				controller.getModel().addCachedReport(thread);
				break;
			}
		}

		forum.getDeletedForumThreads().stream().filter(deletedThread -> controller.getModel().missedReportsContains(deletedThread)).forEach(deletedThread -> controller.getModel().removeMissedReport(deletedThread));
	}
}
