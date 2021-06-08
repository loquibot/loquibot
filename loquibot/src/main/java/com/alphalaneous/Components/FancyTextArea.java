package com.alphalaneous.Components;

import com.alphalaneous.Defaults;
import com.alphalaneous.SilentDeletePrevCharAction;

import javax.swing.*;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.*;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class FancyTextArea extends JTextArea {


	private final UndoManager undoManager = new UndoManager();

	private static ArrayList<FancyTextArea> textAreas = new ArrayList<>();


	public FancyTextArea(boolean intFilter, boolean allowNegative, boolean allowDecimal) {
		createArea(intFilter, allowNegative, allowDecimal);
	}

	public FancyTextArea(boolean intFilter, boolean allowNegative) {
		createArea(intFilter, allowNegative, false);
	}

	private void createArea(boolean intFilter, boolean allowNegative, boolean allowDecimal){
		setOpaque(false);
		setBackground(Defaults.TEXT_BOX);
		setForeground(Defaults.FOREGROUND);
		setCaret(new MyCaret());
		setCaretColor(Defaults.FOREGROUND);
		setFont(Defaults.MAIN_FONT.deriveFont(14f));
		setSelectionColor(Defaults.ACCENT);
		getActionMap().put(DefaultEditorKit.deletePrevCharAction, new SilentDeletePrevCharAction());

		if(intFilter) {
			PlainDocument doc = (PlainDocument) getDocument();
			if(allowDecimal){
				doc.setDocumentFilter(new MyNegIntDecFilter());
			}
			else if(allowNegative){
				doc.setDocumentFilter(new MyNegIntFilter());
			}
			else {
				doc.setDocumentFilter(new MyIntFilter());
			}
		}
		addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				borderColor = Defaults.ACCENT;
				repaint();
			}

			@Override
			public void focusLost(FocusEvent e) {
				borderColor = new Color(102, 102, 102);
				repaint();
			}
		});

		Document doc = getDocument();
		UndoableEditListener undoableEditListener = e -> undoManager.addEdit(e.getEdit());
		doc.addUndoableEditListener(undoableEditListener);

		InputMap im = getInputMap(JComponent.WHEN_FOCUSED);
		ActionMap am = getActionMap();

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "Undo");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "Redo");
		//noinspection MagicConstant
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | Event.SHIFT_MASK), "Redo");

		am.put("Undo", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (undoManager.canUndo()) {
						undoManager.undo();
					}
				} catch (CannotUndoException exp) {
					exp.printStackTrace();
				}
			}
		});
		am.put("Redo", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (undoManager.canRedo()) {
						undoManager.redo();
					}
				} catch (CannotUndoException exp) {
					exp.printStackTrace();
				}
			}
		});
		textAreas.add(this);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		g.setColor(getBackground());

		RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHints(qualityHints);
		g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
		super.paintComponent(g);
	}

	private Color borderColor = new Color(102, 102, 102);

	@Override
	protected void paintBorder(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(borderColor);
		g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, getRadius(), getRadius());

	}

	public int getRadius() {
		return 10;
	}

	@Override
	public Insets getInsets() {
		int value = getRadius() / 2;
		return new Insets(value+1, value, value, value);
	}

	public void clearUndo(){
		undoManager.discardAllEdits();
	}

	public void refresh_(){
		setBackground(Defaults.TEXT_BOX);
		setForeground(Defaults.FOREGROUND);
		setCaretColor(Defaults.FOREGROUND);
	}

	public static void refreshAll(){
		for(FancyTextArea textArea : textAreas){
			textArea.refresh_();
		}
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
				g.drawString(mark, x-1, y + fm.getAscent()-1);
			}
		}

	}

	static class MyIntFilter extends DocumentFilter {
		@Override
		public void insertString(FilterBypass fb, int offset, String string,
								 AttributeSet attr) throws BadLocationException {

			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.insert(offset, string);

			if (test(sb.toString())) {
				super.insertString(fb, offset, string, attr);
			}
		}

		private boolean test(String text) {
			try {
				if(text.equalsIgnoreCase("")){
					return true;
				}
				if(text.contains("-")){
					return false;
				}
				Integer.parseInt(text);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text,
							AttributeSet attrs) throws BadLocationException {

			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.replace(offset, offset + length, text);

			if (test(sb.toString())) {
				super.replace(fb, offset, length, text, attrs);
			}

		}

		@Override
		public void remove(FilterBypass fb, int offset, int length)
				throws BadLocationException {

			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.delete(offset, offset + length);

			if (sb.toString().length() == 0) {
				super.replace(fb, offset, length, "", null);
			} else {
				if (test(sb.toString())) {
					super.remove(fb, offset, length);
				}
			}
		}
	}
	static class MyNegIntFilter extends DocumentFilter {
		@Override
		public void insertString(FilterBypass fb, int offset, String string,
								 AttributeSet attr) throws BadLocationException {

			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.insert(offset, string);

			if (test(sb.toString())) {
				super.insertString(fb, offset, string, attr);
			}
		}

		private boolean test(String text) {
			try {
				if(text.equalsIgnoreCase("")){
					return true;
				}
				if(text.equalsIgnoreCase("-")){
					text = text + "0";
				}
				Integer.parseInt(text);
				return true;
			} catch (NumberFormatException e) {
				return false;

			}
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text,
							AttributeSet attrs) throws BadLocationException {

			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.replace(offset, offset + length, text);

			if (test(sb.toString())) {
				super.replace(fb, offset, length, text, attrs);
			}

		}

		@Override
		public void remove(FilterBypass fb, int offset, int length)
				throws BadLocationException {

			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.delete(offset, offset + length);

			if (sb.toString().length() == 0) {
				super.replace(fb, offset, length, "", null);
			} else {
				if (test(sb.toString())) {
					super.remove(fb, offset, length);
				}
			}
		}
	}
	static class MyNegIntDecFilter extends DocumentFilter {
		@Override
		public void insertString(FilterBypass fb, int offset, String string,
								 AttributeSet attr) throws BadLocationException {

			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.insert(offset, string);

			if (test(sb.toString())) {
				super.insertString(fb, offset, string, attr);
			}
		}

		private boolean test(String text) {
			try {
				if(text.equalsIgnoreCase("")){
					return true;
				}
				if(text.equalsIgnoreCase("-") || text.equalsIgnoreCase(".")){
					text = text + "0";
				}
				Double.parseDouble(text);
				return true;
			} catch (NumberFormatException e) {
				return false;

			}
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text,
							AttributeSet attrs) throws BadLocationException {

			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.replace(offset, offset + length, text);

			if (test(sb.toString())) {
				super.replace(fb, offset, length, text, attrs);
			}

		}

		@Override
		public void remove(FilterBypass fb, int offset, int length)
				throws BadLocationException {

			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.delete(offset, offset + length);

			if (sb.toString().length() == 0) {
				super.replace(fb, offset, length, "", null);
			} else {
				if (test(sb.toString())) {
					super.remove(fb, offset, length);
				}
			}
		}
	}


}
