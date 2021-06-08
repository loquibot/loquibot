package com.alphalaneous.SettingsPanels;

import com.alphalaneous.Components.*;
import com.alphalaneous.Defaults;
import com.alphalaneous.ThemedComponents.ThemedCheckbox;
import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Windows.SettingsWindow;
import com.alphalaneous.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.alphalaneous.Defaults.settingsButtonUI;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class RequestsLog {
	private static final JPanel blockedSettingsPanel = new JPanel();
	private static final JPanel blockedListPanel = new JPanel();
	private static final JScrollPane scrollPane = new SmoothScrollPane(blockedListPanel);
	private static final RoundedJButton clearLogs = new RoundedJButton("\uF0CE", "$CLEAR_LOGS_TOOLTIP$");

	private static int i = 0;
	private static double height = 0;

	public static JPanel createPanel() {

		blockedSettingsPanel.setBackground(Defaults.TOP);
		blockedSettingsPanel.setLayout(null);

		LangLabel label = new LangLabel("$LOGGED_IDS_SETTINGS$");
		label.setForeground(Defaults.FOREGROUND);
		label.setFont(Defaults.MAIN_FONT.deriveFont(24f));
		label.setBounds(25, 25, label.getPreferredSize().width + 5, label.getPreferredSize().height + 5);


		clearLogs.setBackground(Defaults.BUTTON);
		clearLogs.setBounds(490, 31, 30, 30);
		clearLogs.setFont(Defaults.SYMBOLS.deriveFont(18f));
		clearLogs.setForeground(Defaults.FOREGROUND);
		clearLogs.setUI(settingsButtonUI);
		clearLogs.addActionListener(e ->
				new Thread(() -> {
					Path file = Paths.get(Defaults.saveDirectory + "\\GDBoard\\requestsLog.txt");
					String option = DialogBox.showDialogBox("$CLEAR_LOGS_DIALOG_TITLE$", "<html> $CLEAR_LOGS_DIALOG_INFO$ <html>", "", new String[]{"$YES$", "$NO$"});

					if (option.equalsIgnoreCase("YES")) {
						if (Files.exists(file)) {
							try {
								Files.delete(file);
								clear();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
					SettingsWindow.run = true;
				}).start()
		);

		blockedSettingsPanel.add(label);
		blockedSettingsPanel.add(clearLogs);
		blockedListPanel.setDoubleBuffered(true);
		blockedListPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 4, 4));
		blockedListPanel.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
		//blockedListPanel.setBounds(0, 0, 415, 0);
		blockedListPanel.setPreferredSize(new Dimension(542, 0));
		blockedListPanel.setBackground(Defaults.SUB_MAIN);
		scrollPane.setBounds(0, 80, 542, 542);
		scrollPane.setPreferredSize(new Dimension(542, 542));

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
	public static void clear() {
		blockedListPanel.removeAll();
		height = 0;
		blockedListPanel.setBounds(0, 0, 542, (int) (height + 14));
		blockedListPanel.setPreferredSize(new Dimension(542, (int) (height + 14)));
		scrollPane.updateUI();

	}

	private static void removeID(String ID) {
		i--;
		if (i % 6 == 0 && i != 0) {
			height = height - 39;
			blockedListPanel.setBounds(0, 0, 542, (int) (height + 14));
			blockedListPanel.setPreferredSize(new Dimension(542, (int) (height + 14)));

			scrollPane.updateUI();
		}
		scrollPane.updateUI();
		for (Component component : blockedListPanel.getComponents()) {
			if (component instanceof CurvedButton) {
				if (((CurvedButton) component).getLText().equalsIgnoreCase(ID)) {
					blockedListPanel.remove(component);
					blockedListPanel.updateUI();
				}
			}
		}
	}

	public static void addButton(long ID) {
		i++;
		if ((i - 1) % 6 == 0) {
			height = height + 39;
			blockedListPanel.setBounds(0, 0, 542, (int) (height + 14));
			blockedListPanel.setPreferredSize(new Dimension(542, (int) (height + 14)));
		}

		Path file = Paths.get(Defaults.saveDirectory + "\\GDBoard\\requestsLog.txt");
		CurvedButton button = new CurvedButton(String.valueOf(ID));
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		button.setBackground(Defaults.BUTTON);
		button.setUI(settingsButtonUI);
		button.setForeground(Defaults.FOREGROUND);
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		button.setPreferredSize(new Dimension(84, 35));
		button.addActionListener(e -> {
			SettingsWindow.run = false;

			new Thread(() -> {

				String option = DialogBox.showDialogBox("$REMOVE_LOG_DIALOG_TITLE$", "<html> $REMOVE_LOG_DIALOG_INFO$ <html>", "", new String[]{"$YES$", "$NO$"}, new Object[]{button.getLText()});

				if (option.equalsIgnoreCase("YES")) {
					if (Files.exists(file)) {
						try {
							Path temp = Paths.get(Defaults.saveDirectory + "\\GDBoard\\_tempLog_");
							PrintWriter out = new PrintWriter(new FileWriter(temp.toFile()));
							Files.lines(file)
									.filter(line -> !line.contains(button.getLText()))
									.forEach(out::println);
							out.flush();
							out.close();
							Files.delete(file);
							Files.move(temp, temp.resolveSibling(Defaults.saveDirectory + "\\GDBoard\\requestsLog.txt"), StandardCopyOption.REPLACE_EXISTING);

						} catch (IOException ex) {

							JOptionPane.showMessageDialog(Window.windowFrame, "There was an error writing to the file!", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					removeID(button.getLText());
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
