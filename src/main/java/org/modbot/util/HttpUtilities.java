package org.modbot.util;

import java.util.Iterator;
import java.util.Map;

/**
 * Contains HTTP related utility methods.
 * @author Michael Bull
 */
public final class HttpUtilities {
	public static String implode(Map<String, Object> values) {
		StringBuilder builder = new StringBuilder();
		Iterator<Map.Entry<String, Object>> iterator = values.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, Object> entry = iterator.next();
			builder.append(entry.getKey()).append("=").append(entry.getValue());
			if (iterator.hasNext()) {
				builder.append("&");
			}
		}
		return builder.toString();
	}

	public static String getHttpUserAgent() {
		String os = System.getProperty("os.name").toLowerCase();
		String agent = "Mozilla/5.0 (X11; U; Linux i686; en-GB; en-US; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6";
		if (os.contains("mac")) {
			agent = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_5_8; en-us) AppleWebKit/531.9 (KHTML, like Gecko) Version/4.0.3 Safari/531.9";
		} else if (os.contains("windows")) {
			agent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6";
		}
		return agent;
	}
}