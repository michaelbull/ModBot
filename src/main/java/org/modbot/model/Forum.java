package org.modbot.model;

import com.google.common.base.Objects;

import java.util.List;

/**
 * Represents a forum on a VBulletin website.
 * @author Michael Bull
 */
public final class Forum {
	private final int id;
	private final String title;
	private final List<ForumThread> forumThreads;
	private final List<ForumThread> deletedForumThreads;

	public Forum(int id, String title, List<ForumThread> forumThreads, List<ForumThread> deletedForumThreads) {
		this.id = id;
		this.title = title;
		this.forumThreads = forumThreads;
		this.deletedForumThreads = deletedForumThreads;
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public List<ForumThread> getForumThreads() {
		return forumThreads;
	}

	public List<ForumThread> getDeletedForumThreads() {
		return deletedForumThreads;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("id", id)
				.add("title", title)
				.add("threads", forumThreads)
				.add("deletedThreads", deletedForumThreads)
				.toString();
	}
}
