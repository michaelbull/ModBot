package org.modbot.view.swing;

import java.awt.Container;

/**
 * Represents a screen in the view.
 * @author Michael Bull
 */
public abstract class Screen {
	protected final SwingView view;

	protected Screen(SwingView view) {
		super();
		this.view = view;
	}

	public abstract Container getContentPane();
}