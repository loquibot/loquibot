
package com.alphalaneous.Swing.Components;

import com.alphalaneous.Utils.Defaults;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.image.BufferedImage;

public class RectangleScrollbarUI extends BasicScrollBarUI {


    private final Image imageThumb;
    private final Image imageTrack;

    private final JButtonUI buttonUI = new JButtonUI();

    public RectangleScrollbarUI() {
        buttonUI.setBackground(Defaults.COLOR2);
        buttonUI.setHover(Defaults.COLOR5);
        buttonUI.setSelect(Defaults.COLOR4);
        imageThumb = FauxImage.create(32, 30, Defaults.COLOR5);
        imageTrack = FauxImage.create(32, 30, Defaults.COLOR2);

    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {

        g.setColor(Defaults.COLOR5);
        g.drawImage(imageThumb,
                r.x, r.y, r.width, r.height, null);

    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        g.drawImage(imageTrack,
                r.x, r.y, r.width, r.height, null);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {

        JButton down = new JButton();
        if (orientation == SwingConstants.NORTH) {
            down.setText("\uE010");
        } else if (orientation == SwingConstants.WEST) {
            down.setText("\uE00E");
        }
        down.setUI(buttonUI);
        down.setBorder(BorderFactory.createEmptyBorder());
        down.setBackground(Defaults.COLOR2);
        down.setPreferredSize(new Dimension(15, 15));
        down.setFont(Defaults.SYMBOLS.deriveFont(10f));
        down.setForeground(Defaults.FOREGROUND_B);
        return down;
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        JButton up = new JButton();
        if (orientation == SwingConstants.SOUTH) {
            up.setText("\uE011");
        } else if (orientation == SwingConstants.EAST) {
            up.setText("\uE00F");
        }
        up.setUI(buttonUI);
        up.setBorder(BorderFactory.createEmptyBorder());
        up.setBackground(Defaults.COLOR2);
        up.setPreferredSize(new Dimension(15, 15));
        up.setFont(Defaults.SYMBOLS.deriveFont(10f));
        up.setForeground(Defaults.FOREGROUND_B);
        return up;
    }


    private static class FauxImage {

        static public Image create(int w, int h, Color c) {
            BufferedImage bi = new BufferedImage(
                    w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bi.createGraphics();
            g2d.setPaint(c);
            g2d.fillRect(0, 0, w, h);
            g2d.dispose();
            return bi;
        }
    }
}