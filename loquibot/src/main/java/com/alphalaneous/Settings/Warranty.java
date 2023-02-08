package com.alphalaneous.Settings;

import com.alphalaneous.Swing.Components.SettingsPage;
import com.alphalaneous.Main;
import com.alphalaneous.Utils.Utilities;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

public class Warranty {

    private static final BufferedReader privacyFileReader = new BufferedReader(
            new InputStreamReader(Objects.requireNonNull(
                    Main.class.getClassLoader().getResourceAsStream("warranty.txt"))));

    public static JPanel createPanel() {
        SettingsPage settingsPage = new SettingsPage("$WARRANTY_SETTINGS$");
        String text = Utilities.readIntoString(privacyFileReader, true);
        settingsPage.addInput("", "", 20, null, text, false, false);

        return settingsPage;
    }
}
