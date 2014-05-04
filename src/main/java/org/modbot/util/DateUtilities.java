package org.modbot.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Date related utility methods.
 * @author Michael Bull
 */
public final class DateUtilities {
	private static final String TIME_ZONE_IDENTIFIER = "Europe/London";

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy, HH:mm z");

	static {
		dateFormat.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_IDENTIFIER));
	}

	public static String formatDate(Date date) {
		return dateFormat.format(date);
	}
}