package com.alphalaneous;

import com.alphalaneous.ChatbotTab.CustomCommands;
import com.alphalaneous.Components.*;
import com.alphalaneous.Panels.LevelDetailsPanel;
import com.alphalaneous.Panels.SettingsTitle;
import com.alphalaneous.Tabs.ChatbotTab;
import com.alphalaneous.ThemedComponents.ThemedCheckbox;
import com.alphalaneous.ThemedComponents.ThemedConfigCheckbox;
import com.alphalaneous.ThemedComponents.ThemedIconCheckbox;
import com.alphalaneous.Windows.CommandEditor;
import com.alphalaneous.Tabs.SettingsTab;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Windows.OfficerWindow;
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

import static com.alphalaneous.Defaults.defaultUI;
import static com.alphalaneous.Defaults.settingsButtonUI;

public class Themes {

	private static final HashMap<String, String> themeSettings = new HashMap<>();


	public static void refreshUI() {

		defaultUI.setBackground(Defaults.COLOR);
		defaultUI.setHover(Defaults.COLOR5);
		defaultUI.setSelect(Defaults.COLOR4);
		settingsButtonUI.setBackground(Defaults.COLOR2);
		settingsButtonUI.setHover(Defaults.COLOR5);
		settingsButtonUI.setSelect(Defaults.COLOR4);

		Window.refreshUI();

		ThemedColor.setAllThemeColors();

		RequestsTab.refreshUI();
		FancyTooltip.refreshAll();
		SettingsTitle.refreshAll();
		ThemedConfigCheckbox.refreshAll();
		ThemedIconCheckbox.refreshAll();
		ThemedCheckbox.refreshAll();
		FancyTextArea.refreshAll();
		FancyPasswordField.refreshAll();
		RoundedJButton.refreshAll();
		CommandListElement.refreshAll();
		SettingsPage.refreshAll();
		SettingsComponent.refreshAll();
		ListView.refreshAll();
		OfficerWindow.refreshUI();
		CustomCommands.LegacyCommandsLabel.refreshUI();
		CustomCommands.refreshListButtons();
		LevelDetailsPanel.refreshUI();
		SettingsTab.refreshUI();
		ChatbotTab.refreshUI();
		CommandEditor.refreshUI();
	}

	static void loadTheme() {
		Path path = Paths.get(Defaults.saveDirectory + "/loquibot/theme.properties");
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
					themeSettings.put(line.split("=", 2)[0].trim(), line.split("=", 2)[1].trim());
				}
			}
			sc.close();
		}

	}

	public static boolean getIsLight(){
		if (themeSettings.containsKey("is_light")) {
			return new Setting(themeSettings.get("is_light").replace("\\n", "\n")).asBoolean();
		}
		return new Setting(true).asBoolean();
	}

	public static Color getThemeSetting(String key) {
		if (themeSettings.containsKey(key)) {
			return new Setting(themeSettings.get(key).replace("\\n", "\n")).asColor();
		}
		else{
			Color c = Defaults.defaultColors.get(key);
			if(c != null) return c;
			else return Color.MAGENTA;
		}
	}

	public static Color getThemeSettingNullable(String key) {
		if (themeSettings.containsKey(key)) {
			return new Setting(themeSettings.get(key).replace("\\n", "\n")).asColor();
		}
		else{
			return Defaults.defaultColors.get(key);
		}
	}

	public static void writeTheme(String key, String setting) {
		themeSettings.put(key, setting.replace("\n", "\\n"));
	}

	static void saveTheme() {
		if(themeSettings.size() > 0) {
			Path file = Paths.get(Defaults.saveDirectory + "/loquibot/theme.properties");

			try {
				if (!Files.exists(file)) {
					Files.createFile(file);
				}
				Iterator it = themeSettings.entrySet().iterator();
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
	}
}

