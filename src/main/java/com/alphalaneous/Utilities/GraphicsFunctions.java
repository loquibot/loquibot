package com.alphalaneous.Utilities;

import com.alphalaneous.Components.ThemableJComponents.ThemeableColor;

import java.awt.*;

public class GraphicsFunctions {

    public static void roundCorners(Graphics g, Color bg, Dimension d){
        roundCorners(g, bg, d, 20);
    }

    public static void roundCorners(Graphics g, Color bg, Dimension d, int arc){
        Graphics2D g2 = (Graphics2D) g;

        g.setColor(bg);

        RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHints(qualityHints);
        g2.fillRoundRect(0, 0, d.width, d.height, arc,  arc);
    }

    public static void dialog(Graphics g, Color bg, Dimension d){
        dialog(g, bg, ThemeableColor.getColorByName("accent"), d);
    }

    public static void dialog(Graphics g, Color bg, Color accent, Dimension d){
        Graphics2D g2 = (Graphics2D) g;
        RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHints(qualityHints);
        g.setColor(bg);
        g2.fillRoundRect(1, 1, d.width - 2, d.height - 2, 20, 20);
        g.setColor(accent);
        g2.drawRoundRect(0, 0, d.width - 1, d.height - 1, 20, 20);
    }
}
