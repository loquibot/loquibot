package com.alphalaneous.SettingsPanels;

import com.alphalaneous.BotHandler;
import com.alphalaneous.Components.*;
import com.alphalaneous.Defaults;
import com.alphalaneous.Main;
import com.alphalaneous.Windows.CommandEditor;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import static com.alphalaneous.Defaults.settingsButtonUI;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class CommandSettings {
	private static int i = 0;
	private static double height = 0;
	private static final JLabel commandLabel = new JLabel();
	private static final LangLabel sliderValue = new LangLabel("");
	private static final JPanel commandsPanel = new JPanel();
	private static final JScrollPane scrollPane = new SmoothScrollPane(commandsPanel);
	private static final JPanel panel = new JPanel();
	private static final JPanel titlePanel = new JPanel();
	private static final RoundedJButton addCommand = new RoundedJButton("\uF0D1", "$ADD_COMMAND_TOOLTIP$");
	private static final String[] gdCommands = {"!gdping", "!gd", "!kill", "!block", "!blockuser", "!unblock", "!unblockuser", "!clear", "!info", "!move", "!next", "!position", "!queue", "!remove", "!request", "!song", "!stop", "!toggle", "!top", "!wronglevel"};


	public static JPanel createPanel() {

		LangLabel label = new LangLabel("$COMMANDS_LIST$");
		label.setForeground(Defaults.FOREGROUND);
		label.setFont(Defaults.MAIN_FONT.deriveFont(24f));
		label.setBounds(25, 25, label.getPreferredSize().width + 5, label.getPreferredSize().height + 5);

		panel.add(label);

		addCommand.setBackground(Defaults.BUTTON);
		addCommand.setBounds(490, 31, 30, 30);
		addCommand.setFont(Defaults.SYMBOLS.deriveFont(18f));
		addCommand.setForeground(Defaults.FOREGROUND);
		addCommand.setUI(settingsButtonUI);
		addCommand.addActionListener(e -> CommandEditor.showEditor("commands", "", false));

		panel.add(addCommand);


		titlePanel.setBounds(0, 0, 542, 50);
		titlePanel.setLayout(null);
		titlePanel.setDoubleBuffered(true);
		titlePanel.setBackground(Defaults.TOP);


		commandLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		commandLabel.setForeground(Defaults.FOREGROUND);
		commandLabel.setBounds(50, 17, commandLabel.getPreferredSize().width + 5, commandLabel.getPreferredSize().height + 5);
		titlePanel.add(commandLabel);


		panel.setLayout(null);
		panel.setDoubleBuffered(true);
		panel.setBounds(0, 0, 542, 622);
		panel.setBackground(Defaults.SUB_MAIN);
		commandsPanel.setDoubleBuffered(true);
		commandsPanel.setBounds(0, 0, 400, 0);
		commandsPanel.setPreferredSize(new Dimension(400, 0));
		commandsPanel.setBackground(Defaults.SUB_MAIN);
		commandsPanel.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));

		commandsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 4, 4));
		scrollPane.setBounds(0, 80, 542, 542);
		scrollPane.setPreferredSize(new Dimension(542, 542));

		HashMap<String, ButtonInfo> existingCommands = new HashMap<>();
		try {
			URI uri = Main.class.getResource("/Commands/").toURI();
			Path myPath;
			if (uri.getScheme().equals("jar")) {
				myPath = BotHandler.fileSystem.getPath("/Commands/");
			} else {
				myPath = Paths.get(uri);
			}
			Stream<Path> walk = Files.walk(myPath, 1);
			Path comPath = Paths.get(Defaults.saveDirectory + "/GDBoard/commands/");
			if (Files.exists(comPath)) {
				Stream<Path> walk1 = Files.walk(comPath, 1);
				for (Iterator<Path> it = walk1.iterator(); it.hasNext(); ) {
					Path path = it.next();
					String[] file = path.toString().split("\\\\");
					String fileName = file[file.length - 1];
					if (fileName.endsWith(".js")) {
						existingCommands.put(fileName.substring(0, fileName.length() - 3), new ButtonInfo(path, false));
					}
				}
			}
			for (Iterator<Path> it = walk.iterator(); it.hasNext(); ) {
				Path path = it.next();
				String[] file;
				if (uri.getScheme().equals("jar")) {
					file = path.toString().split("/");
				} else {
					file = path.toString().split("\\\\");
				}
				String fileName = file[file.length - 1];
				if (fileName.endsWith(".js")) {
					if (!fileName.equalsIgnoreCase("!rick.js") &&
							!fileName.equalsIgnoreCase("!stoprick.js") &&
							!fileName.equalsIgnoreCase("!eval.js") &&
							!fileName.equalsIgnoreCase("!end.js") &&
							!fileName.equalsIgnoreCase("!popup.js") &&
							!fileName.equalsIgnoreCase("b!addcom.js") &&
							!fileName.equalsIgnoreCase("b!editcom.js") &&
							!fileName.equalsIgnoreCase("b!delcom.js") &&
							!fileName.equalsIgnoreCase("b!addpoint.js") &&
							!fileName.equalsIgnoreCase("b!editpoint.js") &&
							!fileName.equalsIgnoreCase("b!delpoint.js")) {
						String substring = fileName.substring(0, fileName.length() - 3);
						if (!existingCommands.containsKey(substring)) {
							existingCommands.put(substring, new ButtonInfo(path, true));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		TreeMap<String, ButtonInfo> sorted = new TreeMap<>(existingCommands);

		for (Map.Entry<String, ButtonInfo> entry : sorted.entrySet()) {
			boolean exists = false;
			String key = entry.getKey();
			ButtonInfo value = entry.getValue();
			if (!RequestsSettings.gdModeOption) {
				for (String command : gdCommands) {
					if (key.equalsIgnoreCase(command)) {
						exists = true;
						break;
					}
				}
			}
			if (!exists) {
				addButton(key, value.isDefault);
			}
		}
		panel.setBounds(0, 0, 542, 622);
		panel.add(scrollPane);
		return panel;
	}

	public static void refresh() {

		commandsPanel.removeAll();
		height = 0;
		commandsPanel.setBounds(0, 0, 542, (int) (height + 14));
		commandsPanel.setPreferredSize(new Dimension(542, (int) (height + 14)));

		HashMap<String, ButtonInfo> existingCommands = new HashMap<>();
		try {
			URI uri = Main.class.getResource("/Commands/").toURI();
			Path myPath;
			if (uri.getScheme().equals("jar")) {
				myPath = BotHandler.fileSystem.getPath("/Commands/");
			} else {
				myPath = Paths.get(uri);
			}
			Stream<Path> walk = Files.walk(myPath, 1);
			Path comPath = Paths.get(Defaults.saveDirectory + "/GDBoard/commands/");
			if (Files.exists(comPath)) {
				Stream<Path> walk1 = Files.walk(comPath, 1);
				for (Iterator<Path> it = walk1.iterator(); it.hasNext(); ) {
					Path path = it.next();
					String[] file = path.toString().split("\\\\");
					String fileName = file[file.length - 1];
					if (fileName.endsWith(".js")) {
						existingCommands.put(fileName.substring(0, fileName.length() - 3), new ButtonInfo(path, false));
					}
				}
			}
			for (Iterator<Path> it = walk.iterator(); it.hasNext(); ) {
				Path path = it.next();
				String[] file;
				if (uri.getScheme().equals("jar")) {
					file = path.toString().split("/");
				} else {
					file = path.toString().split("\\\\");
				}
				String fileName = file[file.length - 1];
				if (fileName.endsWith(".js")) {

					if (!fileName.equalsIgnoreCase("!rick.js") &&
							!fileName.equalsIgnoreCase("!stoprick.js") &&
							!fileName.equalsIgnoreCase("!eval.js") &&
							!fileName.equalsIgnoreCase("!end.js") &&
							!fileName.equalsIgnoreCase("!popup.js") &&
							!fileName.equalsIgnoreCase("b!addcom.js") &&
							!fileName.equalsIgnoreCase("b!editcom.js") &&
							!fileName.equalsIgnoreCase("b!delcom.js") &&
							!fileName.equalsIgnoreCase("b!addpoint.js") &&
							!fileName.equalsIgnoreCase("b!editpoint.js") &&
							!fileName.equalsIgnoreCase("b!delpoint.js")) {
						String substring = fileName.substring(0, fileName.length() - 3);
						if (!existingCommands.containsKey(substring)) {
							existingCommands.put(substring, new ButtonInfo(path, true));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		TreeMap<String, ButtonInfo> sorted = new TreeMap<>(existingCommands);

		for (Map.Entry<String, ButtonInfo> entry : sorted.entrySet()) {
			boolean exists = false;
			String key = entry.getKey();
			ButtonInfo value = entry.getValue();
			if (!RequestsSettings.gdModeOption) {
				for (String command : gdCommands) {
					if (key.equalsIgnoreCase(command)) {
						exists = true;
						break;
					}
				}
			}
			if (!exists) {
				addButton(key, value.isDefault);
			}
		}
	}

	public static void addButton(String command, boolean isDefault) {
		i++;
		if ((i - 1) % 3 == 0) {
			height = height + 39;

			commandsPanel.setBounds(0, 0, 542, (int) (height + 14));
			commandsPanel.setPreferredSize(new Dimension(542, (int) (height + 14)));
		}
		CurvedButton button = new CurvedButton(command);
		button.setBackground(Defaults.BUTTON);
		button.setUI(settingsButtonUI);
		if (isDefault) {
			button.setForeground(Defaults.FOREGROUND2);
		} else {
			button.setForeground(Defaults.FOREGROUND);
		}
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		button.setPreferredSize(new Dimension(170, 35));

		button.addActionListener(e -> CommandEditor.showEditor("commands", command, isDefault));

		button.refresh();
		commandsPanel.add(button);
	}
	public static void resizeHeight(int height){

		height -= 38;

		panel.setBounds(panel.getX(), panel.getY(), panel.getWidth(), height);

		scrollPane.setBounds(scrollPane.getX(), scrollPane.getY(), scrollPane.getWidth(), height-80);
		scrollPane.setPreferredSize(new Dimension(scrollPane.getWidth(), height-80));
		scrollPane.updateUI();

	}
	public static void refreshUI() {
		panel.setBackground(Defaults.TOP);
		titlePanel.setBackground(Defaults.TOP);
		commandLabel.setForeground(Defaults.FOREGROUND);
		sliderValue.setForeground(Defaults.FOREGROUND);
		commandsPanel.setBackground(Defaults.SUB_MAIN);
		scrollPane.setBackground(Defaults.SUB_MAIN);
		scrollPane.getVerticalScrollBar().setUI(new ScrollbarUI());

		for (Component component : commandsPanel.getComponents()) {
			if (component instanceof CurvedButton) {

				if (component.getForeground().equals(Defaults.FOREGROUND2)) {
					component.setForeground(Defaults.FOREGROUND2);
				} else {
					component.setForeground(Defaults.FOREGROUND);
				}
				component.setBackground(Defaults.BUTTON);
				((CurvedButton) component).refresh();
			}
			if (component instanceof JLabel) {
				component.setForeground(Defaults.FOREGROUND);

			}
		}
		for (Component component : panel.getComponents()) {
			if (component instanceof JButton) {
				component.setForeground(Defaults.FOREGROUND);
				component.setBackground(Defaults.BUTTON);
			}
			if (component instanceof JLabel) {
				component.setForeground(Defaults.FOREGROUND);
			}
		}
		refresh();
	}

	public static class ButtonInfo {

		public Path path;
		boolean isDefault;

		ButtonInfo(Path path, boolean isDefault) {
			this.path = path;
			this.isDefault = isDefault;
		}

	}
}
