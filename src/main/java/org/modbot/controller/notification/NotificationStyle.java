package org.modbot.controller.notification;

import ch.swingfx.color.ColorUtil;
import ch.swingfx.twinkle.style.AbstractNotificationStyle;
import ch.swingfx.twinkle.style.background.ColorBackground;
import ch.swingfx.twinkle.style.closebutton.RoundCloseButton;
import ch.swingfx.twinkle.style.overlay.BorderOverlay;
import ch.swingfx.twinkle.style.overlay.GradientOverlay;
import ch.swingfx.twinkle.style.overlay.OverlayPaintMode;
import ch.swingfx.twinkle.window.NotificationWindowTypes;

import java.awt.Color;

/**
 * Represents the style that a {@link Notification} will use.
 * @author Michael Bull
 * @author Cube
 */
public final class NotificationStyle extends AbstractNotificationStyle {
	public NotificationStyle() {
		super();
		withNotificationWindowCreator(NotificationWindowTypes.DEFAULT);
		withTitleFontColor(Color.WHITE);
		withMessageFontColor(Color.WHITE);
		withAlpha(0.85f);
		withWidth(320);
		withBackground(new ColorBackground(new Color(0x10, 0x10, 0x10)));
		withWindowCornerRadius(25);
		withOverlay(new BorderOverlay(1, Color.WHITE, OverlayPaintMode.MOUSE_OVER,
				new GradientOverlay(ColorUtil.withAlpha(Color.WHITE, 0f), ColorUtil.withAlpha(Color.WHITE, 0.1f), OverlayPaintMode.MOUSE_OVER)));
		withCloseButton(new RoundCloseButton(ColorUtil.withAlpha(Color.BLACK, 0.6f), Color.WHITE).withPosition(9, 9));
	}
}
