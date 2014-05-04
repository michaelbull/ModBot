package org.modbot.controller.parser;

import json.JsonArray;
import json.JsonObject;
import json.JsonValue;
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
	public static List<ThreadPost> parse(JsonObject threadObject) throws IOException {
		List<ThreadPost> threadThreadPosts = new ArrayList<>();

		JsonObject response = threadObject.get("response").asObject();
		int totalPosts = response.get("totalposts").asInt();

		if (totalPosts > 1) {
			JsonArray postBits = response.get("postbits").asArray();
			for (JsonValue postBit : postBits) {
				JsonObject post = postBit.asObject().get("post").asObject();
				ThreadPost threadPost = PostParser.parse(post);
				threadThreadPosts.add(threadPost);
			}
		} else {
			JsonObject postBits = response.get("postbits").asObject();
			JsonObject post = postBits.get("post").asObject();
			ThreadPost threadPost = PostParser.parse(post);
			threadThreadPosts.add(threadPost);
		}
		return threadThreadPosts;
	}
}
