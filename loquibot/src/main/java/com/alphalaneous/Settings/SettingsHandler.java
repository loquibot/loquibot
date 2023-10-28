package com.alphalaneous.Settings;

import com.alphalaneous.Main;
import com.alphalaneous.Utils.Utilities;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.util.HashMap;

public class SettingsHandler {

	private static final HashMap<String, String> settings = new HashMap<>();

	public static void saveSettings() {
		Utilities.save("/loquibot/config.properties", settings);
	}

	public static void writeSettings(String key, String setting) {
		if(key != null && !key.equalsIgnoreCase("null") && !key.trim().equalsIgnoreCase("")) {
			settings.put(key, setting.replace("\n", "\\n"));
			saveSettings();
			if(!key.equalsIgnoreCase("oauth")
					&& !key.equalsIgnoreCase("window")
					&& !key.equalsIgnoreCase("windowState")
					&& !key.equalsIgnoreCase("windowSize")) {
				Main.logger.log(Level.getLevel("SETTINGS"), "WRITE - " + key + ": " + setting);
			}
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
			Main.logger.error("No config.properties");
		}


		settings.forEach((k, v) -> {
			if(!k.equalsIgnoreCase("oauth")) {
				String setting = v;
				if(setting.trim().isEmpty()){
					setting = "UNASSIGNED";
				}
				Main.logger.log(Level.getLevel("SETTINGS"), "LOAD - " + k + ": " + setting);
			}
		});
	}
}
