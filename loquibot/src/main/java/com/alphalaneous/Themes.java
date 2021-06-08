package com.alphalaneous;

import com.alphalaneous.Components.FancyTextArea;
import com.alphalaneous.Components.FancyTooltip;
import com.alphalaneous.Panels.CommentsPanel;
import com.alphalaneous.Panels.InfoPanel;
import com.alphalaneous.Panels.LevelsPanel;
import com.alphalaneous.Panels.SettingsTitle;
import com.alphalaneous.SettingsPanels.*;
import com.alphalaneous.ThemedComponents.ThemedCheckbox;
import com.alphalaneous.ThemedComponents.ThemedIconCheckbox;
import com.alphalaneous.Windows.CommandEditor;
import com.alphalaneous.Windows.SettingsWindow;
import com.alphalaneous.Windows.Window;

import static com.alphalaneous.Defaults.defaultUI;
import static com.alphalaneous.Defaults.settingsButtonUI;

public class Themes {

	public static void refreshUI() {

		Window.refreshUI();
		FancyTooltip.refreshAll();
		SettingsTitle.refreshAll();
		ThemedIconCheckbox.refreshAll();
		ThemedCheckbox.refreshAll();
		FancyTextArea.refreshAll();


		defaultUI.setBackground(Defaults.MAIN);
		defaultUI.setHover(Defaults.BUTTON_HOVER);
		defaultUI.setSelect(Defaults.SELECT);
		settingsButtonUI.setBackground(Defaults.BUTTON);
		settingsButtonUI.setHover(Defaults.BUTTON_HOVER);
		settingsButtonUI.setSelect(Defaults.SELECT);
		CommentsPanel.refreshUI();
		SettingsWindow.refreshUI();
		LevelsPanel.refreshUI();
		InfoPanel.refreshUI();
		InfoPanel.refreshInfo();
		AccountSettings.refreshUI();
		PersonalizationSettings.refreshUI();
		ChatbotSettings.refreshUI();
		BlockedSettings.refreshUI();
		BlockedUserSettings.refreshUI();
		BlockedCreatorSettings.refreshUI();
		RequestsSettings.refreshUI();
		CommandSettings.refreshUI();
		ChannelPointSettings.refreshUI();
		ShortcutSettings.refreshUI();
		FiltersSettings.refreshUI();
		ChaosModeSettings.refreshUI();
		CommandEditor.refreshUI();
		RequestsLog.refreshUI();
		OutputSettings.refreshUI();
	}
}

