package com.alphalaneous.Swing.Components;

import com.alphalaneous.Defaults;
import com.alphalaneous.Swing.SilentDeletePrevCharacter;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class FancyPasswordField extends JPasswordField {

	public static ArrayList<FancyPasswordField> fields = new ArrayList<>();

	public FancyPasswordField() {
		setOpaque(false);
		setBackground(Defaults.COLOR2);
		setForeground(Defaults.FOREGROUND_A);
		setCaret(new MyCaret());
		setCaretColor(Defaults.FOREGROUND_A);
		setFont(Defaults.SEGOE_FONT.deriveFont(14f));
		getActionMap().put(DefaultEditorKit.deletePrevCharAction, new SilentDeletePrevCharacter());
		setSelectionColor(Defaults.ACCENT);

		addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				borderColor = Defaults.ACCENT;
				repaint();
			}

			@Override
			public void focusLost(FocusEvent e) {
				borderColor = Defaults.COLOR5;
				repaint();
			}
		});
		fields.add(this);
	}

	public void refresh_(){
		setBackground(Defaults.COLOR2);
		setForeground(Defaults.FOREGROUND_A);
		setCaretColor(Defaults.FOREGROUND_A);
		borderColor = Defaults.COLOR5;
	}

	public static void refreshAll(){
		for(FancyPasswordField field : fields){
			field.refresh_();
		}
	}
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		g.setColor(getBackground());

		RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHints(qualityHints);
		g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
		super.paintComponent(g);
	}

	private Color borderColor = Defaults.COLOR5;

	@Override
	protected void paintBorder(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(borderColor);
		g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, getRadius()+3, getRadius()+3);

	}

	public int getRadius() {
		return 10;
	}

	@Override
	public Insets getInsets() {
		int value = getRadius() / 2;
		return new Insets(value+1, value, value, value);
	}

	public static class MyCaret extends DefaultCaret {

		MyCaret() {
			setBlinkRate(500);
		}

		@Override
		protected synchronized void damage(Rectangle r) {
			if (r == null) {
				return;
			}

			JTextComponent comp = getComponent();
			FontMetrics fm = comp.getFontMetrics(comp.getFont());
			int textWidth = fm.stringWidth("|");
			int textHeight = fm.getHeight();
			x = (int) r.getX();
			y = (int) r.getY();
			width = textWidth;
			height = textHeight;
			repaint();
		}

		@Override
		public void paint(Graphics g) {
			JTextComponent comp = getComponent();
			if (comp == null) {
				return;
			}

			int dot = getDot();
			Rectangle2D r;
			try {
				r = comp.modelToView2D(dot);
			} catch (BadLocationException e) {
				return;
			}
			if (r == null) {
				return;
			}

			if ((x != r.getX()) || (y != r.getY())) {
				repaint();
				damage(new Rectangle((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight()));
			}

			if (isVisible()) {
				FontMetrics fm = comp.getFontMetrics(comp.getFont());

				g.setColor(comp.getCaretColor());
				String mark = "|";
				g.drawString(mark, x, y + fm.getAscent()-2);
			}
		}

	}
}
