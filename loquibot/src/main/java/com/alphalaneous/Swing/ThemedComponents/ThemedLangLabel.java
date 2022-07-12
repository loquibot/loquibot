package com.alphalaneous.Swing.ThemedComponents;

import com.alphalaneous.Swing.Components.LangLabel;
import com.alphalaneous.Utils.Defaults;

import java.util.ArrayList;

public class ThemedLangLabel extends LangLabel {

	public static ArrayList<ThemedLangLabel> labels = new ArrayList<>();

	public ThemedLangLabel(String text) {
		super(text);
		labels.add(this);
	}

	public static void refreshAll() {
		for (ThemedLangLabel label : labels) {
			label.refresh();
		}
	}

	public void refresh() {
		setForeground(Defaults.FOREGROUND_A);
	}
}
