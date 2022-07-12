package com.alphalaneous.Swing.Components;

import com.alphalaneous.Defaults;
import com.alphalaneous.Theming.ThemedColor;

import javax.swing.*;
import java.awt.*;

import static com.alphalaneous.Defaults.settingsButtonUI;

public class ListButton extends CurvedButton{

    public ListButton(String label, int width) {
        super(label);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBackground(new ThemedColor("color2", this, ThemedColor.BACKGROUND));
        setOpaque(false);

        setUI(settingsButtonUI);
        setForeground(Defaults.FOREGROUND_A);
        setBorder(BorderFactory.createEmptyBorder());
        setFont(Defaults.MAIN_FONT.deriveFont(14f));
        setPreferredSize(new Dimension(width, 35));
        refresh();
    }
}
