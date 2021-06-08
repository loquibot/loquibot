package com.alphalaneous;

import com.alphalaneous.Components.JButtonUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class HighlightButton extends JButton {

	private final JButtonUI highlightUI = new JButtonUI();

	public HighlightButton(Image image) {
		highlightUI.setSelect(Defaults.TOP);
		highlightUI.setHover(Defaults.TOP);
		highlightUI.setBackground(Defaults.TOP);
		ImageIcon icon = new ImageIcon(image);
		setIcon(new ImageIcon(colorImage(convertToBufferedImage(icon), Defaults.FOREGROUND)));
		setBackground(Defaults.TOP);
		setUI(highlightUI);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				setIcon(new ImageIcon(colorImage(convertToBufferedImage(getIcon()), Defaults.ACCENT)));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setIcon(new ImageIcon(colorImage(convertToBufferedImage(getIcon()), Defaults.FOREGROUND)));
			}
		});
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setForeground(Defaults.FOREGROUND);
		setBorder(BorderFactory.createEmptyBorder());
		setOpaque(true);
		setPreferredSize(new Dimension(30, 25));
	}

	public HighlightButton(String text) {
		setText(text);
		setFont(Defaults.SYMBOLS.deriveFont(20f));
		highlightUI.setSelect(Defaults.TOP);
		highlightUI.setHover(Defaults.TOP);
		highlightUI.setBackground(Defaults.TOP);
		setBackground(Defaults.TOP);
		setUI(highlightUI);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				setForeground(Defaults.ACCENT);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setForeground(Defaults.FOREGROUND);
			}
		});
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setForeground(Defaults.FOREGROUND);
		setBorder(BorderFactory.createEmptyBorder());
		setOpaque(true);
		setPreferredSize(new Dimension(getPreferredSize().width + 14, 25));
	}

	public static BufferedImage colorImage(BufferedImage image, Color color) {
		int width = image.getWidth();
		int height = image.getHeight();
		WritableRaster raster = image.getRaster();

		for (int xx = 0; xx < width; xx++) {
			for (int yy = 0; yy < height; yy++) {
				int[] pixels = raster.getPixel(xx, yy, (int[]) null);
				pixels[0] = color.getRed();
				pixels[1] = color.getGreen();
				pixels[2] = color.getBlue();
				raster.setPixel(xx, yy, pixels);
			}
		}
		return image;
	}

	public static BufferedImage convertToBufferedImage(Icon icon) {
		BufferedImage bi = new BufferedImage(
				icon.getIconWidth(),
				icon.getIconHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		icon.paintIcon(null, g, 0, 0);
		g.dispose();
		return bi;
	}

	public void refresh() {
		setForeground(Defaults.FOREGROUND);
		setBackground(Defaults.TOP);
		highlightUI.setSelect(Defaults.TOP);
		highlightUI.setHover(Defaults.TOP);
		highlightUI.setBackground(Defaults.TOP);
		if (getIcon() != null) {
			setIcon(new ImageIcon(colorImage(convertToBufferedImage(getIcon()), Defaults.FOREGROUND)));
		}
	}
}
