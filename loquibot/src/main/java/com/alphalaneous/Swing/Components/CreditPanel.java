package com.alphalaneous.Swing.Components;

import com.alphalaneous.Images.Assets;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Utils.Utilities;
import org.imgscalr.Scalr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class CreditPanel extends JPanel {

    public CreditPanel(String name, String description, String link, ImageIcon icon, boolean isSmall){
        this(name, description, link, icon, isSmall, false);
    }
    public CreditPanel(String name, String description, String link, ImageIcon icon, boolean isSmall, boolean isGif){

        setLayout(null);
        setBackground(new Color(0,0,0,0));
        setOpaque(false);
        if(isSmall) setPreferredSize(new Dimension(400,80));
        else setPreferredSize(new Dimension(400,120));


        JLabel nameLabel = new JLabel(name);

        JLabel iconLabel;
        if(isGif){
            if (isSmall)
                iconLabel = new JLabel(new ImageIcon(icon.getImage()));
            else iconLabel = new JLabel(new ImageIcon(icon.getImage()));

        }
        else {
            if (isSmall)
                iconLabel = new JLabel(new ImageIcon(Scalr.resize(makeRoundedCorner(convertToBufferedImage(icon.getImage())), Scalr.Method.QUALITY, 60)));
            else iconLabel = new JLabel(new ImageIcon(makeRoundedCorner(convertToBufferedImage(icon.getImage()))));
        }
        iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        iconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    Utilities.openURL(new URI(link));
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });

        nameLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nameLabel.setForeground(Defaults.FOREGROUND_A);
        if(isSmall) nameLabel.setFont(Defaults.MAIN_FONT.deriveFont(20f));
        else nameLabel.setFont(Defaults.MAIN_FONT.deriveFont(30f));

        if(isSmall) nameLabel.setBounds(130, 10, 300, 40);
        else nameLabel.setBounds(130, 27, 300, 40);


        nameLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    Utilities.openURL(new URI(link));
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                Font originalFont = nameLabel.getFont();
                Map<TextAttribute, Integer> attributes = (Map<TextAttribute, Integer>) originalFont.getAttributes();
                attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                nameLabel.setFont(originalFont.deriveFont(attributes));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if(isSmall) nameLabel.setFont(Defaults.MAIN_FONT.deriveFont(20f));
                else nameLabel.setFont(Defaults.MAIN_FONT.deriveFont(30f));
            }
        });

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setForeground(Defaults.FOREGROUND_B);
        descriptionLabel.setFont(Defaults.MAIN_FONT.deriveFont(15f));

        if(isSmall) descriptionLabel.setBounds(130, 38, 300, 40);
        else descriptionLabel.setBounds(130, 62, 300, 40);
        if(isSmall) iconLabel.setBounds(40, 5, 80, 80);
        else iconLabel.setBounds(30, 20, 80, 80);


        add(iconLabel);
        add(nameLabel);
        add(descriptionLabel);

    }

    public static BufferedImage makeRoundedCorner(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();

        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new Ellipse2D.Double(0, 0, 80.0, 80.0));
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);

        g2.dispose();

        return output;
    }

    public static BufferedImage convertToBufferedImage(Image image) {
        BufferedImage newImage = new BufferedImage(
                image.getWidth(null), image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }
}
