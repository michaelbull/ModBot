package org.modbot.view;

import org.modbot.model.ForumThread;
import org.modbot.model.ModelListener;
import org.modbot.view.swing.ViewListener;

import javax.swing.Icon;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the view in the MVC pattern.
 * @author Michael Bull
 */
public abstract class View implements ModelListener {
	private final List<ViewListener> listeners = new ArrayList<>();

	protected final String url;

	protected View(String url) {
		this.url = url;
	}

	public String getURL() {
		return url;
	}

	public abstract void displayError(Throwable t);

	public abstract void displayError(String message, String title);

	public abstract void displayInformationDialog(String message, String title, Icon icon);

	public void addListener(ViewListener listener) {
		listeners.add(listener);
	}

	public void usernameFieldChanged(String username) {
		for (ViewListener listener : listeners)
			listener.notifyUsernameChange(username);
	}

	public void passwordFieldChanged(String password) {
		for (ViewListener listener : listeners)
			listener.notifyPasswordChange(password);
	}

	public void loginButtonPressed() {
		for (ViewListener listener : listeners)
			listener.notifyLoginAttempt();
	}

	public void rememberMeSelected(boolean selected) {
		for (ViewListener listener : listeners)
			listener.notifyRememberMeSelected(selected);
	}

	public void reportSearchButtonPressed() {
		for (ViewListener listener : listeners)
			listener.notifyReportSearch();
	}

	public void clearCacheButtonPressed() {
		for (ViewListener listener : listeners)
			listener.notifyClearCachedReports();
	}

	public void deleteReportButtonPressed() {
		for (ViewListener listener : listeners)
			listener.notifyDeleteReport();
	}

	public void openMissedReportButtonPressed(ForumThread missedReport) {
		for (ViewListener listener : listeners)
			listener.notifyOpenMissedReport(missedReport);
	}
}