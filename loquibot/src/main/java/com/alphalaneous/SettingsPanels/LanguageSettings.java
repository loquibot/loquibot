package com.alphalaneous.SettingsPanels;

import com.alphalaneous.Components.FancyTextArea;
import com.alphalaneous.Defaults;
import com.alphalaneous.ThemedComponents.ThemedCheckbox;

import javax.swing.*;
import java.awt.*;


public class LanguageSettings {
	private static final JPanel panel = new JPanel();

	public static JPanel createPanel() {

		panel.setLayout(null);
		panel.setDoubleBuffered(true);
		panel.setBounds(0, 0, 415, 622);
		panel.setBackground(Defaults.SUB_MAIN);

		return panel;
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
				component.setBackground(Defaults.BUTTON);
			}
			if (component instanceof JTextArea) {
				((FancyTextArea) component).refresh_();
			}
		}
	}
}
