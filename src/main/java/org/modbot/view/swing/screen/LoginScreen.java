package org.modbot.view.swing.screen;

import org.modbot.model.Credentials;
import org.modbot.util.SpringUtilities;
import org.modbot.view.swing.Screen;
import org.modbot.view.swing.SwingView;
import org.modbot.view.swing.listener.PasswordDocumentListener;
import org.modbot.view.swing.listener.UsernameDocumentListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import java.awt.Container;

/**
 * Represents the login screen that requires a user's name and password in order to authenticate
 * with the VBulletin Mobile API.
 * @author Michael Bull
 */
public final class LoginScreen extends Screen {
	private final JPanel contentPane = new JPanel(new SpringLayout());
	private final JCheckBox rememberMeCheckBox = new JCheckBox("Remember me");
	private final JTextField usernameField = new JTextField(15);
	private final JPasswordField passwordField = new JPasswordField(15);

	public LoginScreen(SwingView view) {
		super(view);

		contentPane.setBorder(BorderFactory.createTitledBorder("Login"));

		JLabel usernameLabel = new JLabel("Username:");
		usernameLabel.setLabelFor(usernameField);
		contentPane.add(usernameLabel);

		usernameField.getDocument().addDocumentListener(new UsernameDocumentListener(view));
		contentPane.add(usernameField);

		JLabel passwordLabel = new JLabel("Password:");
		passwordLabel.setLabelFor(passwordField);
		contentPane.add(passwordLabel);

		passwordField.getDocument().addDocumentListener(new PasswordDocumentListener(view));
		contentPane.add(passwordField);

		rememberMeCheckBox.addItemListener(e -> view.rememberMeSelected(rememberMeCheckBox.isSelected()));
		contentPane.add(rememberMeCheckBox);

		JButton loginButton = new JButton("Login");
		loginButton.addActionListener(e -> view.loginButtonPressed());
		contentPane.add(loginButton);

		SpringUtilities.makeGrid(contentPane, 3, 2, 6, 6, 6, 6);
	}

	@Override
	public Container getContentPane() {
		return contentPane;
	}

	public void setCredentials(Credentials credentials) {
		usernameField.setText(credentials.getUsername());
		passwordField.setText(credentials.getPassword());
	}

	public void setRememberMeSelected(boolean selected) {
		rememberMeCheckBox.setSelected(selected);
	}
}