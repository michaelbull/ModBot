package org.modbot.controller.parser;

import json.JsonObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.modbot.model.ForumMember;
import org.modbot.model.ForumThread;
import org.modbot.model.ThreadPost;

import java.util.Date;

/**
 * Parses {@link ThreadPost}s in a {@link ForumThread} on a VBulletin website.
 * @author Michael Bull
 */
public final class PostParser {
	public static ThreadPost parse(JsonObject postObject) {
		int postId = Integer.parseInt(postObject.get("postid").asString());
		long postTime = Long.parseLong(postObject.get("posttime").asString());
		int posterId = Integer.parseInt(postObject.get("userid").asString());
		String posterUsername = postObject.get("username").asString();
		String postTitle = postObject.get("title").asString();
		postTitle = postTitle.replace("<!-- google_ad_section_start -->", "").replace("<!-- google_ad_section_end -->", "");

		String content;
//		if (postObject.get("message_bbcode") != null) {
//			content = postObject.get("message_bbcode").asString();
//		} else {
//			content = postObject.get("message_plain").asString();
//		}
		content = postObject.get("message").asString();
		content = content.replace("<!-- google_ad_section_start -->", "").replace("<!-- google_ad_section_end -->", "");

		Document doc = Jsoup.parse(content);
		Elements images = doc.getElementsByTag("img");
		for (Element image : images) {
			String url = image.attr("src");
			if (!url.startsWith("http") && !url.startsWith("www")) {
				image.remove();
			}
		}
		content = doc.html();

		ForumMember user = new ForumMember(posterId, posterUsername);
		Date date = new Date(postTime * 1000);
        return new ThreadPost(postId, postTitle, user, date, content);
	}
}
