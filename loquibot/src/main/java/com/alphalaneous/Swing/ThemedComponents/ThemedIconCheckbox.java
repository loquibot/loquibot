package com.alphalaneous.Swing.ThemedComponents;

import com.alphalaneous.Utils.Defaults;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ThemedIconCheckbox extends JPanel {

	private static final ArrayList<ThemedIconCheckbox> buttons = new ArrayList<>();

	private final JLabel text = new JLabel();
	private final JLabel check = new JLabel("\uE922");
	private final JLabel checkSymbol = new JLabel("\uE73E");
	private final JLabel hover = new JLabel("\uE922");
	private boolean isChecked = false;

	public ThemedIconCheckbox(Icon icon) {
		setLayout(null);
		text.setIcon(icon);
		check.setFont(Defaults.SYMBOLSalt.deriveFont(16f));
		checkSymbol.setForeground(Color.WHITE);
		checkSymbol.setFont(Defaults.SYMBOLSalt.deriveFont(16f));
		hover.setForeground(Defaults.FOREGROUND_A);
		hover.setFont(Defaults.SYMBOLSalt.deriveFont(16f));
		checkSymbol.setVisible(false);
		hover.setVisible(false);
		add(hover);
		add(checkSymbol);
		add(check);
		add(text);
		setBackground(new Color(0, 0, 0, 0));
		setOpaque(false);
		check.setForeground(Defaults.FOREGROUND_B);
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					check.setText("\uE73B");
					check.setForeground(Defaults.FOREGROUND_B);
					hover.setVisible(false);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					if (isChecked) {
						check.setText("\uE922");
						check.setForeground(Defaults.FOREGROUND_B);
						checkSymbol.setVisible(false);
						isChecked = false;
					} else {
						check.setText("\uE73B");
						check.setForeground(Defaults.ACCENT);
						checkSymbol.setVisible(true);
						isChecked = true;
					}
				}
				hover.setVisible(true);
			}

			public void mouseEntered(MouseEvent e) {
				hover.setVisible(true);
			}

			public void mouseExited(MouseEvent e) {
				if (!isChecked) {
					check.setText("\uE922");
					check.setForeground(Defaults.FOREGROUND_B);
					checkSymbol.setVisible(false);
				} else {
					check.setText("\uE73B");
					check.setForeground(Defaults.ACCENT);
					checkSymbol.setVisible(true);
				}
				hover.setVisible(false);
			}
		});
		buttons.add(this);
	}

	public static void refreshAll() {
		for (ThemedIconCheckbox button : buttons) {
			button.refresh();
		}
	}

	public void setText(String textA) {
		text.setText(textA);
	}

	public boolean getSelectedState() {
		return isChecked;
	}

	public void setChecked(boolean checked) {
		this.isChecked = checked;
		if (!isChecked) {
			check.setText("\uE922");
			check.setForeground(Defaults.FOREGROUND_B);
			checkSymbol.setVisible(false);
		} else {
			check.setText("\uE73B");
			check.setForeground(Defaults.ACCENT);
			checkSymbol.setVisible(true);
		}
	}
	private static final int inset = -11;
	public void refresh() {
		if (!isChecked) {
			check.setForeground(Defaults.FOREGROUND_B);
		} else {
			check.setForeground(Defaults.ACCENT);
		}
		text.setBounds(0,5,text.getPreferredSize().width, text.getPreferredSize().height);

		check.setBounds(getPreferredSize().width/2 + inset, getPreferredSize().height/2, 30, 30);
		checkSymbol.setBounds(getPreferredSize().width/2 + inset, getPreferredSize().height/2, 30, 30);
		hover.setForeground(Defaults.FOREGROUND_A);
		hover.setBounds(getPreferredSize().width/2 + inset, getPreferredSize().height/2, 30, 30);
	}
}
