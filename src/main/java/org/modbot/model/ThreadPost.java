package org.modbot.model;

import com.google.common.base.Objects;
import org.modbot.util.DateUtilities;

import java.util.Date;

/**
 * Represents a single post by a {@link ForumMember} in a {@link ForumThread}.
 * @author Michael Bull
 */
public final class ThreadPost {
	private final int id;
	private final String title;
	private final ForumMember author;

	private final Date date;

	private final String content;

	public ThreadPost(int id, String title, ForumMember author, Date date, String content) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.date = date;
		this.content = content;
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

	public Date getDate() {
		return date;
	}

	public String getContent() {
		return content;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("id", id)
				.add("title", title)
				.add("author", author)
				.add("date", DateUtilities.formatDate(date))
				.add("content", content)
				.toString();
	}
}
