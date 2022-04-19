package com.alphalaneous.ChatbotTab;

import com.alphalaneous.CommandData;
import com.alphalaneous.Components.CommandConfigCheckbox;
import com.alphalaneous.Components.ListView;
import com.alphalaneous.LoadCommands;
import com.alphalaneous.Settings;
import com.alphalaneous.Utilities;
import com.alphalaneous.Windows.Window;
import org.apache.commons.text.StringEscapeUtils;

import javax.swing.*;
import java.util.ArrayList;

public class DefaultCommands {
	private static ListView listView;
	public static JPanel createPanel() {
		listView = new ListView("$DEFAULT_COMMANDS_SETTINGS$");



		return listView;

	}

	public static void loadCommands(){
		listView.clearElements();

		ArrayList<CommandData> commands = new ArrayList<>(LoadCommands.getDefaultCommands());
		if(Settings.getSettings("gdMode").asBoolean() && Window.getWindow().isVisible()) commands.addAll(LoadCommands.getGeometryDashCommands());

		ArrayList<CommandData> alphabetizedCommands = Utilities.alphabetizeCommandData(commands);

		for(CommandData commandData : alphabetizedCommands){
			CommandConfigCheckbox commandConfigCheckbox = new CommandConfigCheckbox(commandData);
			commandConfigCheckbox.resize(Window.getWindow().getWidth());
			listView.addElement(commandConfigCheckbox);
		}
	}

}
