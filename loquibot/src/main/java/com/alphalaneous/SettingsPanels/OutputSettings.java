package com.alphalaneous.SettingsPanels;

import com.alphalaneous.Components.FancyTextArea;
import com.alphalaneous.Components.LangLabel;
import com.alphalaneous.Defaults;
import com.alphalaneous.Settings;
import com.alphalaneous.ThemedComponents.ThemedCheckbox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OutputSettings {
	public static String outputString = "Currently playing %levelName% (%levelID%) by %levelAuthor%!";
	public static String noLevelString = "There are no levels in the queue!";
	private static String fileLocation = Paths.get(Defaults.saveDirectory + "\\GDBoard").toString();
	private static final FancyTextArea outputStringInput = new FancyTextArea(false, false);
	private static final FancyTextArea noLevelsStringInput = new FancyTextArea(false, false);
	private static final FancyTextArea fileLocationInput = new FancyTextArea(false, false);
	private static final JPanel panel = new JPanel();

	public static JPanel createPanel() {

		panel.setDoubleBuffered(true);
		panel.setBounds(0, 0, 542, 622);
		panel.setBackground(Defaults.SUB_MAIN);
		panel.setLayout(null);
		LangLabel outputText = new LangLabel("$OUTPUTS_TEXT$");
		outputText.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		outputText.setBounds(25, 16, outputText.getPreferredSize().width + 5, outputText.getPreferredSize().height + 5);
		outputText.setForeground(Defaults.FOREGROUND);
		outputStringInput.setBounds(25, 45, 490, 200);
		outputStringInput.setLineWrap(true);
		outputStringInput.setWrapStyleWord(true);
		outputStringInput.setText(outputString);
		outputStringInput.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (!outputStringInput.getText().equalsIgnoreCase("")) {
					outputString = outputStringInput.getText();
				} else {
					outputString = "Currently playing %levelName% (%levelID%) by %levelAuthor%!";
				}
				Settings.writeSettings("outputString", "%s%" + outputString.replace("\n", "%n%").replaceAll("%s%", ""));
			}
		});

		LangLabel noLevelsText = new LangLabel("$NO_LEVELS_TEXT$");
		noLevelsText.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		noLevelsText.setBounds(25, 261, noLevelsText.getPreferredSize().width + 5, noLevelsText.getPreferredSize().height + 5);
		noLevelsText.setForeground(Defaults.FOREGROUND);
		noLevelsStringInput.setBounds(25, 290, 490, 200);
		noLevelsStringInput.setLineWrap(true);
		noLevelsStringInput.setWrapStyleWord(true);
		noLevelsStringInput.setText(noLevelString);
		noLevelsStringInput.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (!noLevelsStringInput.getText().equalsIgnoreCase("")) {
					noLevelString = noLevelsStringInput.getText();
				} else {
					noLevelString = "There are no levels in the queue!";
				}
				Settings.writeSettings("noLevelsString", "%s%" + noLevelString.replace("\n", "%n%").replaceAll("%s%", ""));
			}
		});

		LangLabel fileLocationText = new LangLabel("$FILE_LOCATION$");
		fileLocationText.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		fileLocationText.setBounds(25, 506, fileLocationText.getPreferredSize().width + 5, fileLocationText.getPreferredSize().height + 5);
		fileLocationText.setForeground(Defaults.FOREGROUND);
		fileLocationInput.setBounds(25, 535, 490, 64);
		fileLocationInput.setLineWrap(true);
		fileLocationInput.getDocument().putProperty("filterNewlines", Boolean.TRUE);
		fileLocationInput.setText(fileLocation);
		fileLocationInput.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				fileLocation = fileLocationInput.getText();
				Settings.writeSettings("outputFileLocation", fileLocation.replace("\\\\", "\\"));
			}
		});

		panel.add(outputText);
		panel.add(outputStringInput);
		panel.add(noLevelsText);
		panel.add(noLevelsStringInput);
		panel.add(fileLocationText);
		panel.add(fileLocationInput);
		return panel;

	}

	public static void loadSettings() {
		if (!Settings.getSettings("noLevelsString").asString().equalsIgnoreCase("")) {
			noLevelString = Settings.getSettings("noLevelsString").asString().replace("%n%", "\n");
			noLevelsStringInput.setText(noLevelString.replaceAll("%s%", ""));
		}
		if (!Settings.getSettings("outputString").asString().equalsIgnoreCase("")) {
			outputString = Settings.getSettings("outputString").asString().replace("%n%", "\n");
			outputStringInput.setText(outputString.replaceAll("%s%", ""));
		}
		if (!Settings.getSettings("outputFileLocation").asString().equalsIgnoreCase("")) {
			fileLocation = Settings.getSettings("outputFileLocation").asString().replace("\\", "\\\\");
			fileLocationInput.setText(fileLocation.replace("\\\\", "\\"));
		}
	}

	public static void setOutputStringFile(String text) {
		Path file = Paths.get(fileLocation + "\\output.txt");
		if (fileLocation.equalsIgnoreCase("")) {
			file = Paths.get(Defaults.saveDirectory + "\\GDBoard\\output.txt");
		}
		try {
			if (!Files.exists(file)) {
				Files.createFile(file);
			}
			Files.write(file, text.getBytes());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static void refreshUI() {

		panel.setBackground(Defaults.SUB_MAIN);
		for (Component component : panel.getComponents()) {
			if (component instanceof JButton) {
				for (Component component2 : ((JButton) component).getComponents()) {
					if (component2 instanceof JLabel) {
						component2.setForeground(Defaults.FOREGROUND);
					}
				}
				component.setBackground(Defaults.MAIN);
			}
			if (component instanceof JLabel) {
				component.setForeground(Defaults.FOREGROUND);

			}
		}
	}
}