package org.modbot.model;

import org.modbot.controller.task.impl.ReportSearchTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the model in the MVC pattern.
 * @author Michael Bull
 */
public final class Model {
	private final List<ModelListener> listeners = new ArrayList<>();
	private final List<ForumThread> cachedReports = new ArrayList<>();
	private final List<ForumThread> missedReports = new ArrayList<>();
	private Credentials credentials = new Credentials("", "");
	private boolean rememberMe = false;
	private ReportSearchTask reportSearchTask;
	private ForumThread openReport;

	public void addListener(ModelListener listener) {
		listeners.add(listener);
	}

	public boolean cachedReportsContains(ForumThread report) {
		return cachedReports.contains(report);
	}

	public void addCachedReport(ForumThread report) {
		cachedReports.add(report);
	}

	public void clearCachedReports() {
		cachedReports.clear();
		for (ModelListener listener : listeners)
			listener.notifyClearedCachedReports();
	}

	public boolean missedReportsContains(ForumThread report) {
		return missedReports.contains(report);
	}

	public void addMissedReport(ForumThread report) {
		missedReports.add(report);
		for (ModelListener listener : listeners)
			listener.notifyMissedReportsUpdated(missedReports);
	}

	public void removeMissedReport(ForumThread report) {
		missedReports.remove(report);
		for (ModelListener listener : listeners)
			listener.notifyMissedReportsUpdated(missedReports);
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public void setUsername(String username) {
		credentials = new Credentials(username, credentials.getPassword());
	}

	public void setPassword(String password) {
		credentials = new Credentials(credentials.getUsername(), password);
	}

	public void setLoadedCredentials(Credentials credentials) {
		this.credentials = credentials;
		for (ModelListener listener : listeners)
			listener.notifyCredentialsLoaded(credentials);
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
		for (ModelListener listener : listeners)
			listener.notifyRememberMeUpdated(rememberMe);
	}

	public void setLoggedIn(boolean loggedIn) {
		for (ModelListener listener : listeners)
			listener.notifyLoggedIn(getCredentials(), loggedIn, rememberMe);
	}

	public ReportSearchTask getReportSearchTask() {
		return reportSearchTask;
	}

	public void setReportSearchTask(ReportSearchTask reportSearchTask) {
		this.reportSearchTask = reportSearchTask;
		for (ModelListener listener : listeners)
			listener.notifyReportSearchTask(reportSearchTask);
	}

	public ForumThread getOpenReport() {
		return openReport;
	}

	public void setOpenReport(ForumThread openReport) {
		this.openReport = openReport;
		for (ModelListener listener : listeners)
			listener.notifyOpenedReport(openReport);
	}

	public void setDeletedThreadResponseMessage(String deletedThreadResponseMessage) {
		for (ModelListener listener : listeners)
			listener.notifyDeletedThreadResponseMessage(deletedThreadResponseMessage);
	}
}