package org.modbot.model;

import com.google.common.base.Objects;

public final class ForumMember {
	private final int id;
	private final String name;

	public ForumMember(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ForumMember forumMember = (ForumMember) o;
		return id == forumMember.id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("id", id)
				.add("name", name)
				.toString();
	}
}
