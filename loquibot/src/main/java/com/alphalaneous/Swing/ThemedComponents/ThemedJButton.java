package com.alphalaneous.Swing.ThemedComponents;

import com.alphalaneous.Utils.Defaults;

import javax.swing.*;
import java.util.ArrayList;

public class ThemedJButton extends JButton {

	private static final ArrayList<ThemedJButton> buttons = new ArrayList<>();
	private String colorChoiceF;
	private String colorChoiceB;
	public ThemedJButton() {
		setBorder(BorderFactory.createEmptyBorder());
		buttons.add(this);
	}


	public ThemedJButton(String text) {
		super(text);
		setBorder(BorderFactory.createEmptyBorder());
		buttons.add(this);
	}

	public static void refreshAll() {
		for (ThemedJButton button : buttons) {
			button.refresh();
		}
	}

	public void setColorF(String color) {
		colorChoiceF = color;
		setForeground(Defaults.colors.get(color));
	}

	public void setColorB(String color) {
		colorChoiceB = color;
		setBackground(Defaults.colors.get(color));
	}

	private void refresh() {
		setForeground(Defaults.colors.get(colorChoiceF));
		setBackground(Defaults.colors.get(colorChoiceB));
	}
}
