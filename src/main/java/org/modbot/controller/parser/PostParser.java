package org.modbot.controller.parser;

import com.google.gson.JsonObject;
import org.modbot.model.ForumMember;
import org.modbot.model.ForumThread;
import org.modbot.model.ThreadPost;

import java.util.Date;

/**
 * Parses {@link ThreadPost}s in a {@link ForumThread} on a VBulletin website.
 * @author Michael Bull
 */
public final class PostParser {
	public static ThreadPost parse(JsonObject post) {
		int postId = post.get("postid").getAsInt();
		long postTime = post.get("posttime").getAsLong();
		int userId = post.get("userid").getAsInt();
		String username = post.get("username").getAsString();
		String postTitle = post.get("title").getAsString();
		postTitle = postTitle.replace("<!-- google_ad_section_start -->", "").replace("<!-- google_ad_section_end -->", "");

		String content;
//		JsonElement message_bbcode = post.get("message_bbcode");
//		if (message_bbcode != null) {
//			content = message_bbcode.getAsString();
//		} else {
//			content = post.get("message_plain").getAsString();
//		}
		content = post.get("message").getAsString();
		content = content.replace("<!-- google_ad_section_start -->", "").replace("<!-- google_ad_section_end -->", "");

		ForumMember user = new ForumMember(userId, username);
		Date date = new Date(postTime * 1000);
		return new ThreadPost(postId, postTitle, user, date, content);
	}

	private PostParser() {
	}
}
