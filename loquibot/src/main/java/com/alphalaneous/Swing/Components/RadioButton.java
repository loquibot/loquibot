package com.alphalaneous.Swing.Components;

import com.alphalaneous.Utils.Defaults;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RadioButton extends JPanel {

	private final LangLabel text = new LangLabel("");
	private final JLabel radio = new JLabel("\uECCA");
	private boolean isChecked = false;
	public RadioButton(String label) {

		setLayout(null);
		text.setTextLang(label);
		text.setForeground(Defaults.FOREGROUND_A);
		radio.setFont(Defaults.SYMBOLSalt.deriveFont(16f));
		radio.setForeground(Defaults.FOREGROUND_B);
		add(radio);
		add(text);
		setBackground(new Color(0, 0, 0, 0));
		setOpaque(false);
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					radio.setForeground(Defaults.FOREGROUND_B);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					if (isChecked) {
						radio.setText("\uECCA");
						radio.setForeground(Defaults.FOREGROUND_A);
						isChecked = false;
					} else {
						radio.setText("\uECCB");
						radio.setForeground(Defaults.FOREGROUND_A);
						isChecked = true;
					}
				}
			}

			public void mouseEntered(MouseEvent e) {
				radio.setForeground(Defaults.FOREGROUND_A);
			}

			public void mouseExited(MouseEvent e) {
				if (!isChecked) {
					radio.setText("\uECCA");
					radio.setForeground(Defaults.FOREGROUND_B);
				} else {
					radio.setText("\uECCB");
					radio.setForeground(Defaults.ACCENT);
				}
			}
		});
	}
	public String getIdentifier(){
		return text.getIdentifier();
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
			radio.setForeground(Defaults.FOREGROUND_B);
		} else {
			radio.setText("\uECCB");
			radio.setForeground(Defaults.ACCENT);
		}
	}

	public void refresh(){
		if(!isChecked){
			radio.setForeground(Defaults.FOREGROUND_B);
		}
		else{
			radio.setForeground(Defaults.ACCENT);
		}
		text.setForeground(Defaults.FOREGROUND_A);
		text.setFont(getFont());
		text.setBounds(30, (getHeight()/2)-(text.getPreferredSize().height/2), getWidth(), text.getPreferredSize().height+5);
		radio.setBounds(0, 1, 30,30);
	}
}
