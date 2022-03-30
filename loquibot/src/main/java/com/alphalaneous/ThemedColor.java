package com.alphalaneous;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ThemedColor extends Color {

    private static final ArrayList<ThemedColor> themedColors = new ArrayList<>();

    public static final int FOREGROUND = 0;
    public static final int BACKGROUND = 1;

    private final String themeSetting;
    private final JComponent component;
    private final int type;

    private static final HashMap<String, Color> lightColors = new HashMap<>();
    private static final HashMap<String, Color> darkColors = new HashMap<>();

    static {
        lightColors.put("color", new Color(230, 230, 230));
        lightColors.put("color1", new Color(205, 205, 205));
        lightColors.put("color2", new Color(224, 224, 224));
        lightColors.put("color3", new Color(240, 240, 240));
        lightColors.put("color4", new Color(215, 215, 215));
        lightColors.put("color5", new Color(204, 204, 204));
        lightColors.put("color6", Color.WHITE);
        lightColors.put("color7", new Color(122, 122, 122,50));
        lightColors.put("forground_a", Color.BLACK);
        lightColors.put("forground_b", new Color(114, 114, 114));

        darkColors.put("color", new Color(31, 29, 46));
        darkColors.put("color1", new Color(47, 44, 66));
        darkColors.put("color2", new Color(39, 38, 59));
        darkColors.put("color3", new Color(23, 22, 35));
        darkColors.put("color4", new Color(45, 42, 66));
        darkColors.put("color5", new Color(58, 56, 80));
        darkColors.put("color6",new Color(8, 7, 20));
        darkColors.put("color7", new Color(161, 161, 250,50));
        darkColors.put("forground_a", Color.WHITE);
        darkColors.put("forground_b", new Color(165, 165, 165));
    }


    public static Color getThemeSetting(String themeSetting){
        Color themeColor;
        if(Defaults.isLight) themeColor = lightColors.get(themeSetting);
        else themeColor = darkColors.get(themeSetting);

        if(Settings.getSettings("theme").exists() && Settings.getSettings("theme").asString().equalsIgnoreCase("CUSTOM_MODE")){
            Color color = Themes.getThemeSettingNullable(themeSetting);
            if(color != null) themeColor = color;
        }
        return themeColor;
    }

    public ThemedColor(String themeSetting, JComponent component, int type) {

        super(getThemeSetting(themeSetting).getRGB());
        this.themeSetting = themeSetting;
        this.component = component;
        this.type = type;
        Color themeColor = getThemeSetting(themeSetting);

        component.setOpaque(themeColor.getAlpha() == 255);

        for(Component component1 : component.getComponents()){
            if(component1.getBackground().getAlpha() != 255){
                component.setOpaque(false);
                break;
            }
        }

        if(type == FOREGROUND) component.setForeground(themeColor);
        if(type == BACKGROUND) component.setBackground(themeColor);
        else throw new IllegalArgumentException("Invalid type: " + type);

        themedColors.add(this);

    }
    public void setThemeColor(){
        Color themeColor = getThemeSetting(themeSetting);

        if(type == FOREGROUND) component.setForeground(themeColor);
        if(type == BACKGROUND) component.setBackground(themeColor);

    }

    public static void setAllThemeColors(){
        for(ThemedColor themedColor : themedColors) themedColor.setThemeColor();
    }
}
