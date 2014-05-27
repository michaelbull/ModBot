package org.modbot.model;

import org.modbot.controller.task.impl.NewMembersSearchTask;
import org.modbot.controller.task.impl.ReportSearchTask;

import java.util.List;

/**
 * An interface for a listener that is notified of changes about the {@link Model}.
 * @author Michael Bull
 */
public interface ModelListener {
	public void notifyClearedCachedReports();
	public void notifyMissedReportsUpdated(List<ForumThread> missedReports);
	public void notifyCredentialsLoaded(Credentials credentials);
	public void notifyRememberMeUpdated(boolean rememberMe);
	public void notifyLoggedIn(Credentials credentials, boolean loggedIn, boolean rememberMe);
	public void notifyReportSearchTask(ReportSearchTask task);
	public void notifyNewMembersSearchTask(NewMembersSearchTask task);
	public void notifyOpenedReport(ForumThread report);
	public void notifyDeletedThreadResponseMessage(String deletedThreadResponseMessage);
}