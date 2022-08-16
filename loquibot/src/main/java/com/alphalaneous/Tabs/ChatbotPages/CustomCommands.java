package com.alphalaneous.Tabs.ChatbotPages;

import com.alphalaneous.Interactive.Commands.LoadCommands;
import com.alphalaneous.Swing.Components.*;
import com.alphalaneous.Interactive.Commands.CommandData;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Utils.Utilities;
import com.alphalaneous.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class CustomCommands {

	private static ListView listView;

	public static JPanel createPanel() {
		listView = new ListView("$CUSTOM_COMMANDS_SETTINGS$");
		listView.addButton("\uF0D1", () -> CommandConfigCheckbox.openCommandSettings(true));

		return listView;

	}

	public static void loadCommands(){
		listView.clearElements();

		ArrayList<CommandData> commands = new ArrayList<>(LoadCommands.getCustomCommands());

		ArrayList<CommandData> alphabetizedCommands = Utilities.alphabetizeCommandData(commands);

		for(CommandData commandData : alphabetizedCommands){
			CommandConfigCheckbox commandConfigCheckbox = new CommandConfigCheckbox(commandData);
			commandConfigCheckbox.resize(Window.getWindow().getWidth());
			listView.addElement(commandConfigCheckbox);
		}
		listView.updateUI();
	}

	public static HashMap<ListButton, Boolean> listButtons = new HashMap<>();

	public static void refreshListButtons(){
		for(Map.Entry<ListButton, Boolean> entry : listButtons.entrySet()){
			if(entry.getValue()){
				entry.getKey().setForeground(Color.RED);
			}
		}
	}

	public static CurvedButton createButton(String text, boolean existingDefault){


		ListButton button = new ListButton(text, 164);
		if(existingDefault) {
			button.setForeground(Color.RED);
		}

		listButtons.put(button, existingDefault);
		return button;
	}
	public static class LegacyCommandsLabel{


		private static final LangLabel label = new LangLabel("Legacy Commands: ");
		private static final JPanel panel = new JPanel();

		public static JPanel create(){
			panel.setBackground(Defaults.COLOR3);
			panel.setLayout(null);
			label.setForeground(Defaults.FOREGROUND_A);
			label.setFont(Defaults.MAIN_FONT.deriveFont(14f));
			label.setBounds(20,5, 500,50);
			panel.add(label);
			return panel;
		}
		public static void resize(int width){
			label.setBounds(20,5, width-300,50);
			panel.setPreferredSize(new Dimension(width-300, 60));
		}
		public static void refreshUI(){
			label.setForeground(Defaults.FOREGROUND_A);
			panel.setBackground(Defaults.COLOR3);
		}

	}
}
