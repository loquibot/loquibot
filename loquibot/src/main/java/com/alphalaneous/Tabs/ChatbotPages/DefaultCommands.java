package com.alphalaneous.Tabs.ChatbotPages;

import com.alphalaneous.Interactive.Commands.CommandData;
import com.alphalaneous.Swing.Components.CommandConfigCheckbox;
import com.alphalaneous.Swing.Components.ListView;
import com.alphalaneous.Interactive.Commands.LoadCommands;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Utils.Utilities;
import com.alphalaneous.Windows.Window;

import javax.swing.*;
import java.util.ArrayList;

public class DefaultCommands {
	private static ListView listView;
	public static JPanel createPanel() {
		listView = new ListView("$DEFAULT_COMMANDS_SETTINGS$");



		return listView;

	}

	public static void loadCommands(){
		if(listView != null) {
			listView.clearElements();
		}
		ArrayList<CommandData> commands = new ArrayList<>(LoadCommands.getDefaultCommands());
		if(SettingsHandler.getSettings("gdMode").asBoolean() && Window.getWindow().isVisible()) commands.addAll(LoadCommands.getGeometryDashCommands());
		commands.addAll(LoadCommands.getMediaShareCommands());

		ArrayList<CommandData> alphabetizedCommands = Utilities.alphabetizeCommandData(commands);

		for(CommandData commandData : alphabetizedCommands){
			CommandConfigCheckbox commandConfigCheckbox = new CommandConfigCheckbox(commandData);
			commandConfigCheckbox.resize(Window.getWindow().getWidth());
			if(listView != null) {
				listView.addElement(commandConfigCheckbox);
			}
		}
	}

}
