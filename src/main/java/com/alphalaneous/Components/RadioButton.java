package com.alphalaneous.Components;

import com.alphalaneous.Components.ThemableJComponents.ThemeableJLabel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Utilities.Fonts;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RadioButton extends ThemeableJPanel {

	private final ThemeableJLabel text = new ThemeableJLabel("");
	private final ThemeableJLabel radio = new ThemeableJLabel("\uECCA");
	private boolean isChecked = false;

	private String setting;
	public RadioButton(String label, String setting) {

		setLayout(new MigLayout("insets 0, al left center", "[][]"));
		setOpaque(false);

		this.setting = setting;

		text.setText(label);
		text.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));
		text.setForeground("foreground");
		radio.setFont(Fonts.getFont("SegoeFluent").deriveFont(16f));
		radio.setForeground("foreground-darker");
		add(radio, "width 20px, height 20px");
		add(text, "spany 2, wrap, height 14");

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

	public String getSetting(){
		return setting;
	}
	public void setSetting(String setting){
		this.setting = setting;
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
