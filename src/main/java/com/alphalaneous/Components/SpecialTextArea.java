package com.alphalaneous.Components;

import com.alphalaneous.Components.ThemableJComponents.ThemeableColor;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableTextArea;
import com.alphalaneous.Utilities.Fonts;
import com.alphalaneous.Utilities.GraphicsFunctions;
import com.alphalaneous.Window;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;

public class SpecialTextArea extends ThemeableJPanel {

	private final UndoManager undoManager = new UndoManager();

	private final ThemeableTextArea textArea = new ThemeableTextArea(){
		@Override
		public Dimension getPreferredSize(){
			Dimension d = super.getPreferredSize();
			d = (d == null) ? new Dimension(400,400) : d;
			Insets insets = getInsets();

			if (getColumns() != 0) {
				d.width = Math.max(d.width, getColumns() * getColumnWidth() +
						insets.left + insets.right);
			}
			if (getRows() != 0) {
				d.height = Math.max(d.height, getRows() * getRowHeight() +
						insets.top + insets.bottom);
			}
			d.height -= 4;
			return d;
		}
	};
	private final SmoothScrollPane smoothScrollPane = new SmoothScrollPane(textArea);


	public SpecialTextArea(boolean intFilter, boolean allowNegative, boolean allowDecimal, int numLimit) {
		createArea(intFilter, allowNegative, allowDecimal, numLimit);
	}

	public SpecialTextArea(boolean intFilter, boolean allowNegative, boolean allowDecimal) {
		createArea(intFilter, allowNegative, allowDecimal, -1);
	}

	public SpecialTextArea(boolean intFilter, boolean allowNegative) {
		createArea(intFilter, allowNegative, false, -1);
	}

	private void createArea(boolean intFilter, boolean allowNegative, boolean allowDecimal, int numLimit){

		setBackground(ThemeableColor.getColorByName("list-background-normal"));
		setOpaque(false);
		textArea.setOpaque(false);
		smoothScrollPane.getViewport().setBackground(ThemeableColor.getColorByName("list-background-normal"));
		smoothScrollPane.setOpaque(false);
		smoothScrollPane.getViewport().setOpaque(false);
		textArea.setBackground(ThemeableColor.getColorByName("list-background-normal"));
		textArea.setOpaque(false);
		textArea.setForeground("foreground");
		textArea.setCaret(new MyCaret());
		textArea.setCaretColor(ThemeableColor.getColorByName("foreground"));
		textArea.setFont(Fonts.getFont("Poppins-Regular").deriveFont(16f));
		textArea.setSelectionColor(ThemeableColor.getColorByName("accent"));
		textArea.getActionMap().put(DefaultEditorKit.deletePrevCharAction, new SilentBackspace());
		if(intFilter) {
			PlainDocument doc = (PlainDocument) textArea.getDocument();
			if(allowDecimal){
				doc.setDocumentFilter(new MyNegIntDecFilter());
			}
			else if(allowNegative){
				doc.setDocumentFilter(new MyNegIntFilter());
			}
			else {
				MyIntFilter myIntFilter = new MyIntFilter();
				myIntFilter.setNumLimit(numLimit);
				doc.setDocumentFilter(myIntFilter);
			}
		}
		textArea.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if(customAccent != null){
					borderColor = customAccent;
				}
				else {
					borderColor = ThemeableColor.getColorByName("accent");
				}
				repaint();
			}

			@Override
			public void focusLost(FocusEvent e) {
				borderColor = ThemeableColor.getColorByName("foreground-darker");
				repaint();
			}
		});
		textArea.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				if(SwingUtilities.isRightMouseButton(e) && textArea.isEditable()){
					textArea.requestFocus();
					Window.addContextMenu(new TextContextMenu(textArea));
				}
			}
		});
		textArea.getDocument().addUndoableEditListener(evt -> undoManager.addEdit(evt.getEdit()));

		textArea.getActionMap().put("Undo", new AbstractAction("Undo") {
			public void actionPerformed(ActionEvent evt) {
				try {
					if (undoManager.canUndo()) {
						undoManager.undo();
					}
				} catch (CannotUndoException e) {
					e.printStackTrace();
				}
			}
		});
		textArea.getActionMap().put("Redo", new AbstractAction("Redo") {
			public void actionPerformed(ActionEvent evt) {
				try {
					if (undoManager.canRedo()) {
						undoManager.redo();
					}
				} catch (CannotRedoException e) {
					e.printStackTrace();
				}
			}
		});

		// Create keyboard accelerators for undo/redo actions (Ctrl+Z/Ctrl+Y)
		textArea.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "Undo");
		textArea.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "Redo");
		textArea.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK), "Redo");
		setLayout(null);
		add(smoothScrollPane);
	}

	private Color customAccent = null;


	public void setCustomAccent(Color customAccent){
		this.customAccent = customAccent;
		if(textArea != null) textArea.setSelectionColor(customAccent);
	}

	public void setBounds(int x, int y, int width, int height){
		super.setBounds(x, y, width, height);
		smoothScrollPane.setBounds(4, 4, width-8, height-8);
	}

	@Override
	public void setDropTarget(DropTarget dt){
		textArea.setDropTarget(dt);
	}

	public JTextArea getTextInput(){
		return textArea;
	}

	public void setFont(Font font){
		if(textArea != null) textArea.setFont(font);
	}

	public void addFocusListener(FocusListener listener){
		if(textArea != null){
			textArea.addFocusListener(listener);
		}
	}

	public Document getDocument(){
		return textArea.getDocument();
	}
	public void setDocument(Document document){
		textArea.setDocument(document);
	}

	public String getText(){
		return textArea.getText();
	}
	public void setLineWrap(boolean lineWrap){
		textArea.setLineWrap(lineWrap);
	}
	public void setWrapStyleWord(boolean wrap){
		textArea.setWrapStyleWord(wrap);
	}
	public void setText(String text){
		textArea.setText(text);
	}
	public void setEditable(boolean editable){
		textArea.setEditable(editable);
	}

	@Override
	public void paintComponent(Graphics g) {
		GraphicsFunctions.roundCorners(g, ThemeableColor.getColorByName("list-background-normal"), getSize());
		super.paintComponent(g);
	}

	private Color borderColor = ThemeableColor.getColorByName("foreground-darker");

	@Override
	protected void paintBorder(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


		g2.setColor(borderColor);
		g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, getRadius()+6, getRadius()+6);
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
				int value = Integer.parseInt(text);
				if(numLimit != -1){
					return value <= numLimit;
				}
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
		private int numLimit = -1;
		public void setNumLimit(int numLimit){
			this.numLimit = numLimit;
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

			if (sb.toString().isEmpty()) {
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

			if (sb.toString().isEmpty()) {
				super.replace(fb, offset, length, "", null);
			} else {
				if (test(sb.toString())) {
					super.remove(fb, offset, length);
				}
			}
		}
	}
}
