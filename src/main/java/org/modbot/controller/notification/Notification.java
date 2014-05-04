package org.modbot.controller.notification;

import ch.swingfx.twinkle.event.NotificationEventAdapter;

/**
 * Represents a notification.
 * @author Michael Bull
 * @author Cube
 */
public final class Notification {
	private final String title;
	private final String message;
	private final NotificationEventAdapter adapter;

	public Notification(String title, String message, NotificationEventAdapter adapter) {
		this.title = title;
		this.message = message;
		this.adapter = adapter;
	}

	public String getTitle() {
		return title;
	}

	public String getMessage() {
		return message;
	}

	public NotificationEventAdapter getAdapter() {
		return adapter;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Notification that = (Notification) o;

		if (message != null ? !message.equals(that.message) : that.message != null) {
			return false;
		}
		if (title != null ? !title.equals(that.title) : that.title != null) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int result = title != null ? title.hashCode() : 0;
		result = 31 * result + (message != null ? message.hashCode() : 0);
		return result;
	}
}
