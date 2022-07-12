package com.alphalaneous.Tabs.ChatbotPages;

import com.alphalaneous.Interactive.Commands.LoadCommands;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Swing.Components.*;
import com.alphalaneous.Interactive.Commands.CommandData;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Utils.Utilities;
import com.alphalaneous.Windows.CommandEditor;
import com.alphalaneous.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class CustomCommands {

	private static ListView listView;
	private static final String[] gdCommands = {"!gdping", "!block", "!blockuser", "!unblock", "!unblockuser", "!clear", "!info", "!move", "!next", "!position", "!queue", "!remove", "!request", "!song", "!toggle", "!top", "!wronglevel"};


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
		loadLegacyCommands();
		listView.updateUI();
	}

	public static HashMap<ListButton, Boolean> listButtons = new HashMap<>();

	private static final JPanel legacyCommandsLabel = LegacyCommandsLabel.create();
	public static void loadLegacyCommands(){
		listButtons.clear();
		try {
			HashMap<String, ButtonInfo> existingCommands = new HashMap<>();
			try {
				Path comPath = Paths.get(Defaults.saveDirectory + "/loquibot/commands/");
				if (Files.exists(comPath)) {
					new Thread(() -> JOptionPane.showMessageDialog(Window.getWindow(), "Legacy Commands are being removed in a future release, please \nbe sure to recreate them with the new command system ASAP.", "Legacy Commands", JOptionPane.WARNING_MESSAGE)).start();

					Stream<Path> walk1 = Files.walk(comPath, 1);
					for (Iterator<Path> it = walk1.iterator(); it.hasNext(); ) {
						Path path = it.next();
						String[] file = path.toString().split("\\\\");
						String fileName = file[file.length - 1];
						if (fileName.endsWith(".js")) {
							existingCommands.put(fileName.substring(0, fileName.length() - 3), new ButtonInfo(path));
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}



			TreeMap<String, ButtonInfo> sorted = new TreeMap<>(existingCommands);

			if(sorted.size() != 0){
				listView.addElement(legacyCommandsLabel);
			}

			for (Map.Entry<String, ButtonInfo> entry : sorted.entrySet()) {

				boolean exists = false;
				String key = entry.getKey();
				if (!SettingsHandler.getSettings("gdMode").asBoolean() && Window.getWindow().isVisible()) {
					for (String command : gdCommands) {
						if (key.equalsIgnoreCase(command)) {
							exists = true;
							break;
						}
					}
				}
				if (!exists) {
					String defaultCommandPrefix = "!";
					String geometryDashCommandPrefix = "!";

					if(SettingsHandler.getSettings("defaultCommandPrefix").exists()) defaultCommandPrefix = SettingsHandler.getSettings("defaultCommandPrefix").asString();
					if(SettingsHandler.getSettings("geometryDashCommandPrefix").exists()) geometryDashCommandPrefix = SettingsHandler.getSettings("geometryDashCommandPrefix").asString();

					boolean existingDefault = false;
					for(CommandData commandData : LoadCommands.getDefaultCommands()) {
						if ((defaultCommandPrefix + commandData.getCommand()).equalsIgnoreCase(key)){
							existingDefault = true;
							break;
						}
					}
					for(CommandData commandData : LoadCommands.getGeometryDashCommands()) {
						if ((geometryDashCommandPrefix + commandData.getCommand()).equalsIgnoreCase(key)){
							existingDefault = true;
							break;
						}
					}
					listView.addElement(createButton(key, existingDefault));
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void refreshListButtons(){
		for(Map.Entry<ListButton, Boolean> entry : listButtons.entrySet()){
			if(entry.getValue()){
				entry.getKey().setForeground(Color.RED);
				entry.getKey().refresh();
			}
		}
	}

	public static CurvedButton createButton(String text, boolean existingDefault){


		ListButton button = new ListButton(text, 164);
		if(existingDefault) {
			button.setForeground(Color.RED);
			button.refresh();
		}

		button.addActionListener(e -> new Thread(() -> CommandEditor.showEditor("commands", text, false)).start());
		listButtons.put(button, existingDefault);
		return button;
	}
	public static class ButtonInfo {

		public Path path;

		ButtonInfo(Path path) {
			this.path = path;
		}

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
