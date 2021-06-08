package com.alphalaneous.SettingsPanels;

import com.alphalaneous.Components.*;
import com.alphalaneous.Defaults;
import com.alphalaneous.ThemedComponents.ThemedCheckbox;
import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Windows.SettingsWindow;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.Scanner;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class BlockedCreatorSettings {
	private static final JPanel blockedSettingsPanel = new JPanel();
	private static final JPanel blockedListPanel = new JPanel();
	private static final JScrollPane scrollPane = new SmoothScrollPane(blockedListPanel);
	private static final FancyTextArea blockedInput = new FancyTextArea(false, false);
	private static final RoundedJButton addID = new RoundedJButton("\uF0D1", "$ADD_CREATOR_TOOLTIP$");
	private static int i = 0;
	private static double height = 0;

	public static JPanel createPanel() {

		blockedSettingsPanel.setBackground(Defaults.TOP);
		blockedSettingsPanel.setLayout(null);

		LangLabel label = new LangLabel("$BLOCKED_CREATORS$");
		label.setForeground(Defaults.FOREGROUND);
		label.setFont(Defaults.MAIN_FONT.deriveFont(24f));
		label.setBounds(25, 25, label.getPreferredSize().width + 5, label.getPreferredSize().height + 5);

		blockedInput.setBounds(280, 30, 200, 32);
		blockedInput.getDocument().putProperty("filterNewlines", Boolean.TRUE);
		addID.setBackground(Defaults.BUTTON);
		addID.setBounds(490, 31, 30, 30);
		addID.setFont(Defaults.SYMBOLS.deriveFont(18f));
		addID.setForeground(Defaults.FOREGROUND);
		addID.setUI(Defaults.settingsButtonUI);

		blockedSettingsPanel.add(addID);
		blockedSettingsPanel.add(blockedInput);
		blockedSettingsPanel.add(label);
		blockedListPanel.setDoubleBuffered(true);
		blockedListPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 4, 4));
		blockedListPanel.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));

		blockedListPanel.setBounds(0, 0, 542, 0);
		blockedListPanel.setPreferredSize(new Dimension(542, 0));
		blockedListPanel.setBackground(Defaults.SUB_MAIN);
		addID.addActionListener(e -> {

			try {
				Path file = Paths.get(Defaults.saveDirectory + "\\GDBoard\\blockedGDUsers.txt");
				if (!Files.exists(file)) {
					Files.createFile(file);
				}
				boolean goThrough = true;
				Scanner sc = new Scanner(file.toFile());
				while (sc.hasNextLine()) {
					if (String.valueOf(blockedInput.getText()).equals(sc.nextLine())) {
						goThrough = false;
						break;
					}
				}
				sc.close();
				if (goThrough) {
					if (!blockedInput.getText().equalsIgnoreCase("")) {

						Files.write(file, (blockedInput.getText() + "\n").getBytes(), StandardOpenOption.APPEND);
						addButton(blockedInput.getText());
						blockedInput.setText("");
						blockedListPanel.updateUI();
					}
				}
			} catch (IOException e1) {
				DialogBox.showDialogBox("Error!", e1.toString(), "Please report to Alphalaneous.", new String[]{"OK"});
			}
		});
		scrollPane.setBounds(0, 80, 542, 542);
		scrollPane.setPreferredSize(new Dimension(542, 542));

		File file = new File(Defaults.saveDirectory + "\\GDBoard\\blockedGDUsers.txt");
		if (file.exists()) {
			Scanner sc = null;
			try {
				sc = new Scanner(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			assert sc != null;
			while (sc.hasNextLine()) {
				addButton(sc.nextLine());
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			sc.close();
		}
		blockedSettingsPanel.setBounds(0, 0, 542, 622);
		blockedSettingsPanel.add(scrollPane);
		return blockedSettingsPanel;

	}
	public static void resizeHeight(int height){

		height -= 38;

		blockedSettingsPanel.setBounds(blockedSettingsPanel.getX(), blockedSettingsPanel.getY(), blockedSettingsPanel.getWidth(), height);

		scrollPane.setBounds(scrollPane.getX(), scrollPane.getY(), scrollPane.getWidth(), height-80);
		scrollPane.setPreferredSize(new Dimension(scrollPane.getWidth(), height-80));
		scrollPane.updateUI();
	}
	private static void removeUser(String user) {
		i--;
		if (i % 3 == 0) {
			height = height - 39;
			blockedListPanel.setBounds(0, 0, 542, (int) (height + 14));
			blockedListPanel.setPreferredSize(new Dimension(542, (int) (height + 14)));
			scrollPane.updateUI();
		}
		for (Component component : blockedListPanel.getComponents()) {
			if (component instanceof CurvedButton) {
				System.out.println(((CurvedButton) component).getLText());
				if (((CurvedButton) component).getLText().equalsIgnoreCase(user)) {
					blockedListPanel.remove(component);
					blockedListPanel.updateUI();
				}
			}
		}
	}

	public static void addButton(String user) {
		i++;
		if ((i - 1) % 3 == 0) {
			height = height + 39;

			blockedListPanel.setBounds(0, 0, 542, (int) (height + 14));
			blockedListPanel.setPreferredSize(new Dimension(542, (int) (height + 14)));
		}
		Path file = Paths.get(Defaults.saveDirectory + "\\GDBoard\\blockedGDUsers.txt");
		CurvedButton button = new CurvedButton(user);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		button.setBackground(Defaults.BUTTON);
		button.setUI(Defaults.settingsButtonUI);
		button.setForeground(Defaults.FOREGROUND);
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		button.setPreferredSize(new Dimension(170, 35));

		button.addActionListener(e -> {
			SettingsWindow.run = false;
			new Thread(() -> {

				String option = DialogBox.showDialogBox("$UNBLOCK_CREATOR_DIALOG_TITLE$", "<html> $UNBLOCK_CREATOR_DIALOG_INFO$ <html>", "", new String[]{"$YES$", "$NO$"}, new Object[]{button.getLText()});

				if (option.equalsIgnoreCase("YES")) {
					if (Files.exists(file)) {
						try {
							Path temp = Paths.get(Defaults.saveDirectory + "\\GDBoard\\_temp_");
							PrintWriter out = new PrintWriter(new FileWriter(temp.toFile()));
							Files.lines(file)
									.filter(line -> !line.contains(button.getLText()))
									.forEach(out::println);
							out.flush();
							out.close();
							Files.delete(file);
							Files.move(temp, temp.resolveSibling(Defaults.saveDirectory + "\\GDBoard\\blockedGDUsers.txt"), StandardCopyOption.REPLACE_EXISTING);

						} catch (IOException ex) {
							ex.printStackTrace();
							DialogBox.showDialogBox("Error!", ex.toString(), "Please report to Alphalaneous.", new String[]{"OK"});
						}
					}
					removeUser(button.getLText());
				}
				SettingsWindow.run = true;
			}).start();
		});
		button.refresh();
		blockedListPanel.add(button);

	}

	public static void refreshUI() {
		scrollPane.setBackground(Defaults.SUB_MAIN);
		scrollPane.getVerticalScrollBar().setUI(new ScrollbarUI());

		blockedSettingsPanel.setBackground(Defaults.TOP);
		for (Component component : blockedSettingsPanel.getComponents()) {
			if (component instanceof JButton) {
				for (Component component2 : ((JButton) component).getComponents()) {
					if (component2 instanceof JLabel) {
						component2.setForeground(Defaults.FOREGROUND);
					}
				}
				component.setBackground(Defaults.BUTTON);
				component.setForeground(Defaults.FOREGROUND);
			}
			if (component instanceof JLabel) {
				component.setForeground(Defaults.FOREGROUND);

			}
		}
		blockedListPanel.setBackground(Defaults.SUB_MAIN);
		for (Component component : blockedListPanel.getComponents()) {
			if (component instanceof JButton) {
				for (Component component2 : ((JButton) component).getComponents()) {
					if (component2 instanceof JLabel) {
						component2.setForeground(Defaults.FOREGROUND);
					}
				}
				component.setBackground(Defaults.BUTTON);
			}
		}
	}
}
