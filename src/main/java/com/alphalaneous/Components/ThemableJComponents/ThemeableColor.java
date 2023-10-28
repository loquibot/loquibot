package com.alphalaneous.Components.ThemableJComponents;

import com.alphalaneous.Exceptions.ColorNotFoundException;

import java.awt.*;
import java.util.ArrayList;

public class ThemeableColor {

    public static ArrayList<ThemeableColor> registeredColors = new ArrayList<>();

    private String name;
    private Color color;

    public ThemeableColor(String name, Color color){
        this.name = name;
        this.color = color;
        registeredColors.add(this);
    }

    public Color getColor(){
        return color;
    }

    public void setColor(Color color){
        this.color = color;
    }

    public String getName(){
        return name;
    }

    public static void setColorByName(String name, Color color){
        registeredColors.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .ifPresentOrElse(themeableColor -> themeableColor.setColor(color), () -> {
                    throw new ColorNotFoundException(name);
                });
    }

    public static Color getColorByName(String name){
        return registeredColors.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .map(ThemeableColor::getColor)
                .orElse(Color.MAGENTA);

    }
}
