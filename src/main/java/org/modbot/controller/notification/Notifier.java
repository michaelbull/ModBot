package org.modbot.controller.notification;

import ch.swingfx.twinkle.NotificationBuilder;
import ch.swingfx.twinkle.event.INotificationEventListener;
import ch.swingfx.twinkle.event.NotificationEvent;
import ch.swingfx.twinkle.event.NotificationEventAdapter;
import ch.swingfx.twinkle.window.Positions;

import javax.swing.ImageIcon;

/**
 * Creates notifications that appear in the top right of the screen
 * @author Michael Bull
 * @author Cube
 */
public final class Notifier {
	public static void notify(String title, String message, final NotificationEventAdapter eventAdapter) {
		notify(new Notification(title, message, eventAdapter));
	}

	private static void notify(final Notification notification) {
		final NotificationEventAdapter eventListener = notification.getAdapter();
		new NotificationBuilder()
				.withStyle(new NotificationStyle())
				.withPosition(Positions.NORTH_EAST)
				.withDisplayTime(5000)
				.withIcon(new ImageIcon("img/trayicon.png"))
				.withTitle(notification.getTitle())
				.withMessage(notification.getMessage())
				.withListener(new INotificationEventListener() {
					@Override
					public void opened(NotificationEvent event) {
						eventListener.opened(event);
					}

					@Override
					public void clicked(NotificationEvent event) {
						eventListener.clicked(event);
					}

					@Override
					public void mouseOver(NotificationEvent event) {
						eventListener.mouseOver(event);
					}

					@Override
					public void mouseOut(NotificationEvent event) {
						eventListener.mouseOut(event);
					}

					@Override
					public void closed(NotificationEvent event) {
						eventListener.closed(event);
					}
				}).showNotification();
	}
}
