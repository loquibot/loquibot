package com.alphalaneous.Settings;

import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Utils.Utilities;
import com.alphalaneous.Windows.DialogBox;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class SettingsHandler {

	private static final HashMap<String, String> settings = new HashMap<>();

	public static void saveSettings() {
		Utilities.save("/loquibot/config.properties", settings);
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

	public static void loadSettings() {
		try {
			Utilities.load("/loquibot/config.properties", settings);
		} catch (IOException e) {
			System.out.println("No config.properties");
		}
	}
}
