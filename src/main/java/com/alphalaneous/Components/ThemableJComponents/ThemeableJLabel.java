package com.alphalaneous.Components.ThemableJComponents;

import com.alphalaneous.Utilities.Language;
import org.jdesktop.swingx.JXLabel;

import javax.swing.*;
import java.awt.*;

public class ThemeableJLabel extends JXLabel {

    Color prevBG;
    Color prevFG;

    String prevText = "";
    String text;

    public ThemeableJLabel(String text){

        super(Language.setLocale(text));
        this.text = text;

    }

    public ThemeableJLabel(ImageIcon icon){
        super(icon);
    }

    public ThemeableJLabel(){
    }

    String bgColorName;
    String fgColorName;

    @Override
    public void setText(String text){
        super.setText(Language.setLocale(text));
        this.text = text;
    }


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

        String currentText = Language.setLocale(text);

        if(!prevText.equalsIgnoreCase(currentText)){
            prevText = currentText;
            setText(text);
        }

        if(prevBG != currentBGColor) {
            prevBG = currentBGColor;
            setBackground(currentBGColor);
            revalidate();
        }

        if(prevFG != currentFGColor) {
            prevFG = currentFGColor;
            setForeground(currentFGColor);
            revalidate();
        }


        super.paintComponent(g);
    }
}
