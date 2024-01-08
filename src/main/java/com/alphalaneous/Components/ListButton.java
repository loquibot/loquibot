package com.alphalaneous.Components;

import com.alphalaneous.Components.ThemableJComponents.ThemeableJButton;
import com.alphalaneous.Utilities.GraphicsFunctions;

import javax.swing.*;
import java.awt.*;

public class ListButton extends ThemeableJButton {

    private int arc = 20;

    public ListButton(String text) {

        setHoverColor("list-hover-normal", "list-hover-selected");
        setClicked("list-clicked-normal", "list-clicked-selected");
        setBackground("list-background-normal", "list-background-selected");
        setForeground("list-foreground-normal", "list-foreground-selected");
        setPreferredSize(new Dimension(52,52));
        setMaximumSize(new Dimension(52, 52));
        setMargin(new Insets(0, 0, 0, 0));
        setBorder(BorderFactory.createEmptyBorder());
        setForeground("foreground", "foreground");
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder());
        if(text != null) setText(text);
        setOpaque(false);
    }

    public void setArc(int arc){
        this.arc = arc;
    }

    @Override
    public void paintComponent(Graphics g) {
        GraphicsFunctions.roundCorners(g, getBackground(), getSize(), arc);
        super.paintComponent(g);
    }
}