package com.alphalaneous.Components;

import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Utilities.Fonts;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class RadioPanel extends ThemeableJPanel {

	public ArrayList<RadioButton> buttons = new ArrayList<>();
	public String currentSelect = "";

	public RadioPanel(HashMap<String, String> labels) {

		setOpaque(false);
		setLayout(new MigLayout("flowy, insets 0"));

		labels.forEach((k, v) -> {
			RadioButton radioButton = new RadioButton(k, v);
			radioButton.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));
			radioButton.setBorder(BorderFactory.createEmptyBorder());

			radioButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					for (RadioButton button : buttons) {
						if (button.getText().equalsIgnoreCase(radioButton.getText())) {
							button.setChecked(true);
							currentSelect = radioButton.getText();
						} else {
							button.setChecked(false);
						}
					}
					changeFired(v);
				}
			});
			buttons.add(radioButton);
			add(radioButton, "width 100%");
		});
	}

	public void changeFired(String setting){
	}

	public void setChecked(String option) {
		for (RadioButton button : buttons) {
			if (button.getSetting().equals(option)) {
				button.setChecked(true);
				currentSelect = option;
			} else {
				button.setChecked(false);
			}
		}
	}

	public String getSelectedButton() {
		for (RadioButton button : buttons) {
			if (button.getSelectedState()) {
				return button.getText();
			}
		}
		return null;
	}
}
