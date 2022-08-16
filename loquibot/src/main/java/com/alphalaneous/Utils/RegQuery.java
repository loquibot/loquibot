package com.alphalaneous.Utils;

import com.alphalaneous.Settings.SettingsHandler;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigInteger;

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

    public static String runRegQuery(String query, String token){
        try {
            Process process = Runtime.getRuntime().exec(query);
            StreamReader reader = new StreamReader(process.getInputStream());

            reader.start();
            process.waitFor();
            reader.join();

            String result = reader.getResult();
            int p = result.indexOf(token);

            if (p == -1) {
                return null;
            }
            return result.substring(p + token.length()).trim();
        }
        catch (Exception e){
            return null;
        }
    }

    public static Color getWallpaperColor(){
        if(SettingsHandler.getSettings("noReg").asBoolean()) return new Color(70, 86, 255);

        String result = runRegQuery(WALLPAPER_COLOR, REGSTR_TOKEN);
        if(result == null) return new Color(0,0,0);
        String[] color = result.split(" ");
        return new Color(Integer.parseInt(color[0]),Integer.parseInt(color[1]),Integer.parseInt(color[2]));


    }

    public static String getWallpaperLocation(){
        if(SettingsHandler.getSettings("noReg").asBoolean()) return "";
        return runRegQuery(WALLPAPER, REGSTR_TOKEN);
    }

    public static int getTheme() {
        if(SettingsHandler.getSettings("noReg").asBoolean()) return -1;
        String result = runRegQuery(PERSONALIZE, REGDWORD_TOKEN);
        if(result == null) return -1;
        String a = result.substring("0x".length());
        return hex2decimal(a);
    }

    public static int getColor() {
        if(SettingsHandler.getSettings("noReg").asBoolean()) return -1;
        String result = runRegQuery(SYSTEM_COLOR, REGDWORD_TOKEN);
        if(result == null) return -1;

        String a = result.substring("0x".length());
        return hex2decimal(a);

    }
    public static int hex2decimal(String s) {
        return new BigInteger(s, 16).intValue();
    }
    static class StreamReader extends Thread {
        private final InputStream is;
        private final StringWriter sw;

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
