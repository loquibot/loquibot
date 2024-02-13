package com.alphalaneous.Components;

import com.alphalaneous.Components.ThemableJComponents.ThemeableColor;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;

import javax.swing.*;
import java.awt.*;

public class SettingsSeparator extends ThemeableJPanel {

    public SettingsSeparator(String text){
        this(text, true);
    }
    public SettingsSeparator(String text, boolean capitalize){

        TitleSeparator titleSeparator = new TitleSeparator(text, capitalize);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        setPreferredSize(new Dimension(100,30));

        add(titleSeparator);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g.setColor(ThemeableColor.getColorByName("foreground-darker"));
        g2.drawLine(5, 25, 165,25);
        super.paintComponent(g);
    }
}
