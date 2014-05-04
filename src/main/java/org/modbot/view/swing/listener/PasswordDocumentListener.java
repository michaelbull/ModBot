package org.modbot.view.swing.listener;

import org.modbot.model.Model;
import org.modbot.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * Represents a {@link DocumentListener} that will listen for changes to the password text field
 * and update the {@link Model} with this new password.
 * @author Michael Bull
 */
public final class PasswordDocumentListener implements DocumentListener {
	private static final Logger logger = LoggerFactory.getLogger(PasswordDocumentListener.class);

	private final View view;

	public PasswordDocumentListener(View view) {
		this.view = view;
	}

	public void update(DocumentEvent event) {
		Document doc = event.getDocument();
		int length = doc.getLength();
		try {
			String password = doc.getText(0, length);
			view.passwordFieldChanged(password);
		} catch (BadLocationException e) {
			view.displayError(e);
			logger.info("Failed to update password:", e);
		}
	}

	@Override
	public void insertUpdate(DocumentEvent event) {
		update(event);
	}

	@Override
	public void removeUpdate(DocumentEvent event) {
		update(event);
	}

	@Override
	public void changedUpdate(DocumentEvent event) {
		update(event);
	}
}