package org.modbot.controller.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.modbot.model.Forum;
import org.modbot.model.ForumThread;
import org.modbot.model.ThreadPost;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses {@link ForumThread}s in a {@link Forum} on a VBulletin website.
 * @author Michael Bull
 */
public final class ThreadParser {
	public static List<ThreadPost> parse(JsonObject jsonObject) throws IOException {
		List<ThreadPost> threadPosts = new ArrayList<>();
		JsonObject response = jsonObject.getAsJsonObject("response");
		int totalPosts = response.get("totalposts").getAsInt();

		if (totalPosts > 1) {
			JsonArray postBits = response.getAsJsonArray("postbits");
			for (JsonElement element : postBits) {
				JsonObject postBit = element.getAsJsonObject();

				JsonObject post = postBit.getAsJsonObject("post");
				ThreadPost threadPost = PostParser.parse(post);
				threadPosts.add(threadPost);
			}
		} else {
			JsonObject postBits = response.getAsJsonObject("postbits");
			JsonObject post = postBits.getAsJsonObject("post");
			ThreadPost threadPost = PostParser.parse(post);
			threadPosts.add(threadPost);
		}
		return threadPosts;
	}

	private ThreadParser() {
	}
}
