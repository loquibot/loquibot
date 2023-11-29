package com.alphalaneous.Utilities;

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


    public static int getTheme() {
        if(SettingsHandler.getSettings("noReg").asBoolean()) return -1;
        String result = runRegQuery(PERSONALIZE, REGDWORD_TOKEN);
        if(result == null) return -1;
        String a = result.substring("0x".length());
        return hex2decimal(a);
    }

    public static Color getColor() {
        if(SettingsHandler.getSettings("noReg").asBoolean()) new Color(66, 69, 255);
        String result = runRegQuery(SYSTEM_COLOR, REGDWORD_TOKEN);
        if(result == null) return new Color(66, 69, 255);
        else {
            String a = result.substring("0x".length());
            return Color.decode(String.valueOf(hex2decimal(a)));
        }

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
