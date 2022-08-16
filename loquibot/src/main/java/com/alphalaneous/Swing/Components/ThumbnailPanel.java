package com.alphalaneous.Swing.Components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class ThumbnailPanel extends JPanel {

    private BufferedImage image;

    public void setImage(BufferedImage image){
        this.image = image;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Shape clipShape = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20);

        BufferedImage bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bg = bi.createGraphics();
        Graphics2D g2 = (Graphics2D) g;
        bg.setComposite(AlphaComposite.Clear);
        bg.fillRect(0, 0, getWidth(), getHeight());
        bg.setComposite(AlphaComposite.SrcOver);
        bg.drawImage(image, 0, 0, getWidth(), getHeight(), null);

        g2.setPaint(new TexturePaint(bi, new Rectangle2D.Float(0, 0, getWidth(), getHeight())));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fill(clipShape);
        g2.setPaint(null);

    }
}
