package org.modbot.util;

import org.modbot.model.ForumThread;

/**
 * An exception that occurs when trying to open a previously opened {@link ForumThread}.
 * @author Michael Bull
 */
public final class OpenThreadException extends Exception {
	public OpenThreadException() {
		super("this thread has already been opened and parsed");
	}
}