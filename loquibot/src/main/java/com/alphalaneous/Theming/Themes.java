package com.alphalaneous.Theming;

import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Settings.SettingData;
import com.alphalaneous.Tabs.ChatbotPages.CustomCommands;
import com.alphalaneous.Swing.Components.*;
import com.alphalaneous.Swing.Components.LevelDetailsPanel;
import com.alphalaneous.Swing.Components.SettingsTitle;
import com.alphalaneous.Swing.Components.VideoDetailsPanel;
import com.alphalaneous.Tabs.ChatbotTab;
import com.alphalaneous.Tabs.MediaShareTab;
import com.alphalaneous.Swing.ThemedComponents.ThemedCheckbox;
import com.alphalaneous.Swing.ThemedComponents.ThemedConfigCheckbox;
import com.alphalaneous.Swing.ThemedComponents.ThemedIconCheckbox;
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

import static com.alphalaneous.Utils.Defaults.defaultUI;
import static com.alphalaneous.Utils.Defaults.settingsButtonUI;

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
		MediaShareTab.refreshUI();
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
		VideoDetailsPanel.refreshUI();
		SettingsTab.refreshUI();
		ChatbotTab.refreshUI();
		CommandEditor.refreshUI();
	}

	public static void loadTheme() {
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
			return new SettingData(themeSettings.get("is_light").replace("\\n", "\n")).asBoolean();
		}
		return new SettingData(true).asBoolean();
	}

	public static Color getThemeSetting(String key) {
		if (themeSettings.containsKey(key)) {
			return new SettingData(themeSettings.get(key).replace("\\n", "\n")).asColor();
		}
		else{
			Color c = Defaults.defaultColors.get(key);
			if(c != null) return c;
			else return Color.MAGENTA;
		}
	}

	public static Color getThemeSettingNullable(String key) {
		if (themeSettings.containsKey(key)) {
			return new SettingData(themeSettings.get(key).replace("\\n", "\n")).asColor();
		}
		else{
			return Defaults.defaultColors.get(key);
		}
	}

	public static void writeTheme(String key, String setting) {
		themeSettings.put(key, setting.replace("\n", "\\n"));
	}

	public static void saveTheme() {
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

