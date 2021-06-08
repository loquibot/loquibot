package com.alphalaneous.Components;

import com.alphalaneous.Defaults;
import com.alphalaneous.Language;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SettingsButtonOld extends JButton {

	private final LangLabel titleLabel = new LangLabel("");
	private final LangLabel subLabel = new LangLabel("");
	private final JLabel iconLabel = new JLabel();

	private final JButtonUI coolUI = new JButtonUI();

	public SettingsButtonOld(String title, String sub, String icon){

		coolUI.setSelect(Defaults.TOP);
		coolUI.setHover(Defaults.TOP);
		coolUI.setBackground(Defaults.TOP);

		String newTitle = Language.setLocale(title);
		String newSub = Language.setLocale(sub);

		titleLabel.setTextLang(newTitle);
		titleLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));
		titleLabel.setForeground(Defaults.FOREGROUND);

		subLabel.setTextLang("<html>" + newSub + "</html>");
		subLabel.setFont(Defaults.MAIN_FONT.deriveFont(12f));
		subLabel.setForeground(Defaults.FOREGROUND2);

		iconLabel.setFont(Defaults.SYMBOLS.deriveFont(30f));
		iconLabel.setForeground(Defaults.ACCENT);
		iconLabel.setText(icon);
		setLayout(null);
		setUI(coolUI);

		iconLabel.setBounds(15,10, 50,50);
		titleLabel.setBounds(60,10, 180, 30);
		subLabel.setBounds(60, 28, 150, 50);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				setBorder(BorderFactory.createEmptyBorder());

			}
			@Override
			public void mouseReleased(MouseEvent e) {
				setBorder(BorderFactory.createLineBorder(Defaults.BUTTON_HOVER, 2));
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				setBorder(BorderFactory.createLineBorder(Defaults.BUTTON_HOVER, 2));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				setBorder(BorderFactory.createEmptyBorder());

			}
		});

		add(iconLabel);
		add(subLabel);
		add(titleLabel);

		setPreferredSize(new Dimension(250, 85));
		setBounds(0,0,250,85);
		setBackground(Defaults.TOP);
		setBorder(BorderFactory.createEmptyBorder());

	}
	public void refreshUI(){
		titleLabel.setForeground(Defaults.FOREGROUND);
		subLabel.setForeground(Defaults.FOREGROUND2);
		iconLabel.setForeground(Defaults.ACCENT);

		coolUI.setSelect(Defaults.TOP);
		coolUI.setHover(Defaults.TOP);
		coolUI.setBackground(Defaults.TOP);
		setBackground(Defaults.TOP);

	}
}
