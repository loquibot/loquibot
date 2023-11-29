package com.alphalaneous.Utilities;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.FileUtils.FileList;
import com.alphalaneous.FileUtils.GetInternalFiles;
import com.alphalaneous.FileUtils.InternalFile;

import java.awt.*;
import java.util.HashMap;

public class Fonts {

    private static final HashMap<String, Font> fonts = new HashMap<>();

    @OnLoad
    public static void init(){

        GetInternalFiles getInternalFiles = new GetInternalFiles("Fonts/");
        FileList files = getInternalFiles.getFiles();

        for(InternalFile file : files){

            String name = file.getName().split("\\.")[0];
            if(!name.equals("Fonts")) {
                try {
                    Font font = file.getFont();
                    GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
                    fonts.put(name, font);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public static Font getFont(String name){

        return fonts.get(name);

    }
}
