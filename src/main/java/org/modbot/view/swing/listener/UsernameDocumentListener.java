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
 * Represents a {@link DocumentListener} that will listen for changes to the username text field
 * and update the {@link Model} with this new username.
 * @author Michael Bull
 */
public final class UsernameDocumentListener implements DocumentListener {
	private static final Logger logger = LoggerFactory.getLogger(UsernameDocumentListener.class);

	private final View view;

	public UsernameDocumentListener(View view) {
		this.view = view;
	}

	public void update(DocumentEvent event) {
		Document doc = event.getDocument();
		int length = doc.getLength();
		try {
			String username = doc.getText(0, length);
			view.usernameFieldChanged(username);
		} catch (BadLocationException e) {
			view.displayError(e);
			logger.info("Failed to update username:", e);
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