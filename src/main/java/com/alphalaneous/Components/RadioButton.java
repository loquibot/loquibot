package com.alphalaneous.Components;

import com.alphalaneous.Components.ThemableJComponents.ThemeableJLabel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Fonts;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RadioButton extends ThemeableJPanel {

	private final ThemeableJLabel text = new ThemeableJLabel("");
	private final ThemeableJLabel radio = new ThemeableJLabel("\uECCA");
	private boolean isChecked = false;
	public RadioButton(String label) {

		setLayout(null);
		setOpaque(false);

		text.setText(label);
		text.setForeground("foreground");
		radio.setFont(Fonts.getFont("SegoeFluent").deriveFont(16f));
		radio.setForeground("foreground-darker");
		add(radio);
		add(text);

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					radio.setForeground("foreground-darker");
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					if (isChecked) {
						radio.setText("\uECCA");
						radio.setForeground("foreground");
						isChecked = false;
					} else {
						radio.setText("\uECCB");
						radio.setForeground("foreground");
						isChecked = true;
					}
				}
			}

			public void mouseEntered(MouseEvent e) {
				radio.setForeground("foreground");
			}

			public void mouseExited(MouseEvent e) {
				if (!isChecked) {
					radio.setText("\uECCA");
					radio.setForeground("foreground-darker");
				} else {
					radio.setText("\uECCB");
					radio.setForeground("accent");
				}
			}
		});
	}
	public String getText(){
		return text.getText();
	}
	public void setText(String textA){
		text.setText(textA);
	}
	public boolean getSelectedState(){
		return isChecked;
	}
	public void setChecked(boolean checked){
	 	this.isChecked = checked;
		if (!isChecked) {
			radio.setText("\uECCA");
			radio.setForeground("foreground-darker");
		} else {
			radio.setText("\uECCB");
			radio.setForeground("accent");
		}
	}
}
