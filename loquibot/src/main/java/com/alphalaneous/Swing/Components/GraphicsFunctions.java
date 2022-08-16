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
}
