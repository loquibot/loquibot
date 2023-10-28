package com.alphalaneous.Components;

import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Fonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class RadioPanel extends ThemeableJPanel {

	public ArrayList<RadioButton> buttons = new ArrayList<>();
	public String currentSelect = "";

	public RadioPanel(String... labels) {
		int pos = 0;
		setOpaque(false);
		setLayout(null);
		for (String label : labels) {
			RadioButton radioButton = new RadioButton(label);
			radioButton.setBounds(0, pos, 475, 30);
			radioButton.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));
			radioButton.setBorder(BorderFactory.createEmptyBorder());
			pos = pos + 30;

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
					changeFired(radioButton.getText());
				}
			});
			buttons.add(radioButton);
			add(radioButton);
		}
		setPreferredSize(new Dimension(getWidth(), pos + 30));
	}

	public void changeFired(String identifier){
	}

	public void setChecked(String option) {
		for (RadioButton button : buttons) {
			if (button.getText().equalsIgnoreCase(option)) {
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


	public void setWidth(int width){
		for (RadioButton button : buttons) {
			button.setPreferredSize(new Dimension(width, button.getHeight()));
			button.setBounds(button.getX(), button.getY(), width, button.getHeight());
		}
	}
}
