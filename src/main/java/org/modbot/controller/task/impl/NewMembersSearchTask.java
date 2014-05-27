package org.modbot.controller.task.impl;

import ch.swingfx.twinkle.event.NotificationEvent;
import ch.swingfx.twinkle.event.NotificationEventAdapter;
import com.google.gson.JsonObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.modbot.controller.Controller;
import org.modbot.controller.notification.Notifier;
import org.modbot.controller.task.Task;
import org.modbot.util.DesktopUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

/**
 * A {@link Task} that searches for reported posts in the reported posts forum (typically staff only).
 * @author Michael Bull
 */
public final class NewMembersSearchTask extends Task {
	private static final Logger logger = LoggerFactory.getLogger(NewMembersSearchTask.class);

	private final Controller controller;
	private String lastFoundUsername = "";

	public NewMembersSearchTask(Controller controller) {
		super(15, TimeUnit.SECONDS);
		this.controller = controller;
	}

	@Override
	public void execute() {
		try {
			boolean newMember = false;

			String membersList = controller.getConnection().get("members/list/?order=desc&sort=joindate");

			Document doc = Jsoup.parse(membersList);
			Elements links = doc.select("a.username");
			Element link = links.first();
			String username = link.text();
			if (!username.equals(lastFoundUsername)) {
				newMember = true;
				lastFoundUsername = username;
			}

			if (newMember) {
				logger.info("Found new user: " + lastFoundUsername);
				String vcard = controller.getConnection().get("members/" + URLEncoder.encode(lastFoundUsername, "UTF-8") + "/?do=vcard");
				BufferedReader reader = new BufferedReader(new StringReader(vcard));
				String email = "";

				String line;
				while((line=reader.readLine()) != null) {
					if (line.startsWith("EMAIL")) {
						email = line.substring(line.indexOf(":") + 1);
					}
				}

				JsonObject jsonObject = controller.member(lastFoundUsername);
				JsonObject response = jsonObject.getAsJsonObject("response");
				JsonObject prepared = response.getAsJsonObject("prepared");
				int userId = prepared.get("userid").getAsInt();

				Notifier.notify("\'" + lastFoundUsername + "\' Registered",  email, new NotificationEventAdapter() {
					@Override
					public void clicked(NotificationEvent event) {
						super.clicked(event);
						DesktopUtilities.browse(controller.getUrl() + "/infraction.php?do=report&u=" + userId + "&evader=true");
					}
				});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
