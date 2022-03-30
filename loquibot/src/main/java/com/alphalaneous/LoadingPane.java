package com.alphalaneous;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;

public class LoadingPane extends JPanel {

    private double angle;
    private double extent;

    private double angleDelta = -1;
    private double extentDelta = -5;

    private boolean flip = false;

    public LoadingPane() {
        setOpaque(false);
        setBackground(new Color(0,0,0,0));
        Timer timer = new Timer(10, e -> {
            angle += angleDelta;
            extent += extentDelta;
            if (Math.abs(extent) % 360.0 == 0) {
                angle = angle - extent;
                flip = !flip;
                if (flip) {
                    extent = 360.0;
                } else {
                    extent = 0.0;
                }
            }
            repaint();
        });
        timer.start();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        Arc2D.Double arc = new Arc2D.Double(5, 5, 50, 50, angle, extent, Arc2D.OPEN);
        BasicStroke stroke = new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
        g2d.setStroke(stroke);
        g2d.setColor(Defaults.FOREGROUND_A);
        g2d.draw(arc);
        g2d.dispose();
    }

}
