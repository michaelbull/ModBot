package org.modbot.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Contains VBulletin related utilities.
 * @author Michael Bull
 */
public final class VBulletinUtilities {
	public static String md5Hex(String string) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		return md5Hex(string.getBytes("UTF-8"));
	}

	private static String md5Hex(byte[] data) throws NoSuchAlgorithmException {
		MessageDigest mdEnc = MessageDigest.getInstance("MD5");
		mdEnc.update(data, 0, data.length);
		return new BigInteger(1, mdEnc.digest()).toString(16);
	}
}