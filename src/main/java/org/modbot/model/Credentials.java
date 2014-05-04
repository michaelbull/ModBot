package org.modbot.model;

import com.google.common.base.Objects;

/**
 * Represents a user's credentials on the forum.
 * @author Michael Bull
 */
public final class Credentials {

	private final String username;
	private final String password;

	public Credentials(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("username", username)
				.toString();
	}
}