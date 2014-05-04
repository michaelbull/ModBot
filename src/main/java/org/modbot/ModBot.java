package org.modbot;

import org.modbot.controller.Controller;
import org.modbot.model.Credentials;
import org.modbot.model.Model;
import org.modbot.model.io.CredentialsCodec;
import org.modbot.view.swing.SwingView;
import org.modbot.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

/**
 * The initial entry point for the ModBot application.
 * @author Michael Bull
 */
public final class ModBot {
	public static final int MAJOR_VERSION = 5;
	public static final int MINOR_VERSION = 0;

	private static final Logger logger = LoggerFactory.getLogger(ModBot.class);

	public static void main(String[] args) {
		try {
			logger.info("Starting ModBot v" + MAJOR_VERSION + "." + MINOR_VERSION + "...");

			String url = args[0];
			String apiKey = args[1];
			ModBot modBot = new ModBot(url, apiKey);
			modBot.start();
		} catch (Throwable t) {
			logger.error("Failed to start ModBot:", t);
		}
	}

	private final Model model;
	private final Controller controller;

	public ModBot(String url, String apiKey) {
		View view = new SwingView(url);
		this.model = new Model();
		this.model.addListener(view);
		this.controller = new Controller(this.model, url, apiKey);
		view.addListener(this.controller);
	}

	public void start() throws IOException, NoSuchAlgorithmException {
		logger.info("Started ModBot successfully.");
		controller.init();

		try {
			Credentials loadedCredentials = CredentialsCodec.read();
			model.setLoadedCredentials(loadedCredentials);
		} catch (GeneralSecurityException e) {
			logger.warn("Failed to load existing saved credentials:", e);
		}
	}
}