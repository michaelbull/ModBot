package org.modbot.controller.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
	public static Forum parse(JsonObject jsonObject) throws IOException {
		List<ForumThread> forumThreads = new ArrayList<>();
		List<ForumThread> deletedForumThreads = new ArrayList<>();

		JsonObject response = jsonObject.getAsJsonObject("response");
		JsonObject forumInfo = response.getAsJsonObject("foruminfo");
		JsonArray threadBits = response.getAsJsonArray("threadbits");

		int forumId = forumInfo.get("forumid").getAsInt();
		String forumTitle = forumInfo.get("title").getAsString();

		for (JsonElement element : threadBits) {
			JsonObject threadBit = element.getAsJsonObject();

			JsonObject thread = threadBit.getAsJsonObject("thread");
			int threadId = thread.get("threadid").getAsInt();
			String threadTitle = thread.get("threadtitle").getAsString();
			String threadPosterUsername = thread.get("postusername").getAsString();
			int threadPosterId = thread.get("postuserid").getAsInt();
			String preview = thread.get("preview").getAsString();

			JsonObject show = threadBit.getAsJsonObject("show");
			boolean deleted = show.get("deletedthread").getAsInt() != 0;

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

	private ForumParser() {
	}
}
