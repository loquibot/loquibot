package com.alphalaneous;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Utilities.Utilities;

import java.io.IOException;
import java.util.HashMap;

public class SettingsHandler {

	private static final HashMap<String, String> settings = new HashMap<>();

	public static void saveSettings() {
		Utilities.save("config.properties", settings);
	}

	public static void writeSettings(String key, String setting) {
		if(key != null && !key.equalsIgnoreCase("null")) {
			settings.put(key, setting.replace("\n", "\\n"));
			saveSettings();
		}

	}

	public static SettingData getSettings(String key) {
		if (settings.containsKey(key)) {
			return new SettingData(settings.get(key).replace("\\n", "\n"));
		}
		return new SettingData(true);
	}

	@OnLoad
	public static void loadSettings() {
		try {
			Utilities.load("config.properties", settings);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
