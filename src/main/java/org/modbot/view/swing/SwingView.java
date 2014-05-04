package org.modbot.view.swing;

import org.modbot.ModBot;
import org.modbot.controller.task.impl.ReportSearchTask;
import org.modbot.model.Credentials;
import org.modbot.model.ForumThread;
import org.modbot.model.io.CredentialsCodec;
import org.modbot.view.View;
import org.modbot.view.swing.screen.LoginScreen;
import org.modbot.view.swing.screen.MainScreen;
import org.modbot.view.swing.screen.ReportScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.Image;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.NoSuchFileException;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * A view that incorporates the {@link javax.swing} library.
 * @author Michael Bull
 */
public final class SwingView extends View {
	private ImageIcon LOGO = new ImageIcon("img/logo.png");

	private static final Logger logger = LoggerFactory.getLogger(SwingView.class);

	private final LoginScreen loginScreen = new LoginScreen(this);
	private final MainScreen mainScreen = new MainScreen(this);
	private final ReportScreen reportScreen = new ReportScreen(this);
	private final JFrame mainFrame = new JFrame("ModBot v" + ModBot.MAJOR_VERSION + "." + ModBot.MINOR_VERSION);

	public SwingView(String url) {
		super(url);

		// sets the dock icon to the logo on OS X
		try {
			Class<?> util = Class.forName("com.apple.eawt.Application");
			Method getApplication = util.getMethod("getApplication", new Class[0]);
			Object application = getApplication.invoke(util);
			Method setDockIconImage = util.getMethod("setDockIconImage",  Image.class);
			setDockIconImage.invoke(application, LOGO.getImage());
		} catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			/* empty */
		}

		setScreen(loginScreen);
		mainFrame.setVisible(true);
	}

	public void setScreen(Screen screen) {
		mainFrame.setContentPane(screen.getContentPane());
		mainFrame.pack();
		mainFrame.setMinimumSize(mainFrame.getPreferredSize());
		mainFrame.setLocationRelativeTo(null); // centers the application in the middle of the screen
	}

	public MainScreen getMainScreen() {
		return mainScreen;
	}

	@Override
	public void displayError(Throwable t) {
		StringBuilder message = new StringBuilder(/*"An exception occurred: "*/);

		try (StringWriter writer = new StringWriter(); PrintWriter printWriter = new PrintWriter(writer)) {
			t.printStackTrace(printWriter);
			message.append(writer.toString());
		} catch (IOException ex) {
			/* cannot occur */
		}

		t.printStackTrace();
		displayError(message.toString(), "An exception occurred:");
	}

	@Override
	public void displayError(String message, String title) {
		JOptionPane.showMessageDialog(mainFrame, message, title, JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void displayInformationDialog(String message, String title, Icon icon) {
		JOptionPane.showMessageDialog(mainFrame, message, title, JOptionPane.INFORMATION_MESSAGE, icon == null ? LOGO : icon);
	}

	@Override
	public void notifyClearedCachedReports() {
		displayInformationDialog("All cached reports cleared.", "Cache Cleared", null);
	}

	@Override
	public void notifyMissedReportsUpdated(List<ForumThread> missedReports) {
		mainScreen.updateMissedReports(missedReports);
	}

	@Override
	public void notifyCredentialsLoaded(Credentials credentials) {
		loginScreen.setCredentials(credentials);
		loginScreen.setRememberMeSelected(true);
	}

	@Override
	public void notifyRememberMeUpdated(boolean rememberMe) {
		loginScreen.setRememberMeSelected(rememberMe);
	}

	@Override
	public void notifyLoggedIn(Credentials credentials, boolean loggedIn, boolean rememberMe) {
		if (loggedIn) {
			if (rememberMe) {
				try {
					CredentialsCodec.write(credentials);
				} catch (IOException | GeneralSecurityException e) {
					logger.warn("Failed to save credentials:", e);
				}
			} else {
				try {
					if (!CredentialsCodec.delete()) {
						logger.warn("Failed to delete existing saved credentials:", new NoSuchFileException(CredentialsCodec.PATH.toFile().toString()));
					}
				} catch (IOException e) {
					logger.warn("Failed to delete existing saved credentials:", e);
				}
			}
			displayInformationDialog("Login successful", "Login", null);
			setScreen(mainScreen);
		} else {
			displayInformationDialog("Login failed", "Login", null);
		}
	}

	@Override
	public void notifyReportSearchTask(ReportSearchTask task) {
		boolean started = (task != null);
//		if (started) {
//			displayInformationDialog("Started searching for reports.", "Report Search", null);
//		}
		mainScreen.setReportsSearchButtonText(started ? "Stop report search" : "Start report search");
	}

	@Override
	public void notifyOpenedReport(ForumThread report) {
		reportScreen.setReport(report);
		setScreen(reportScreen);
	}

	@Override
	public void notifyDeletedThreadResponseMessage(String deletedThreadResponseMessage) {
		if (deletedThreadResponseMessage.equals("redirect_inline_deleted")) {
			displayInformationDialog("Successfully deleted forum thread.", "Thread Deleted", null);
		} else {
			displayInformationDialog("Failed to delete forum thread.", "Thread Deleted", null);
		}
	}
}