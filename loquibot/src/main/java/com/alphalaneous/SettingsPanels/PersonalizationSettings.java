package com.alphalaneous.SettingsPanels;

import com.alphalaneous.Components.LangLabel;
import com.alphalaneous.Components.RadioButton;
import com.alphalaneous.Components.RadioPanel;
import com.alphalaneous.Defaults;
import com.alphalaneous.Panels.SettingsTitle;
import com.alphalaneous.Settings;
import com.alphalaneous.ThemedComponents.ThemedCheckbox;
import com.alphalaneous.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class PersonalizationSettings {
	public static boolean onTopOption = false;
	public static boolean disableNotifOption = false;
	public static String theme = "SYSTEM_MODE";
	private static final JPanel panel = new JPanel(null);
	private static final RadioPanel themePanel = new RadioPanel(new String[]{"$LIGHT_MODE$", "$DARK_MODE$", "$SYSTEM_MODE$"});
	private static final LangLabel themeText = new LangLabel("$THEME_TEXT$");
	private static final ThemedCheckbox onTop = createButton("$ALWAYS_ON_TOP$", 210);
	private static final ThemedCheckbox notifications = createButton("$DISABLE_NOTIFICATIONS$", 240);

	public static JPanel createPanel() {

		themeText.setBounds(25, 75, 542, 30);
		themeText.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		themeText.setForeground(Defaults.FOREGROUND2);
		themeText.setOpaque(false);
		themePanel.setBounds(25, 110, 542, 500);
		onTop.setChecked(false);
		onTop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				onTopOption = onTop.getSelectedState();
				Window.setOnTop(onTop.getSelectedState());
				Settings.writeSettings("onTop", String.valueOf(onTopOption));
			}
		});
		notifications.setChecked(false);
		notifications.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				disableNotifOption = notifications.getSelectedState();
				Settings.writeSettings("disableNotifications", String.valueOf(disableNotifOption));
			}
		});

		for (RadioButton button : themePanel.buttons) {
			button.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					System.out.println(themePanel.getSelectedButton());
					theme = themePanel.getSelectedButton();
					if (theme.equalsIgnoreCase("DARK_MODE")) {
						Defaults.setDark();
					} else if (theme.equalsIgnoreCase("LIGHT_MODE")) {
						Defaults.setLight();
					} else {
						Defaults.setSystem();
					}
					Settings.writeSettings("theme", themePanel.getSelectedButton());

				}
			});
		}
		themePanel.setChecked("SYSTEM_MODE");
		panel.setDoubleBuffered(true);
		panel.setBounds(0, 0, 542, 622);
		panel.setBackground(Defaults.SUB_MAIN);

		panel.add(new SettingsTitle("$PERSONALIZATION_SETTINGS$"));
		panel.add(onTop);
		panel.add(notifications);
		panel.add(themePanel);
		panel.add(themeText);
		return panel;

	}

	private static ThemedCheckbox createButton(String text, int y) {
		ThemedCheckbox button = new ThemedCheckbox(text);
		button.setBounds(25, y, 490, 30);
		button.setForeground(Defaults.FOREGROUND);
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		button.refresh();
		return button;
	}

	public static void loadSettings() {
		if (!Settings.getSettings("theme").asString().equalsIgnoreCase("")) {
			theme = Settings.getSettings("theme").asString();
			themePanel.setChecked(theme);
			if (theme.equalsIgnoreCase("DARK_MODE")) {
				Defaults.setDark();
				themePanel.setChecked("DARK_MODE");
			} else if (theme.equalsIgnoreCase("LIGHT_MODE")) {
				Defaults.setLight();
				themePanel.setChecked("LIGHT_MODE");
			} else {
				Defaults.setSystem();
			}
		} else {
			Defaults.setSystem();
		}
		if (!Settings.getSettings("onTop").asString().equalsIgnoreCase("")) {
			onTopOption = Settings.getSettings("onTop").asBoolean();
			onTop.setChecked(onTopOption);
			Window.setOnTop(onTopOption);
		}
		if (!Settings.getSettings("disableNotifications").asString().equalsIgnoreCase("")) {
			disableNotifOption = Settings.getSettings("disableNotifications").asBoolean();
			notifications.setChecked(disableNotifOption);
		}
	}

	public static void refreshUI() {
		themePanel.refreshUI();
		themeText.setForeground(Defaults.FOREGROUND2);
		panel.setBackground(Defaults.SUB_MAIN);
	}
}
