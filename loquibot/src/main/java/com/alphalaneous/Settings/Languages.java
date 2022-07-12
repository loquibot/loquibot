package com.alphalaneous.Settings;

import com.alphalaneous.Swing.Components.SettingsPage;
import com.alphalaneous.Utils.Language;

import javax.swing.*;


public class Languages {

	public static JPanel createPanel() {

		Language.switchLanguage(SettingsHandler.getSettings("language").asString());
		SettingsPage settingsPage = new SettingsPage("$LANGUAGE_SETTINGS$");

		settingsPage.addRadioOption("$LANGUAGE$", "", new String[]{"〈en_us〉", "〈es_es〉","〈pt_br〉","〈fr_fr〉"}, "language", "en_us", () -> {
			Language.switchLanguage(SettingsHandler.getSettings("language").asString());
		});

		return settingsPage;
	}


}
