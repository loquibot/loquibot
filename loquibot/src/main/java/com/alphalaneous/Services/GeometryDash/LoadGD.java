package com.alphalaneous.Services.GeometryDash;

import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Settings.Account;
import com.alphalaneous.Utils.Utilities;
import jdash.common.entity.GDUser;
import jdash.common.entity.GDUserProfile;

import javax.swing.*;
import java.util.Base64;

public class LoadGD {

    public static String username = "";
    public static volatile boolean isAuth = false;
    static boolean loaded = false;
    private static String password = "";
    private static Thread loadThread;
    private static Thread waitThread;

    public static void load() {
        loadThread = new Thread(() -> {
            if (SettingsHandler.getSettings("GDLogon").asBoolean()) {
                try {
                    username = SettingsHandler.getSettings("GDUsername").asString();
                    password = xor(new String(Base64.getDecoder().decode(SettingsHandler.getSettings("p").asString().getBytes())));
                    GDAPI.login(username, password);
                    isAuth = true;
                    Account.refreshGD(username);
                } catch (Exception e) {
                    e.printStackTrace();
                    SettingsHandler.writeSettings("GDLogon", "false");
                    isAuth = false;
                }
            } else {
                try {
                    GDAPI.getLevel(128); //"Warms up" the connection, so it doesn't hang longer than it should, rather have longer start time than not working when open.
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("> Failed to load initial GD connection");
                }
                SettingsHandler.writeSettings("GDLogon", "false");
                isAuth = false;
            }
            waitThread.stop();
            loaded = true;
            System.out.println("> LoadGD Loaded");
        });
        loadThread.start();
        waitThread = new Thread(() -> {
            Utilities.sleep(60000);
            loadThread.stop();
            SettingsHandler.writeSettings("GDLogon", "false");
            isAuth = false;
            JOptionPane.showMessageDialog(null, "Could not connect to GD, this could be for many reasons. The servers may be down, if not,\n - Try whitelisting board.exe in your Antivirus\n - Checking if your network is blocking http requests\n - Resetting the program by going to %appdata% > loquibot and deleting config.properties.", "Error Connecting to GD", JOptionPane.ERROR_MESSAGE);
            loaded = true;
        });
        waitThread.start();
    }

    private static String xor(String inputString) {

        StringBuilder outputString = new StringBuilder();

        int len = inputString.length();

        for (int i = 0; i < len; i++) {
            outputString.append((char) (inputString.charAt(i) ^ 15));
        }
        return outputString.toString();
    }
}
