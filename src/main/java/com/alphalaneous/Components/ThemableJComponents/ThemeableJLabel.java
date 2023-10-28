package com.alphalaneous.Components.ThemableJComponents;

import javax.swing.*;
import java.awt.*;

public class ThemeableJLabel extends JLabel {

    Color prevBG;
    Color prevFG;
    public ThemeableJLabel(String text){
        super(text);
    }

    public ThemeableJLabel(){
    }

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
            updateUI();
        }

        if(prevFG != currentFGColor) {
            prevFG = currentFGColor;
            setForeground(currentFGColor);
            updateUI();
        }


        super.paintComponent(g);
    }
}
