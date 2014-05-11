package org.modbot.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.modbot.util.AdvancedHttpURLConnection;
import org.modbot.util.VBulletinUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents a query that can be sent to the VBulletin mobile API.
 * @author Michael Bull
 */
public final class Query {
	private static final Logger logger = LoggerFactory.getLogger(Query.class);

	public static class Builder  {
		private final String method;
		private final int apiVersion;
		private final String apiKey;
		private final Map<String, Object> parameters = new TreeMap<>();

		/**
		 * The ClientID is required for each API call (except the first call of api_init).
		 * It's used for identify the client itself.
		 * A client will get its secret key by calling api_init for the first time.
		 */
		private final String apiClientId;

		/**
		 * An Access Token is required for each API call (except the first call of api_init).
		 * It's used for authenticate users and assign correct permission to logged-in users.
		 * A client will get a new or old accesstoken by calling api_init each time.
		 * The client should save it for further usage.
		 */
		private final String apiAccessToken;

		/**
		 * The Secret Key is used for generating signatures/validating responses.
		 * A client will get its secret key by calling api_init for the first time.
		 * It should save the secret key safely and never pass it through the network.
		 */
		private final String secret;

		public Builder(String method, int apiVersion, String apiClientId, String apiAccessToken, String secret, String apiKey) {
			this.method = method;
			this.apiVersion = apiVersion;
			this.apiClientId = apiClientId;
			this.apiAccessToken = apiAccessToken;
			this.secret = secret;
			this.apiKey = apiKey;
		}

		public Builder parameter(String key, Object value) {
			parameters.put(key, value);
			return this;
		}

		public Query build() {
			return new Query(this);
		}
	}

	private final Map<String, Object> parameters;

	public Query(Builder builder) {
		String signature = "";
		try {
			// The signature is the md5 value of the method + accesstoken + clientid + secret + API key
			signature = VBulletinUtilities.md5Hex("api_m=" + builder.method + builder.apiAccessToken + builder.apiClientId + builder.secret + builder.apiKey);
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
			logger.warn("Failed to create signature for query:", e);
		}
		this.parameters = builder.parameters;
		this.parameters.put("api_v", builder.apiVersion);
		this.parameters.put("api_c", builder.apiClientId);
		this.parameters.put("api_s", builder.apiAccessToken);
		this.parameters.put("api_sig", signature);
		this.parameters.put("api_m", builder.method);
	}

	public JsonObject run(AdvancedHttpURLConnection connection) throws IOException {
		String apiResponse = connection.post(getMethodURL(), parameters);
		return new Gson().fromJson(apiResponse, JsonObject.class);
	}

	private String getMethodURL() {
		return "api.php?api_m=" + parameters.get("api_m");
	}
}