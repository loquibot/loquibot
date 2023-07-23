package com.alphalaneous.Theming;

import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Settings.SettingData;
import com.alphalaneous.Tabs.ChatbotPages.CustomCommands;
import com.alphalaneous.Swing.Components.*;
import com.alphalaneous.Swing.Components.LevelDetailsPanel;
import com.alphalaneous.Swing.Components.SettingsTitle;
import com.alphalaneous.Tabs.ChatbotTab;
import com.alphalaneous.Swing.ThemedComponents.ThemedCheckbox;
import com.alphalaneous.Swing.ThemedComponents.ThemedConfigCheckbox;
import com.alphalaneous.Swing.ThemedComponents.ThemedIconCheckbox;
import com.alphalaneous.Tabs.SettingsTab;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.Utils.Utilities;
import com.alphalaneous.Windows.OfficerWindow;
import com.alphalaneous.Windows.Window;

import java.awt.*;
import java.util.HashMap;

import static com.alphalaneous.Utils.Defaults.defaultUI;
import static com.alphalaneous.Utils.Defaults.settingsButtonUI;

public class Themes {

	private static final HashMap<String, String> themeSettings = new HashMap<>();


	public static void refreshUI() {

		try {
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
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void loadTheme() {
		try {
			Utilities.load("/loquibot/theme.properties", themeSettings);
		}
		catch (Exception e){
			System.out.println("No theme.properties");
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
		saveTheme();
	}

	public static void saveTheme() {
		if(themeSettings.size() > 0) {
			Utilities.save("/loquibot/theme.properties", themeSettings);
		}
	}
}

