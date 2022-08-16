package com.alphalaneous.Utils;

import com.alphalaneous.Settings.SettingsHandler;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegQuery {

    private static final String REGQUERY_UTIL = "reg query ";
    private static final String REGSTR_TOKEN = "REG_SZ";
    private static final String REGDWORD_TOKEN = "REG_DWORD";

    private static final String PERSONALIZE = REGQUERY_UTIL +
            "\"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize\"" + " /v AppsUseLightTheme";
    private static final String SYSTEM_COLOR = REGQUERY_UTIL +
            "\"HKCU\\Software\\Microsoft\\Windows\\DWM\""
            + " /v ColorizationColor";
    private static final String WALLPAPER = REGQUERY_UTIL +
            "\"HKEY_CURRENT_USER\\Control Panel\\Desktop\""
            + " /v WallPaper";
    private static final String WALLPAPER_COLOR = REGQUERY_UTIL +
            "\"HKEY_CURRENT_USER\\Control Panel\\Colors\""
            + " /v Background";

    public static Color getWallpaperColor(){
        if(SettingsHandler.getSettings("noReg").asBoolean()) return new Color(70, 86, 255);
        try {
            Process process = Runtime.getRuntime().exec(WALLPAPER_COLOR);
            StreamReader reader = new StreamReader(process.getInputStream());

            reader.start();
            process.waitFor();
            reader.join();

            String result = reader.getResult();
            int p = result.indexOf(REGSTR_TOKEN);

            if (p == -1) {
                System.out.println(p);
                return null;
            }

            String[] color = result.substring(p + REGSTR_TOKEN.length()).trim().split(" ");

            return new Color(Integer.parseInt(color[0]),Integer.parseInt(color[1]),Integer.parseInt(color[2]));
        }
        catch (Exception e){
            e.printStackTrace();
            return new Color(0,0,0);
        }
    }

    public static String getWallpaperLocation(){
        if(SettingsHandler.getSettings("noReg").asBoolean()) return "";
        try {
            Process process = Runtime.getRuntime().exec(WALLPAPER);
            StreamReader reader = new StreamReader(process.getInputStream());

            reader.start();
            process.waitFor();
            reader.join();

            String result = reader.getResult();
            int p = result.indexOf(REGSTR_TOKEN);
            if (p == -1) {
                return null;
            }
            return result.substring(p + REGSTR_TOKEN.length()).trim();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    public static int getTheme() {

        if(SettingsHandler.getSettings("noReg").asBoolean()) return -1;
        try {
            Process process = Runtime.getRuntime().exec(PERSONALIZE);
            StreamReader reader = new StreamReader(process.getInputStream());

            reader.start();
            process.waitFor();
            reader.join();

            String result = reader.getResult();
            int p = result.indexOf(REGDWORD_TOKEN);

            if (p == -1)
                return -1;

            String temp = result.substring(p + REGDWORD_TOKEN.length()).trim();
            String a = temp.substring("0x".length());
            return hex2decimal(a);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getColor() {
        if(SettingsHandler.getSettings("noReg").asBoolean()) return -1;
        try {
            Process process = Runtime.getRuntime().exec(SYSTEM_COLOR);
            StreamReader reader = new StreamReader(process.getInputStream());

            reader.start();
            process.waitFor();
            reader.join();

            String result = reader.getResult();

            int p = result.indexOf(REGDWORD_TOKEN);
            if (p == -1)
                return -1;
            String temp = result.substring(p + REGDWORD_TOKEN.length()).trim();
            String a = temp.substring("0x".length());
            return hex2decimal(a);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    public static int hex2decimal(String s) {
        return new BigInteger(s, 16).intValue();
    }
    static class StreamReader extends Thread {
        private InputStream is;
        private StringWriter sw;

        StreamReader(InputStream is) {
            this.is = is;
            sw = new StringWriter();
        }

        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1)
                    sw.write(c);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String getResult() {
            return sw.toString();
        }
    }
}
