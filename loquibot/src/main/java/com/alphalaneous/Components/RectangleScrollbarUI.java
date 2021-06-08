
package com.alphalaneous.Components;

import com.alphalaneous.Defaults;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.image.BufferedImage;

public class RectangleScrollbarUI extends BasicScrollBarUI {


    private final Image imageThumb;
    private final Image imageTrack;

    private final JButtonUI buttonUI = new JButtonUI();


    public RectangleScrollbarUI() {
        buttonUI.setBackground(Defaults.BUTTON);
        buttonUI.setHover(Defaults.BUTTON_HOVER);
        buttonUI.setSelect(Defaults.SELECT);
        imageThumb = FauxImage.create(32, 30, Defaults.BUTTON_HOVER);
        imageTrack = FauxImage.create(32, 30, Defaults.BUTTON);

    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {

        g.setColor(Defaults.BUTTON_HOVER);
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
        down.setBackground(Defaults.BUTTON);
        down.setPreferredSize(new Dimension(15, 15));
        down.setFont(Defaults.SYMBOLS.deriveFont(10f));
        down.setForeground(Defaults.FOREGROUND2);
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
        up.setBackground(Defaults.BUTTON);
        up.setPreferredSize(new Dimension(15, 15));
        up.setFont(Defaults.SYMBOLS.deriveFont(10f));
        up.setForeground(Defaults.FOREGROUND2);
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