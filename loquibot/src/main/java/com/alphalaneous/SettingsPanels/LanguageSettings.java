package com.alphalaneous.SettingsPanels;

import com.alphalaneous.Components.FancyTextArea;
import com.alphalaneous.Components.SettingsPage;
import com.alphalaneous.Defaults;
import com.alphalaneous.Language;
import com.alphalaneous.Settings;

import javax.swing.*;
import java.awt.*;


public class LanguageSettings {

	public static JPanel createPanel() {

		Language.switchLanguage(Settings.getSettings("language").asString());
		SettingsPage settingsPage = new SettingsPage("$LANGUAGE_SETTINGS$");

		settingsPage.addRadioOption("$LANGUAGE$", "", new String[]{"〈en_us〉", /*"%es_es%",*/"〈pt_br〉","〈fr_fr〉"}, "language", "en_us", () -> {
			Language.switchLanguage(Settings.getSettings("language").asString());
		});

		return settingsPage;
	}


}
