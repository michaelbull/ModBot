package org.modbot.view.swing.screen;

import org.modbot.controller.task.impl.ReportSearchTask;
import org.modbot.model.ForumThread;
import org.modbot.view.swing.Screen;
import org.modbot.view.swing.SwingView;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

/**
 * Represents the main screen that the user may interact with to start a {@link ReportSearchTask} or view
 * missed reports.
 * @author Michael Bull
 */
public final class MainScreen extends Screen {
	private final JPanel contentPane = new JPanel(new GridBagLayout());
	private final JPanel reportsPanel = new JPanel(new GridBagLayout());
	private final GridBagConstraints reportsPanelConstraints = new GridBagConstraints();
	private final JButton reportsSearchButton = new JButton("Start report search");

	public MainScreen(SwingView view) {
		super(view);

		GridBagConstraints c = new GridBagConstraints();

		c.gridy++;
		reportsSearchButton.addActionListener(e -> view.reportSearchButtonPressed());
		contentPane.add(reportsSearchButton, c);

		c.gridy++;
		JButton clearCacheButton = new JButton("Clear reports cache");
		clearCacheButton.addActionListener(e -> view.clearCacheButtonPressed());
		contentPane.add(clearCacheButton, c);

		reportsPanel.setBorder(BorderFactory.createTitledBorder("Missed Reports"));
		c.gridy++;
		contentPane.add(reportsPanel, c);
	}

	public void updateMissedReports(List<ForumThread> missedReports) {
		reportsPanel.removeAll();
		reportsPanelConstraints.gridx = 0;
		reportsPanelConstraints.gridy = 0;

		for (ForumThread thread : missedReports) {
			JButton reportButton = new JButton(thread.getTitle());
			reportButton.addActionListener(event -> view.openMissedReportButtonPressed(thread));
			reportsPanelConstraints.gridy++;
			reportsPanel.add(reportButton, reportsPanelConstraints);
		}

		reportsPanel.repaint();
		reportsPanel.revalidate();
	}

	public void setReportsSearchButtonText(String text) {
		reportsSearchButton.setText(text);
	}

	@Override
	public Container getContentPane() {
		return contentPane;
	}
}