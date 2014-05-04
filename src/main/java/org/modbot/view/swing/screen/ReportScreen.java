package org.modbot.view.swing.screen;

import org.modbot.model.Forum;
import org.modbot.model.ForumThread;
import org.modbot.model.ThreadPost;
import org.modbot.util.DateUtilities;
import org.modbot.util.DesktopUtilities;
import org.modbot.view.swing.Screen;
import org.modbot.view.swing.SwingView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;

/**
 * Represents a screen that is shown to detail the content of a report made on the {@link Forum}.
 * @author Michael Bull
 */
public final class ReportScreen extends Screen {
	private static final Logger logger = LoggerFactory.getLogger(ReportScreen.class);

	private final JScrollPane contentPane;
	private final JButton authorButton = new JButton();
	private final JEditorPane postContentPane = new JEditorPane();
	private final JButton viewReportButton = new JButton("View");
	private final JButton deleteReportButton = new JButton("Delete");

	public ReportScreen(SwingView view) {
		super(view);

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Details"));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 0, 0);

		panel.add(new JLabel("Reported by:"), c);
		c.gridx++;
		panel.add(authorButton, c);

		c.gridx = 0;
		c.gridy++;
		c.ipady = 20;
		c.insets = new Insets(25, 25, 0, 25);
		panel.add(viewReportButton, c);

		c.gridx++;
		panel.add(deleteReportButton, c);

		c.gridx++;
		c.gridx++;
		JButton dismissReportButton = new JButton("Dismiss");
		dismissReportButton.addActionListener(e -> view.setScreen(view.getMainScreen()));
		panel.add(dismissReportButton, c);

		postContentPane.setContentType("text/html");
		postContentPane.setEditable(false);
		postContentPane.setOpaque(false);
		postContentPane.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent event) {
				if (!event.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
					return;
				}

				String description = event.getDescription();
				if (description.startsWith("vb:member") && description.contains("u=")) {
					int memberId = Integer.parseInt(description.substring(description.indexOf("u=") + 2));
					DesktopUtilities.viewUserProfile(view.getURL(), memberId);
				} else if (description.startsWith("vb:showthread") && description.contains("t=")) {
					String threadText = description.substring(description.indexOf("t="));
					int threadId = Integer.parseInt(threadText.substring(2, threadText.indexOf("/")));
//						String postText = description.substring(description.indexOf("p="));
					int postId = Integer.parseInt(description.substring(description.indexOf("p=") + 2));
					DesktopUtilities.viewPost(view.getURL(), threadId, postId);
				} else if (description.startsWith("vb:editpost") && description.contains("p=")) {
					String threadText = description.substring(description.indexOf("p="));
					int threadId = Integer.parseInt(threadText.substring(2));
					DesktopUtilities.viewThread(view.getURL(), threadId);
				} else if (description.startsWith("mailto:")) {
					DesktopUtilities.mailTo(description);
				} else {
					logger.warn("Unhandled URL description: " + description + ".");
				}
			}
		});

		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 5;
		c.ipady = 0;
		c.insets = new Insets(5, 5, 0, 0);
		panel.add(postContentPane, c);

		contentPane = new JScrollPane(panel);
	}

	public void removeAllActionListeners(AbstractButton button) {
		for (ActionListener actionListener : button.getActionListeners()) {
			button.removeActionListener(actionListener);
		}
	}

	public void setReport(ForumThread report) {
		authorButton.setText(report.getAuthor().getName());
		removeAllActionListeners(authorButton);
		authorButton.addActionListener(event -> {
			try {
				Desktop.getDesktop().browse(URI.create(view.getURL() + "/member.php?u=" + report.getAuthor().getId()));
			} catch (IOException e) {
				logger.warn("Failed to open member profile:", logger);
			}
		});

		ThreadPost post = report.getThreadPosts().get(0);
		postContentPane.setBorder(BorderFactory.createTitledBorder(DateUtilities.formatDate(post.getDate())));
		postContentPane.setText(post.getContent());

		removeAllActionListeners(viewReportButton);
		viewReportButton.addActionListener(e -> DesktopUtilities.viewThread(view.getURL(), report.getId()));


		removeAllActionListeners(deleteReportButton);
		deleteReportButton.addActionListener(e -> view.deleteReportButtonPressed());
	}

	@Override
	public Container getContentPane() {
		return contentPane;
	}
}