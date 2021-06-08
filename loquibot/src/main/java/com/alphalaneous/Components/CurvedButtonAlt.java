package com.alphalaneous.Components;

import com.alphalaneous.Language;
import com.alphalaneous.ThemedComponents.ThemedJButton;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class CurvedButtonAlt extends ThemedJButton {

	public static ArrayList<CurvedButtonAlt> buttonList = new ArrayList<>();

	private String text;
	public CurvedButtonAlt(String label) {
		setLayout(null);
		this.text = label;
		setText(Language.setLocale(label));

		Dimension size = getPreferredSize();
		size.width = size.height = Math.max(size.width, size.height);
		setPreferredSize(size);
		setContentAreaFilled(false);
		buttonList.add(this);
	}

	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		g.setColor(getBackground());

		RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHints(qualityHints);
		g2.fillRoundRect(0, 0, getSize().width, getSize().height, 10, 10);


		super.paintComponent(g);
	}

	/*
	 * protected void paintBorder(Graphics g) { g.setColor(getForeground());
	 * g.drawOval(0, 0, getSize().width-1, getSize().height-1); }
	 */

	private Shape shape;

	public boolean contains(int x, int y) {
		if (shape == null || !shape.getBounds().equals(getBounds())) {
			shape = new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),10,10);
		}
		return shape.contains(x, y);
	}
	public void refreshLocale(){
		setText(Language.setLocale(text));
	}
	public void setTextLang(String text){
		this.text = text;
		setText(Language.setLocale(text));
	}


	public static void refreshAllLocale(){
		for(CurvedButtonAlt button : buttonList){
			button.refreshLocale();
		}
	}

}
