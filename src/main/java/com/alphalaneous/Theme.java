package com.alphalaneous;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Components.ThemableJComponents.ThemeableColor;
import com.alphalaneous.FileUtils.FileList;
import com.alphalaneous.FileUtils.GetInternalFiles;
import com.alphalaneous.Utilities.Utilities;

import java.awt.*;

public class Theme {

    @OnLoad
    public static void init(){

        GetInternalFiles getInternalFiles = new GetInternalFiles("Themes/");
        FileList files = getInternalFiles.getFiles();

        String[] colors = files.getFile("default-theme.colors").getString().split("\n");

        for(String line : colors){

            try {
                String[] kv = line.split("=", 2);
                String name = kv[0].trim();
                String color = kv[1].trim();

                new ThemeableColor(name, Color.decode(color));

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        if(Utilities.isWindows()) {
            Color color = RegQuery.getColor();
            new ThemeableColor("accent", color);
            accentChangeListener();
        }
        else{
            new ThemeableColor("accent", new Color(66, 69, 255));
        }
    }


    public static void accentChangeListener(){
        if(Utilities.isWindows()) {
            new Thread(() -> {
                while (true) {
                    Color origColor = ThemeableColor.getColorByName("accent");

                    Color color = RegQuery.getColor();

                    if (!origColor.equals(color)) {
                        ThemeableColor.setColorByName("accent", color);
                    }
                    Utilities.sleep(1000);
                }
            }).start();
        }
    }
}
