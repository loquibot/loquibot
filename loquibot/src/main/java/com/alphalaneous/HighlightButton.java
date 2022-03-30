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
		highlightUI.setSelect(Defaults.COLOR6);
		highlightUI.setHover(Defaults.COLOR6);
		highlightUI.setBackground(Defaults.COLOR6);
		ImageIcon icon = new ImageIcon(image);
		setIcon(new ImageIcon(colorImage(convertToBufferedImage(icon), Defaults.FOREGROUND_A)));
		setBackground(Defaults.COLOR6);
		setUI(highlightUI);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				setIcon(new ImageIcon(colorImage(convertToBufferedImage(getIcon()), Defaults.ACCENT)));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setIcon(new ImageIcon(colorImage(convertToBufferedImage(getIcon()), Defaults.FOREGROUND_A)));
			}
		});
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setForeground(Defaults.FOREGROUND_A);
		setBorder(BorderFactory.createEmptyBorder());
		setOpaque(true);
		setPreferredSize(new Dimension(30, 25));
	}

	public HighlightButton(String text) {
		setText(text);
		setFont(Defaults.SYMBOLS.deriveFont(20f));
		highlightUI.setSelect(Defaults.COLOR6);
		highlightUI.setHover(Defaults.COLOR6);
		highlightUI.setBackground(Defaults.COLOR6);
		setBackground(Defaults.COLOR6);
		setUI(highlightUI);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				setForeground(Defaults.ACCENT);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setForeground(Defaults.FOREGROUND_A);
			}
		});
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setForeground(Defaults.FOREGROUND_A);
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
		setForeground(Defaults.FOREGROUND_A);
		setBackground(Defaults.COLOR6);
		highlightUI.setSelect(Defaults.COLOR6);
		highlightUI.setHover(Defaults.COLOR6);
		highlightUI.setBackground(Defaults.COLOR6);
		if (getIcon() != null) {
			setIcon(new ImageIcon(colorImage(convertToBufferedImage(getIcon()), Defaults.FOREGROUND_A)));
		}
	}
}
