package com.alphalaneous.Components.ThemableJComponents;

import com.alphalaneous.Components.JButtonUI;

import javax.swing.*;
import java.awt.*;

public class ThemeableJButton extends JButton {

    String bgNormalColorName;
    String fgNormalColorName;
    String hoverNormalColorName;
    String clickedNormalColorName;

    String bgSelectedColorName;
    String fgSelectedColorName;
    String hoverSelectedColorName;
    String clickedSelectedColorName;

    Color prevBG;
    Color prevFG;

    boolean isSelected = false;
    private final JButtonUI normalUI = new JButtonUI();
    private final JButtonUI disabledUI = new JButtonUI();
    private final JButtonUI selectUI = new JButtonUI();

    {
        setUI(normalUI);
    }

    public void setBackground(String normalBackground, String selectedBackground){
        this.bgNormalColorName = normalBackground;
        this.bgSelectedColorName = selectedBackground;
        setBackground(ThemeableColor.getColorByName(normalBackground));
    }

    public void setForeground(String normalForeground, String selectedForeground){
        this.fgNormalColorName = normalForeground;
        this.fgSelectedColorName = selectedForeground;
        setForeground(ThemeableColor.getColorByName(normalForeground));
    }

    public void setHoverColor(String normalHover, String selectedHover){
        this.hoverNormalColorName = normalHover;
        this.hoverSelectedColorName = selectedHover;
    }

    public void setClicked(String normalClicked, String selectedClicked){
        this.clickedNormalColorName = normalClicked;
        this.clickedSelectedColorName = selectedClicked;
    }

    @Override
    public void setSelected(boolean selected){
        isSelected = selected;
        if(selected) setUI(selectUI);
        else setUI(normalUI);
    }

    @Override
    public void setEnabled(boolean enabled){
        if(enabled){
            setUI(normalUI);
        }
        else{
            setUI(disabledUI);
        }
    }

    public boolean getSelected(){
        return isSelected;
    }

    @Override
    public void paintComponent(Graphics g){

        super.paintComponent(g);

        Color currentBGColor;
        Color currentFGColor;

        if(isSelected){
            currentBGColor = ThemeableColor.getColorByName(bgSelectedColorName);
            currentFGColor = ThemeableColor.getColorByName(fgSelectedColorName);
        }
        else{
            currentBGColor = ThemeableColor.getColorByName(bgNormalColorName);
            currentFGColor = ThemeableColor.getColorByName(fgNormalColorName);
        }

        if(prevBG != currentBGColor) {
            prevBG = currentBGColor;
            setBackground(currentBGColor);
        }

        if(prevFG != currentFGColor) {
            prevFG = currentFGColor;
            setForeground(currentFGColor);
        }

        normalUI.setColors(ThemeableColor.getColorByName(bgNormalColorName),
                ThemeableColor.getColorByName(hoverNormalColorName),
                ThemeableColor.getColorByName(clickedNormalColorName));

        selectUI.setColors(ThemeableColor.getColorByName(bgSelectedColorName),
                ThemeableColor.getColorByName(hoverSelectedColorName),
                ThemeableColor.getColorByName(clickedSelectedColorName));

        disabledUI.setColors(ThemeableColor.getColorByName(bgNormalColorName),
                ThemeableColor.getColorByName(bgNormalColorName),
                ThemeableColor.getColorByName(bgNormalColorName));

    }
}
