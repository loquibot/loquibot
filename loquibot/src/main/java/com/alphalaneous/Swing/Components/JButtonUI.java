package com.alphalaneous.Swing.Components;

import com.alphalaneous.Utils.Defaults;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class JButtonUI extends BasicButtonUI implements java.io.Serializable, MouseListener, KeyListener {

	private final Border m_borderRaised = UIManager.getBorder("Button.border");

	private boolean mouseHover = false;

	private Color bgColor = Defaults.COLOR6;
	private Color hoverColor = Defaults.COLOR3;
	private Color selectColor = Defaults.COLOR4;


	public void installUI(JComponent c) {
		super.installUI(c);
		c.addMouseListener(this);
		c.addKeyListener(this);
	}

	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		c.removeMouseListener(this);
		c.removeKeyListener(this);
	}

	public Dimension getPreferredSize(JComponent c) {
		Dimension d = super.getPreferredSize(c);
		if (m_borderRaised != null && d!= null) {
			Insets ins = m_borderRaised.getBorderInsets(c);
			d.setSize(d.width + ins.left + ins.right, d.height + ins.top + ins.bottom);
		}
		return d;
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		JComponent c = (JComponent) e.getComponent();
		c.setBackground(selectColor);
	}

	public void mouseReleased(MouseEvent e) {
		JComponent c = (JComponent) e.getComponent();
		if (mouseHover) {
			c.setBackground(hoverColor);
		} else {
			c.setBackground(bgColor);
		}
	}

	public void mouseEntered(MouseEvent e) {
		mouseHover = true;
		JComponent c = (JComponent) e.getComponent();
		c.setBackground(hoverColor);
		c.repaint();
	}

	public void mouseExited(MouseEvent e) {
		mouseHover = false;
		JComponent c = (JComponent) e.getComponent();
		c.setBackground(bgColor);
		c.repaint();
	}
	public void setSelect(Color color) {
		selectColor = color;
	}
	public void setBackground(Color color) {
		bgColor = color;
	}
	public void setHover(Color color) {
		hoverColor = color;
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}
}