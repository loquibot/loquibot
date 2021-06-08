package com.alphalaneous.Components;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Set;

public class TextLabel extends JLabel{



	private final String[] fontFamilies = GraphicsEnvironment.
			getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

	private final String[] preferredFonts = {
			"Segoe UI",
			"Microsoft JhengHei",
			"Noto Sans CJK TC Black",
	};

	public TextLabel(String text){
		this.setText(text);
		this.setFont(getPreferredFontForText(text));
	}
	public void setLText(String text){
		this.setText(text);
		this.setFont(getPreferredFontForText(text));
	}


	private HashMap getCompatibleFonts(String text) {
		HashMap cF = new HashMap<>();
		for (String font : fontFamilies) {
			Font f = new Font(font, Font.PLAIN, 1);
			if (f.canDisplayUpTo(text) < 0) {
				cF.put(font, f);
			}
		}
		return cF;
	}

	private Font getPreferredFontForText(String text) {
		HashMap compatibleFonts = getCompatibleFonts(text);
		for (String preferredFont : preferredFonts) {
			Font font = (Font) compatibleFonts.get(preferredFont);
			if (font != null) {
				return font;
			}
		}
		Set keySet = compatibleFonts.keySet();
		String firstCompatibleFont = (String) keySet.iterator().next();
		return (Font) compatibleFonts.get(firstCompatibleFont);
	}


}
