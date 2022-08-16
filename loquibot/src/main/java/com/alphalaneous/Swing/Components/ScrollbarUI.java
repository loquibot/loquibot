package com.alphalaneous.Swing.Components;


import com.alphalaneous.Utils.Defaults;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class ScrollbarUI extends BasicScrollBarUI {

    public ScrollbarUI() {
        super();
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color color;
        JScrollBar sb = (JScrollBar) c;
        if (!sb.isEnabled() || r.width > r.height) {

            return;
        } else if (isDragging) {
            color = Defaults.COLOR2;
        } else if (isThumbRollover()) {
            color = Defaults.COLOR5;
        } else {
            color = Defaults.COLOR2;
        }
        g2.setPaint(color);
        g2.fillRoundRect(r.x, r.y + 5, r.width - 2, r.height - 10, 5, 5);
        g2.dispose();
    }

    @Override
    protected void setThumbBounds(int x, int y, int width, int height) {
        if(scrollbar != null) {
            super.setThumbBounds(x, y, width, height);
        }
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new Color(0,0,0,0));
        g2.fillRect(r.x, r.y, r.width, r.height);
        g2.dispose();
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return new InvisibleScrollBarButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return new InvisibleScrollBarButton();
    }

    @Override
    protected void layoutVScrollbar(JScrollBar sb)
    {
        Dimension sbSize = sb.getSize();
        Insets sbInsets = sb.getInsets();

        int itemW = sbSize.width - (sbInsets.left + sbInsets.right);
        int itemX = sbInsets.left;
        int decrButtonH = 0;
        int incrButtonH = 0;
        int decrButtonY = sbInsets.top;
        int incrButtonY = sbSize.height - (sbInsets.bottom + incrButtonH);
        int sbInsetsH = sbInsets.top + sbInsets.bottom;
        int sbButtonsH = 0;
        int gaps = decrGap + incrGap;

        float trackH = sbSize.height - (sbInsetsH + sbButtonsH) - gaps;

        float min = sb.getMinimum();
        float extent = sb.getVisibleAmount();
        float range = sb.getMaximum() - min;
        float value = scrollbar.getValue();

        int thumbH = (range <= 0)
                ? getMaximumThumbSize().height : (int)(trackH * (extent / range));
        thumbH = Math.max(thumbH, getMinimumThumbSize().height);
        thumbH = Math.min(thumbH, getMaximumThumbSize().height);

        int thumbY = incrButtonY - incrGap - thumbH;
        if (value < (sb.getMaximum() - sb.getVisibleAmount())) {
            float thumbRange = trackH - thumbH;
            thumbY = (int)(0.5f + (thumbRange * ((value - min) / (range - extent))));
            thumbY +=  decrButtonY + decrButtonH + decrGap;
        }

        int sbAvailButtonH = (sbSize.height - sbInsetsH);
        if (sbAvailButtonH < sbButtonsH) {
            incrButtonH = decrButtonH = sbAvailButtonH / 2;
            incrButtonY = sbSize.height - (sbInsets.bottom + incrButtonH);
        }
        try {
            if (decrButton != null && incrButton != null) {
                decrButton.setBounds(itemX, decrButtonY, itemW, decrButtonH);
                incrButton.setBounds(itemX, incrButtonY, itemW, incrButtonH);
            }
        }
        catch (NullPointerException ignored){}

        int itrackY = decrButtonY + decrButtonH + decrGap;
        int itrackH = incrButtonY - incrGap - itrackY;
        trackRect.setBounds(itemX, itrackY, itemW, itrackH);

        if(thumbH >= (int)trackH) {
            if (UIManager.getBoolean("ScrollBar.alwaysShowThumb")) {
                setThumbBounds(itemX, itrackY, itemW, itrackH);
            } else {
                setThumbBounds(0, 0, 0, 0);
            }
        }
        else {
            if ((thumbY + thumbH) > incrButtonY - incrGap) {
                thumbY = incrButtonY - incrGap - thumbH;
            }
            if (thumbY  < (decrButtonY + decrButtonH + decrGap)) {
                thumbY = decrButtonY + decrButtonH + decrGap + 1;
            }
            setThumbBounds(itemX, thumbY, itemW, thumbH);
        }
    }

    private static class InvisibleScrollBarButton extends JButton {
        private InvisibleScrollBarButton() {
            setOpaque(false);
            setFocusable(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setBorder(BorderFactory.createEmptyBorder());
        }
    }
}