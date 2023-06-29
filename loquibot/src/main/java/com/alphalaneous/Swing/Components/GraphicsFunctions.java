package com.alphalaneous.Swing.Components;

import com.alphalaneous.Utils.Defaults;

import java.awt.*;

public class GraphicsFunctions {

    public static void roundCorners(Graphics g, Color bg, Dimension d){
        Graphics2D g2 = (Graphics2D) g;

        g.setColor(bg);

        RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHints(qualityHints);
        g2.fillRoundRect(0, 0, d.width, d.height, Defaults.globalArc,  Defaults.globalArc);
    }

    public static void roundCorners(Graphics g, Color bg, Dimension d, int arc){
        Graphics2D g2 = (Graphics2D) g;

        g.setColor(bg);

        RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHints(qualityHints);
        g2.fillRoundRect(0, 0, d.width, d.height, arc,  arc);
    }
    public static void roundedDialog(Graphics g, Color bg, Dimension d){
        Graphics2D g2 = (Graphics2D) g;
        RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHints(qualityHints);
        g.setColor(bg);
        g2.fillRoundRect(1, 1, d.width - 2, d.height - 2, Defaults.globalArc, Defaults.globalArc);
        g.setColor(Defaults.ACCENT);
        g2.drawRoundRect(0, 0, d.width - 1, d.height - 1, Defaults.globalArc, Defaults.globalArc);

    }

}
