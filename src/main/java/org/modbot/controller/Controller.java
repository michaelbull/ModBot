package org.modbot.controller;

import com.google.common.base.Objects;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.modbot.controller.task.TaskScheduler;
import org.modbot.controller.task.impl.NewMembersSearchTask;
import org.modbot.controller.task.impl.ReportSearchTask;
import org.modbot.model.Credentials;
import org.modbot.model.ForumMember;
import org.modbot.model.ForumThread;
import org.modbot.model.Model;
import org.modbot.util.AdvancedHttpURLConnection;
import org.modbot.util.OpenThreadException;
import org.modbot.view.swing.ViewListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * The VBulletin mobile API controller.
 * @author Michael Bull
 * @see <a href="http://www.vbulletin.com/vbcms/content.php/334-mobile-api">http://www.vbulletin.com/vbcms/content.php/334-mobile-api</a>
 */
public final class Controller implements ViewListener {
	private static final String CLIENT_NAME = "ModBot";
	private static final String CLIENT_VERSION = "1.0";
	private static final String UNIQUE_ID = "777888999111444333";

	private static final Logger logger = LoggerFactory.getLogger(Controller.class);

	private final TaskScheduler taskScheduler = new TaskScheduler();
	private final Model model;
	private final String url;
	private final AdvancedHttpURLConnection connection;
	private final String apiKey;

	private int apiVersion;
	private String apiClientId;
	private String apiAccessToken;
	private String secret;

	public Controller(Model model, String url, String apiKey) {
		this.model = model;
		this.url = url;
		this.connection = new AdvancedHttpURLConnection(url);
		this.apiKey = apiKey;
	}

	/**
	 * api_init is the first method of the API that a client should call.
	 * Client should send its name and version.
	 * The method will return information of the API such as API version.
	 * Also it will return the session hash to the client.
	 * @throws IOException If an IOException occurs
	 * @throws NoSuchAlgorithmException If a NoSuchAlgorithmException
	 */
	public void apiInit() throws IOException, NoSuchAlgorithmException {
		Query apiInit = new Query.Builder("api_init", apiVersion, apiClientId, apiAccessToken, secret, apiKey)
				.parameter("clientname", CLIENT_NAME)
				.parameter("platformname", CLIENT_NAME)
				.parameter("clientversion", CLIENT_VERSION)
				.parameter("platformversion", CLIENT_VERSION)
				.parameter("uniqueid", UNIQUE_ID)
				.build();

		JsonObject response = apiInit.run(connection);
		apiVersion = response.get("apiversion").getAsInt();
		apiClientId = response.get("apiclientid").getAsString();
		apiAccessToken = response.get("apiaccesstoken").getAsString();
		secret = response.get("secret").getAsString();
	}

	@SuppressWarnings("unchecked")
	public boolean login() {
		Credentials credentials = model.getCredentials();

		// login via http also for GET requests
		try {
			boolean successful = connection.login(credentials);
			if (!successful) {
				logger.warn("Failed to login via HTTP.");
			} else {
				logger.info("Successfully logged in via HTTP to " + credentials.getUsername() + ".");
			}
		} catch (IOException e) {
			logger.warn("Failed to login via HTTP:", e);
		}


		Query login = new Query.Builder("login_login", apiVersion, apiClientId, apiAccessToken, secret, apiKey)
				.parameter("vb_login_username", credentials.getUsername())
				.parameter("vb_login_password", credentials.getPassword())
				.parameter("logintype", "modcplogin")
				.parameter("debug", "1")
				.build();

		JsonObject jsonObject;
		try {
			jsonObject = login.run(connection);
		} catch (IOException e) {
			logger.warn("Failed to login:", e);
			return false;
		}

		try {
			// $return['response']['errormessage'] is an array.
			// Its first item is always an error ID (underlying it's actually the error message phrase name).
			// Other items are params coupled with the error.
			JsonObject response = jsonObject.getAsJsonObject("response");
			JsonArray errorMessage = response.get("errormessage").getAsJsonArray();
			String errorId = errorMessage.get(0).getAsString();

			switch (errorId) {
				case "redirect_login":
					String username = errorMessage.get(1).getAsString();
					JsonObject session = jsonObject.getAsJsonObject("session");
					double userId = session.get("userid").getAsInt();
					ForumMember member = new ForumMember((int) userId, username);
					logger.info("Successfully logged into " + member + ".");
					return true;
				case "badlogin":  // called if ($vbulletin->GPC['vb_login_username'] == '')
					logger.warn("Failed to login as the provided user credentials were invalid.");
					return false;
				case "strikes":  // called if ($strikes['strikes'] >= 5 AND $strikes['lasttime'] > TIMENOW - 900)
					logger.warn("Failed to login as we have entered invalid credentials more than 5 times in 15 minutes.");
					return false;
				case "badlogin_strikes_passthru":  // called if $vbulletin->options['usestrikesystem'] is true
					int loginFailedTimes = errorMessage.get(3).getAsInt();
					logger.warn("Failed to login due to invalid user credentials. We have attempted to login " + loginFailedTimes + " times recently.");
					return false;
				case "badlogin_passthru":
					logger.warn("Failed to login due to invalid user credentials.");
					return false;
				default:
					logger.warn("Failed to login.");
					return false;
			}
		} catch (NullPointerException | UnsupportedOperationException e) {
			logger.warn("Failed to login:", e);
			return false;
		}
	}

	public JsonObject forumDisplay(int forumId) throws IOException {
		Query query = new Query.Builder("forumdisplay", apiVersion, apiClientId, apiAccessToken, secret, apiKey)
				.parameter("forumid", forumId)
				.build();
		return query.run(connection);
	}

	public JsonObject showThread(int threadId) throws IOException {
		Query query = new Query.Builder("showthread", apiVersion, apiClientId, apiAccessToken, secret, apiKey)
				.parameter("threadid", threadId)
				.build();
		return query.run(connection);
	}

	public JsonObject deleteThread(int threadId) throws IOException {
		Query query = new Query.Builder("inlinemod_dodeletethreads", apiVersion, apiClientId, apiAccessToken, secret, apiKey)
				.parameter("threadids", threadId)
				.parameter("deletereason", "Thread deleted by ModBot.")
				.parameter("deletetype", 1)
				.build();
		return query.run(connection);
	}

	public JsonObject member(String username) throws IOException {
		Query query = new Query.Builder("member", apiVersion, apiClientId, apiAccessToken, secret, apiKey)
				.parameter("username", username)
				.build();
		return query.run(connection);
	}

	public Model getModel() {
		return model;
	}

	public AdvancedHttpURLConnection getConnection() {
		return connection;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("apiVersion", apiVersion)
				.add("apiClientId", apiClientId)
				.add("apiAccessToken", apiAccessToken)
				.add("secret", secret)
				.toString();
	}

	@Override
	public void notifyUsernameChange(String username) {
		model.setUsername(username);
	}

	@Override
	public void notifyPasswordChange(String password) {
		model.setPassword(password);
	}

	@Override
	public void notifyLoginAttempt() {
		boolean successful = login();
		model.setLoggedIn(successful);
	}

	@Override
	public void notifyRememberMeSelected(boolean selected) {
		model.setRememberMe(selected);
	}

	@Override
	public void notifyReportSearch() {
		ReportSearchTask existingTask = model.getReportSearchTask();
		if (existingTask != null) {
			existingTask.stop();
			model.setReportSearchTask(null);
		} else {
			ReportSearchTask task = new ReportSearchTask(this);
			model.setReportSearchTask(task);
			taskScheduler.start(task, true);
		}
	}

	@Override
	public void notifyNewMembersSearch() {
		NewMembersSearchTask existingTask = model.getNewMembersSearchTask();
		if (existingTask != null) {
			existingTask.stop();
			model.setNewMembersSearchTask(null);
		} else {
			NewMembersSearchTask task = new NewMembersSearchTask(this);
			model.setNewMembersSearchTask(task);
			taskScheduler.start(task, true);
		}
	}

	@Override
	public void notifyClearCachedReports() {
		model.clearCachedReports();
	}

	@Override
	public void notifyDeleteReport() {
		try {
			JsonObject jsonObject = deleteThread(model.getOpenReport().getId());
			JsonObject response = jsonObject.getAsJsonObject("response");
			String errorMessage = response.get("errormessage").getAsString();
			model.setDeletedThreadResponseMessage(errorMessage);
		} catch (IOException e) {
			logger.warn("Failed to delete report:", e);
		}
	}

	@Override
	public void notifyOpenMissedReport(ForumThread report) {
		try {
			report.open(this);
			model.removeMissedReport(report);
			model.setOpenReport(report);
			ReportSearchTask task = model.getReportSearchTask();
			if (task != null) {
				task.stop();
				model.setReportSearchTask(null);
			}
		} catch (OpenThreadException | IOException e) {
			logger.warn("Failed to open thread:", e);
		}
	}
}