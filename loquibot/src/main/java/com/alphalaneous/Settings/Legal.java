package com.alphalaneous.Settings;

import com.alphalaneous.Swing.Components.SettingsPage;
import com.alphalaneous.Main;
import com.alphalaneous.Utils.Utilities;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

public class Legal {

    private static final BufferedReader legalFileReader = new BufferedReader(
            new InputStreamReader(Objects.requireNonNull(
                    Main.class.getClassLoader().getResourceAsStream("legal.txt"))));

    public static JPanel createPanel() {
        SettingsPage settingsPage = new SettingsPage("$LEGAL_SETTINGS$");
        String text = Utilities.readIntoString(legalFileReader, true);
        settingsPage.addInput("", "", 20, null, text, false, false);

        return settingsPage;
    }
}
