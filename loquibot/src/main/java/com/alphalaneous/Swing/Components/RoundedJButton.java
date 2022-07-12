package com.alphalaneous.Swing.Components;

import com.alphalaneous.Utils.Language;
import com.alphalaneous.Swing.ThemedComponents.ThemedJButton;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.*;

public class RoundedJButton extends ThemedJButton {

	@Override
	public JToolTip createToolTip() {
		return (tooltip);
	}

	public static ArrayList<RoundedJButton> buttons = new ArrayList<>();
	private String tooltipText;
	private final String text;
	private final JToolTip tooltip = new FancyTooltip(this);

	public RoundedJButton(String label, String tooltip) {
		super(label);
		this.tooltipText = tooltip;
		this.text = label;
		Dimension size = getPreferredSize();
		size.width = size.height = Math.max(size.width, size.height);
		setPreferredSize(size);
		setContentAreaFilled(false);
		if(!tooltip.equalsIgnoreCase("")) {
			setToolTipText(Language.setLocale(tooltip));
		}
		setText(Language.setLocale(text));
		buttons.add(this);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				Font originalFont = getFont();
				Map attributes = originalFont.getAttributes();
				attributes.put(TextAttribute.SIZE, originalFont.getSize() + 2);
				setFont(originalFont.deriveFont(attributes));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				Font originalFont = getFont();
				Map attributes = originalFont.getAttributes();
				attributes.put(TextAttribute.SIZE, originalFont.getSize() - 2);
				setFont(originalFont.deriveFont(attributes));
			}
		});
	}
	public void setTooltip(String tooltip){
		this.tooltipText = tooltip;
		setToolTipText(Language.setLocale(tooltip));
	}
	public void refreshLocale(){
		setToolTipText(Language.setLocale(tooltipText));
		setText(Language.setLocale(text));
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

	private Shape shape;

	public boolean contains(int x, int y) {
		if (shape == null || !shape.getBounds().equals(getBounds())) {
			shape = new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),10,10);
		}
		return shape.contains(x, y);
	}
	public void setTextLang(String text){
		this.tooltipText = text;
		setText(Language.setLocale(text));
	}

	public void setColorB(String color){
	}
	public void setColorF(String color){
	}
	public void refresh_(){
		setForeground(getForeground());
		setBackground(getBackground());
	}

	public static void refreshAll(){
		for(RoundedJButton button : buttons){
			button.refresh_();
		}
	}

}
