package com.alphalaneous.Components;

import com.alphalaneous.Defaults;
import com.alphalaneous.ThemedComponents.ThemedJButton;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class CurvedButton extends ThemedJButton {

	private final LangLabel text = new LangLabel("");

	public static ArrayList<CurvedButton> buttons = new ArrayList<>();

	public CurvedButton(String label) {
		setLayout(null);
		setOpaque(false);
		text.setTextLang(label);
		text.setForeground(getForeground());
		setBorder(BorderFactory.createEmptyBorder());
		add(text);
		Dimension size = getPreferredSize();
		size.width = size.height = Math.max(size.width, size.height);
		setPreferredSize(size);
		setBackground(Defaults.COLOR2);
		setContentAreaFilled(false);
		buttons.add(this);
	}

	public String getLText(){
		return text.getText();
	}

	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		g.setColor(getBackground());

		RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHints(qualityHints);
		g2.fillRoundRect(0, 0, getSize().width, getSize().height, Defaults.globalArc,  Defaults.globalArc);


		super.paintComponent(g);
	}

	private Shape shape;

	public boolean contains(int x, int y) {
		if (shape == null || !shape.getBounds().equals(getBounds())) {
			shape = new RoundRectangle2D.Float(0,0,getWidth(),getHeight(), Defaults.globalArc, Defaults.globalArc);
		}
		return shape.contains(x, y);
	}
	public void setLForeground(Color color){
		text.setForeground(color);

	}
	public void refresh(){
		text.setForeground(getForeground());
		text.setFont(getFont());
		text.setBounds((getPreferredSize().width/2)-(text.getPreferredSize().width/2), (getPreferredSize().height/2)-(text.getPreferredSize().height/2)-1, text.getPreferredSize().width+5, text.getPreferredSize().height+5);
		setOpaque(false);

	}
	public static void refreshAll(){
		for(CurvedButton button : buttons){
			button.refresh();
		}
	}
}
