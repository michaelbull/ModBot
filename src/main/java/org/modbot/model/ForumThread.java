package org.modbot.model;

import com.google.common.base.Objects;
import org.modbot.controller.Controller;
import org.modbot.controller.parser.ThreadParser;
import org.modbot.util.OpenThreadException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a thread in a {@link Forum}.
 */
public final class ForumThread {
	private final int id;
	private final String title;
	private final ForumMember author;
	private final String preview;
	private final boolean deleted;
	private final List<ThreadPost> threadPosts;

	public ForumThread(int id, String title, ForumMember author, String preview, boolean deleted, List<ThreadPost> threadPosts) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.preview = preview;
		this.deleted = deleted;
		this.threadPosts = threadPosts;
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public ForumMember getAuthor() {
		return author;
	}

	public String getPreview() {
		return preview;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public List<ThreadPost> getThreadPosts() {
		return threadPosts;
	}

	/**
	 * Opens this thread and parses the posts inside of it.
	 */
	public void open(Controller controller) throws OpenThreadException, IOException {
		if (threadPosts.size() != 0) {
			throw new OpenThreadException();
		}

		List<ThreadPost> parsedThreadPosts = ThreadParser.parse(controller.showThread(id));
		threadPosts.addAll(parsedThreadPosts.stream().collect(Collectors.toList()));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ForumThread forumThread = (ForumThread) o;
		return id == forumThread.id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("id", id)
				.add("title", title)
				.add("author", author)
				.add("preview", preview)
				.add("deleted", deleted)
				.add("threadPosts", threadPosts)
				.toString();
	}
}
