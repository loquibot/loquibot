package com.alphalaneous.Components.ThemableJComponents;

import com.alphalaneous.Components.LightSliderUI;

import javax.swing.*;

public class ThemeableJSlider extends JSlider {

    ThemeableJSlider(){
        setUI(new LightSliderUI(this));
    }

}
