package com.alphalaneous.Components;

import com.alphalaneous.Components.ThemableJComponents.ThemeableJLabel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Fonts;

public class SettingsTitle extends ThemeableJPanel {


    public SettingsTitle(String title){
        setLayout(null);
        setOpaque(false);
        ThemeableJLabel settingsTitleLabel = new ThemeableJLabel(title);
        settingsTitleLabel.setFont(Fonts.getFont("Poppins-Regular").deriveFont(24f));
        settingsTitleLabel.setForeground("foreground");
        settingsTitleLabel.setBounds(25,20,500,50);
        setBounds(0,0, 500, 80);
        add(settingsTitleLabel);
    }
}
