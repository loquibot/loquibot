package com.alphalaneous;

import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Windows.Window;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class Settings {

	private static HashMap<String, String> settings = new HashMap<>();
	private static Point windowWLoc = new Point(0, 0);

	public static void setWindowSettings(String key, String setting) {
		if (key.equalsIgnoreCase("Window")) {
			int x = Integer.parseInt(setting.split(",")[0]);
			int y = Integer.parseInt(setting.split(",")[1]);
			windowWLoc = new Point(x, y);
		}
	}

	static void writeLocation() {
		writeSettings("window", windowWLoc.x + "," + windowWLoc.y);
	}

	@SuppressWarnings("rawtypes")
	static void saveSettings() {
		Path file = Paths.get(Defaults.saveDirectory + "/GDBoard/config.properties");

		try {
			if (!Files.exists(file)) {
				Files.createFile(file);
			}
			Iterator it = settings.entrySet().iterator();
			StringBuilder pairs = new StringBuilder();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				pairs.append(pair.getKey()).append(" = ").append(pair.getValue()).append("\n");
				it.remove();
			}
			if (!Files.exists(file)) {
				Files.createFile(file);
			}
			Files.write(
					file,
					pairs.toString().getBytes());
		} catch (IOException e1) {
			DialogBox.showDialogBox("Error!", e1.toString(), "There was an error writing to the file!", new String[]{"OK"});

		}
	}

	public static void writeSettings(String key, String setting) {
		settings.put(key, setting);
	}

	public static Setting getSettings(String key) {
		if (settings.containsKey(key)) {
			return new Setting(settings.get(key));
		}
		return new Setting(true);
	}

	static void loadSettings() {
		Path path = Paths.get(Defaults.saveDirectory + "/GDBoard/config.properties");
		if (Files.exists(path)) {
			Scanner sc = null;
			try {
				sc = new Scanner(path.toFile());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			assert sc != null;
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (line.contains("=")) {
					settings.put(line.split("=", 2)[0].trim(), line.split("=", 2)[1].trim());
				}
			}
			sc.close();
		}

	}
}
