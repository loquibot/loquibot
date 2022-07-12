package com.alphalaneous.Swing.ThemedComponents;

import com.alphalaneous.Utils.Defaults;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ThemedJPanel extends JPanel {


	private static final ArrayList<ThemedJPanel> panels = new ArrayList<>();
	private String colorChoice;

	public ThemedJPanel() {
		super();
		panels.add(this);
	}

	public ThemedJPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		panels.add(this);
	}

	public ThemedJPanel(LayoutManager layout) {
		super(layout);
		panels.add(this);
	}

	public ThemedJPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		panels.add(this);
	}

	public static void refreshAll() {
		for (ThemedJPanel panel : panels) {
			panel.refresh();
		}
	}

	public void setColor(String color) {
		colorChoice = color;
		setBackground(Defaults.colors.get(color));
	}

	public void refresh() {
		setBackground(Defaults.colors.get(colorChoice));
	}
}
