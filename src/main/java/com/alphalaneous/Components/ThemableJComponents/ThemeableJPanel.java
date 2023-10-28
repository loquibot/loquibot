package com.alphalaneous.Components.ThemableJComponents;

import javax.swing.*;
import java.awt.*;

public class ThemeableJPanel extends JPanel {

    Color prevBG;
    Color prevFG;

    String bgColorName;
    String fgColorName;

    public void setBackground(String colorName){
        this.bgColorName = colorName;
        setBackground(ThemeableColor.getColorByName(bgColorName));
    }

    public void setForeground(String colorName){
        this.fgColorName = colorName;
        setForeground(ThemeableColor.getColorByName(fgColorName));
    }

    @Override
    public void paintComponent(Graphics g){

        Color currentBGColor = ThemeableColor.getColorByName(bgColorName);
        Color currentFGColor = ThemeableColor.getColorByName(fgColorName);

        if(prevBG != currentBGColor) {
            prevBG = currentBGColor;
            setBackground(currentBGColor);
        }

        if(prevFG != currentFGColor) {
            prevFG = currentFGColor;
            setForeground(currentFGColor);
        }


        super.paintComponent(g);
    }
}
