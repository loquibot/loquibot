package com.alphalaneous.Components;

import com.alphalaneous.Components.ThemableJComponents.ThemeableJLabel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Utilities.Fonts;

import java.awt.*;

public class TitleSeparator extends ThemeableJPanel {

    public TitleSeparator(String title, boolean capitalize) {

        if(capitalize) title = title.toUpperCase();

        ThemeableJLabel label = new ThemeableJLabel("");
        label.setText(title);
        label.setFont(Fonts.getFont("Poppins-Regular").deriveFont(12f));
        label.setForeground("foreground-darker");
        label.setPreferredSize(new Dimension(200, 15));

        setLayout(new FlowLayout(FlowLayout.LEFT));
        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));
        add(label);
    }

}