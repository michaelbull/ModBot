package org.modbot.view.swing;

import org.modbot.model.ForumThread;
import org.modbot.view.View;

/**
 * An interface for a listener that is notified of changes about the {@link View}.
 * @author Michael Bull
 */
public interface ViewListener {
	public void notifyUsernameChange(String username);
	public void notifyPasswordChange(String password);
	public void notifyLoginAttempt();
	public void notifyRememberMeSelected(boolean selected);
	public void notifyReportSearch();
	public void notifyNewMembersSearch();
	public void notifyClearCachedReports();
	public void notifyDeleteReport();
	public void notifyOpenMissedReport(ForumThread report);
}