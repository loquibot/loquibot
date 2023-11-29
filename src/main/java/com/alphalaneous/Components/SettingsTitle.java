package com.alphalaneous.Components;

import com.alphalaneous.Components.ThemableJComponents.ThemeableJLabel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Utilities.Fonts;

public class SettingsTitle extends ThemeableJPanel {


    public SettingsTitle(String title){
        setOpaque(false);
        ThemeableJLabel settingsTitleLabel = new ThemeableJLabel(title);
        settingsTitleLabel.setFont(Fonts.getFont("Poppins-Regular").deriveFont(24f));
        settingsTitleLabel.setForeground("foreground");
        add(settingsTitleLabel);
    }
}
