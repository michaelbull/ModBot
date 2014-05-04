package org.modbot.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Represents a more advanced version of a {@link HttpURLConnection}.
 * @author Michael Bull
 */
public final class AdvancedHttpURLConnection {
	private final String url;

	public AdvancedHttpURLConnection(String url) {
		if (url.charAt(url.length() - 1) != '/') {
			url += "/";
		}
		this.url = url;
	}

	public String get(String file) throws IOException {
		StringBuilder contents = new StringBuilder();
		HttpURLConnection connection = (HttpURLConnection) new URL(url + file).openConnection();
		connection.setRequestProperty("User-Agent", HttpUtilities.getHttpUserAgent());
		connection.setInstanceFollowRedirects(false);
		connection.setUseCaches(false);
		connection.connect();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				contents.append(line).append("\n");
			}
		}
		return contents.toString();
	}

	public String post(String file, Map<String, Object> values) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url + file).openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("User-Agent", HttpUtilities.getHttpUserAgent());
		connection.setInstanceFollowRedirects(false);
		connection.setUseCaches(false);
		connection.connect();

		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		writer.write(HttpUtilities.implode(values));
		writer.flush();

		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder contents = new StringBuilder();
		while (true) {
			String line = reader.readLine();
			if (line == null) {
				break;
			}
			contents.append(line).append("\n");
		}
		return contents.toString();
	}
}