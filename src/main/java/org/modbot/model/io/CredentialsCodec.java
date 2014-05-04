package org.modbot.model.io;

import org.modbot.model.Credentials;
import org.modbot.util.PasswordUtilities;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;

/**
 * A codec for encoding/decoding {@link Credentials}.
 * @author Michael Bull
 */
public final class CredentialsCodec {
	public static final Path PATH = Paths.get(System.getProperty("user.home") + "/.mb5c");

	public static Credentials read() throws IOException, GeneralSecurityException {
		try (DataInputStream in = new DataInputStream(Files.newInputStream(PATH))) {
			String username = PasswordUtilities.decrypt(in.readUTF());
			String password = PasswordUtilities.decrypt(in.readUTF());
			return new Credentials(username, password);
		}
	}

	public static void write(Credentials credentials) throws GeneralSecurityException, IOException {
		try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(PATH))) {
			out.writeUTF(PasswordUtilities.encrypt(credentials.getUsername()));
			out.writeUTF(PasswordUtilities.encrypt(credentials.getPassword()));
		}
	}

	public static boolean delete() throws IOException {
		return Files.deleteIfExists(PATH);
	}
}