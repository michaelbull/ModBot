package org.modbot.controller.parser;

import json.JsonArray;
import json.JsonObject;
import json.JsonValue;
import org.modbot.model.Forum;
import org.modbot.model.ForumMember;
import org.modbot.model.ForumThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses {@link Forum}s on a VBulletin website.
 * @author Michael Bull
 */
public final class ForumParser {
	public static Forum parse(JsonObject forumObject) throws IOException {
		List<ForumThread> forumThreads = new ArrayList<>();
		List<ForumThread> deletedForumThreads = new ArrayList<>();

		JsonObject response = forumObject.get("response").asObject();
		JsonObject forumInfo = response.get("foruminfo").asObject();
		JsonArray threadBits = response.get("threadbits").asArray();

		int forumId = forumInfo.get("forumid").asInt();
		String forumTitle = forumInfo.get("title").asString();

		for (JsonValue threadBit : threadBits) {
			JsonObject thread = threadBit.asObject().get("thread").asObject();

			int threadId = Integer.parseInt(thread.get("threadid").asString());
			String threadTitle = thread.get("threadtitle").asString();
			String threadPosterUsername = thread.get("postusername").asString();
			int threadPosterId = Integer.parseInt(thread.get("postuserid").asString());
			String preview = thread.get("preview").asString();

			JsonObject show = threadBit.asObject().get("show").asObject();
			boolean deleted = show.get("deletedthread").asInt() != 0;

			ForumMember threadPoster = new ForumMember(threadPosterId, threadPosterUsername);
			ForumThread forumThread = new ForumThread(threadId, threadTitle, threadPoster, preview, deleted, new ArrayList<>());
			if (deleted) {
				deletedForumThreads.add(forumThread);
			} else {
				forumThreads.add(forumThread);
			}
		}
		return new Forum(forumId, forumTitle, forumThreads, deletedForumThreads);
	}

}
