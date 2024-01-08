package com.alphalaneous.Components;

import com.alphalaneous.Components.ThemableJComponents.ThemeableJButton;
import com.alphalaneous.Utilities.Fonts;
import com.alphalaneous.Utilities.GraphicsFunctions;

import javax.swing.*;
import java.awt.*;

public class RoundedButton extends ThemeableJButton {

    ImageIcon icon;
    int arc;
    public RoundedButton(String text){
        arc = 20;
        setHoverColor("list-hover-normal", "list-hover-selected");
        setClicked("list-clicked-normal", "list-clicked-selected");
        setBackground("list-background-normal", "list-background-selected");
        setForeground("list-foreground-normal", "list-foreground-selected");
        setMargin(new Insets(0, 0, 0, 0));
        setBorder(BorderFactory.createEmptyBorder());
        setForeground("foreground", "foreground");
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));
        setText(text);
        setOpaque(false);
    }

    public void setButtonIcon(ImageIcon icon){
        this.icon = icon;
        setIcon(icon);
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
