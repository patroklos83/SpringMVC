package com.patroclos.utils;

import java.util.Base64;

public class Base64Util {

	public static String encode(String text) {
		var textBase64 = Base64.getEncoder().encodeToString(text.getBytes());
		return textBase64;
	}
	
}
