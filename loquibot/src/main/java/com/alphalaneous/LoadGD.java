package com.alphalaneous;

import com.alphalaneous.SettingsPanels.AccountSettings;

import java.util.Base64;

public class LoadGD {

	public static String username = "";
	public static volatile boolean isAuth = false;
	static boolean loaded = false;
	private static String password = "";

	static void load() {
		new Thread(() -> {
			if (Settings.getSettings("GDLogon").asBoolean()) {
				try {
					username = Settings.getSettings("GDUsername").asString();
					password = xor(new String(Base64.getDecoder().decode(Settings.getSettings("p").asString().getBytes())));
					GDAPI.login(username,password);
					isAuth = true;
					AccountSettings.refreshGD(username);
				} catch (Exception e) {
					Settings.writeSettings("GDLogon", "false");
					isAuth = false;
				}
			} else {
				Settings.writeSettings("GDLogon", "false");
				isAuth = false;
			}
			loaded = true;
		}).start();
	}

	private static String xor(String inputString) {

		StringBuilder outputString = new StringBuilder();

		int len = inputString.length();

		for (int i = 0; i < len; i++) {
			outputString.append((char) (inputString.charAt(i) ^ 15));
		}
		return outputString.toString();
	}
}
