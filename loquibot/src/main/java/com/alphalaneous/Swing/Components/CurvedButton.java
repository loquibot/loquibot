package com.alphalaneous.Swing.Components;

import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Utils.Language;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Map;

public class CurvedButton extends JButton {

	@Override
	public JToolTip createToolTip() {
		return tooltip;
	}

	private final String identifier;

	public static ArrayList<CurvedButton> buttons = new ArrayList<>();

	private final JToolTip tooltip = new FancyTooltip(this);

	private final String tooltipText;


	public CurvedButton(String label) {
		this(label, null);
	}
	public CurvedButton(String label, String tooltipText) {
		this.identifier = label;
		this.tooltipText = tooltipText;
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder());
		setText(label);
		setForeground(getForeground());
		setBackground(Defaults.COLOR2);
		setContentAreaFilled(false);


		if(tooltipText != null){
			if(!tooltipText.equalsIgnoreCase("")) {
				setToolTipText(Language.setLocale(tooltipText));
			}
		}


		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				Font originalFont = getFont();

				Map<TextAttribute, Integer> attributes = (Map<TextAttribute, Integer>) originalFont.getAttributes();
				attributes.put(TextAttribute.SIZE, originalFont.getSize() + 2);
				setFont(originalFont.deriveFont(attributes));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				Font originalFont = getFont();
				Map<TextAttribute, Integer> attributes = (Map<TextAttribute, Integer>) originalFont.getAttributes();
				attributes.put(TextAttribute.SIZE, originalFont.getSize() - 2);
				setFont(originalFont.deriveFont(attributes));
			}
		});
		buttons.add(this);
	}

	public void refreshLocale(){
		setToolTipText(Language.setLocale(tooltipText));
		setText(Language.setLocale(identifier));
	}

	@Override
	public void setText(String textA){
		super.setText(Language.setLocale(textA));
	}

	public String getIdentifier(){
		return identifier.replace("$", "");
	}

	private Shape shape;

	@Override
	protected void paintComponent(Graphics g) {
		GraphicsFunctions.roundCorners(g, getBackground(), getSize());
		super.paintComponent(g);
	}
	@Override
	public boolean contains(int x, int y) {
		if (shape == null || !shape.getBounds().equals(getBounds())) {
			shape = new RoundRectangle2D.Float(0,0,getWidth(),getHeight(), Defaults.globalArc, Defaults.globalArc);
		}
		return shape.contains(x, y);
	}
}
