package org.modbot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

/**
 * Provides desktop related utilities such as opening a web browser or a mail client.
 * @author Michael Bull
 */
public final class DesktopUtilities {
	private static final Logger logger = LoggerFactory.getLogger(DesktopUtilities.class);

	public static void viewThread(String url, int threadId) {
		browse(url + "/showthread.php?t=" + threadId);
	}

	public static void viewPost(String url, int threadId, int postId) {
		browse(url + "/showthread.php?t=" + threadId + "&p=" + postId);
	}

	public static void viewUserProfile(String url, int userId) {
		browse(url + "/member.php?u=" + userId);
	}

	public static void mailTo(String url) {
		try {
			Desktop.getDesktop().mail(URI.create(url));
		} catch (IOException e) {
			logger.warn("Failed to open mail client:", e);
		}
	}

	private static void browse(String url) {
		try {
			Desktop.getDesktop().browse(URI.create(url));
		} catch (IOException e) {
			logger.warn("Failed to open browser client:", e);
		}
	}

}
