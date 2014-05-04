package org.modbot.controller;

import com.google.common.base.Objects;
import json.JsonObject;
import org.modbot.util.OpenThreadException;
import org.modbot.controller.task.TaskScheduler;
import org.modbot.controller.task.impl.ReportSearchTask;
import org.modbot.model.Credentials;
import org.modbot.model.ForumThread;
import org.modbot.model.Model;
import org.modbot.util.AdvancedHttpURLConnection;
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
	private final AdvancedHttpURLConnection connection;
	private final String apiKey;

	private int apiVersion;
	private String apiClientId;
	private String apiAccessToken;
	private String secret;

	public Controller(Model model, String url, String apiKey) {
		this.model = model;
		this.connection = new AdvancedHttpURLConnection(url);
		this.apiKey = apiKey;
	}

	public void init() throws IOException, NoSuchAlgorithmException {
		/* initialise mobile api */
		Query apiInit = new Query.Builder("api_init", apiVersion, apiClientId, apiAccessToken, secret, apiKey)
				.parameter("clientname", CLIENT_NAME)
				.parameter("platformname", CLIENT_NAME)
				.parameter("clientversion", CLIENT_VERSION)
				.parameter("platformversion", CLIENT_VERSION)
				.parameter("uniqueid", UNIQUE_ID)
				.build();

		JsonObject response = apiInit.run(connection);
		apiVersion = response.get("apiversion").asInt();
		apiClientId = response.get("apiclientid").asString();
		apiAccessToken = response.get("apiaccesstoken").asString();
		secret = response.get("secret").asString();
		logger.info("Initialised " + this + ".");
	}

	public boolean login() {
		Credentials credentials = model.getCredentials();
		Query login = new Query.Builder("login_login", apiVersion, apiClientId, apiAccessToken, secret, apiKey)
				.parameter("vb_login_username", credentials.getUsername())
				.parameter("vb_login_password", credentials.getPassword())
				.parameter("logintype", "modcplogin")
				.parameter("debug", "1")
				.build();

		JsonObject response;
		try {
			response = login.run(connection);
		} catch (IOException e) {
			logger.warn("Failed to login: ", e);
			return false;
		}

		try {
			JsonObject session = response.get("session").asObject();
			int uid = session.get("userid").asInt();
			return uid != 0;
		} catch (NullPointerException | UnsupportedOperationException e) {
			logger.warn("Failed to login: ", e);
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

	public Model getModel() {
		return model;
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
	public void notifyClearCachedReports() {
		model.clearCachedReports();
	}

	@Override
	public void notifyDeleteReport() {
		try {
			JsonObject deleteObject = deleteThread(model.getOpenReport().getId());
			String errorMessage = deleteObject.get("response").asObject().get("errormessage").asString();
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