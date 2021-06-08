package com.alphalaneous.Components;

import com.alphalaneous.Defaults;

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
			text.setForeground(Defaults.FOREGROUND);
			radio.setFont(Defaults.SYMBOLSalt.deriveFont(16f));
			radio.setForeground(Color.LIGHT_GRAY);
			add(radio);
			add(text);
			setBackground(new Color(0, 0, 0, 0));
			setOpaque(false);
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					if (SwingUtilities.isLeftMouseButton(e)) {
						radio.setForeground(Color.LIGHT_GRAY);
					}
				}

				public void mouseReleased(MouseEvent e) {
					if (SwingUtilities.isLeftMouseButton(e)) {
						if (isChecked) {
							radio.setText("\uECCA");
							radio.setForeground(Defaults.FOREGROUND);
							isChecked = false;
						} else {
							radio.setText("\uECCB");
							radio.setForeground(Defaults.FOREGROUND);
							isChecked = true;
						}
					}
				}

				public void mouseEntered(MouseEvent e) {
					radio.setForeground(Defaults.FOREGROUND);
				}

				public void mouseExited(MouseEvent e) {
					if (!isChecked) {
						radio.setText("\uECCA");
						radio.setForeground(Color.LIGHT_GRAY);
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
			radio.setForeground(Color.LIGHT_GRAY);
		} else {
			radio.setText("\uECCB");
			radio.setForeground(Defaults.ACCENT);
		}
	}
	/*public void setLText(String text) {
		this.text.setText(text);
		refresh();
	}*/
	public void refresh(){
		if(!isChecked){
			radio.setForeground(Color.LIGHT_GRAY);
		}
		else{
			radio.setForeground(Defaults.ACCENT);
		}
		text.setForeground(Defaults.FOREGROUND);
		text.setFont(getFont());
		if(getFont().getName().equalsIgnoreCase("bahnschrift")){
			text.setBounds(0, (getHeight()/2)-(text.getPreferredSize().height/2)+1, getWidth(), text.getPreferredSize().height+5);
		}
		else{
			text.setBounds(0, (getHeight()/2)-(text.getPreferredSize().height/2)-2, getWidth(), text.getPreferredSize().height+5);
		}
		radio.setBounds(getWidth()-20, 0, 30,30);
	}
}
