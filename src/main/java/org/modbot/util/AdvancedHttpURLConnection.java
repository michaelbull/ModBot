package org.modbot.util;

import org.modbot.model.Credentials;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a more advanced version of a {@link HttpURLConnection}.
 * @author Michael Bull
 */
public final class AdvancedHttpURLConnection {

	static {
		CookieHandler.setDefault(new CookieManager());
	}

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
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent", HttpUtilities.getHttpUserAgent());
		connection.setInstanceFollowRedirects(true);
		connection.setUseCaches(false);
		connection.connect();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				contents.append(line).append("\n");
			}
		}
		return contents.toString();
	}

	public String post(String file, Map<String, Object> values) throws IOException {
		StringBuilder contents = new StringBuilder();
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

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				contents.append(line).append("\n");
			}
		}
		return contents.toString();
	}

	public boolean login(Credentials credentials) throws IOException {
		String response = post("login.php", constructLogin(credentials.getUsername(), credentials.getPassword()));
		return response.contains("Thank you for logging in");
	}

	private HashMap<String, Object> constructLogin(String username, String password) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("do", "login");
		map.put("securitytoken", "guest");
		map.put("s", "");
		map.put("cookieuser", "1");
		map.put("vb_login_username", username);
		map.put("vb_login_password", password);
		return map;
	}
}