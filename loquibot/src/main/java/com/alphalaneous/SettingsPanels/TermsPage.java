package com.alphalaneous.SettingsPanels;

import com.alphalaneous.Components.SettingsPage;
import com.alphalaneous.Main;
import com.alphalaneous.Utilities;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

public class TermsPage {

    private static final BufferedReader tosFileReader = new BufferedReader(
            new InputStreamReader(Objects.requireNonNull(
                    Main.class.getClassLoader().getResourceAsStream("terms.txt"))));

    public static JPanel createPanel() {
        SettingsPage settingsPage = new SettingsPage("$TERMS_SETTINGS$");
        String text = Utilities.readIntoString(tosFileReader, true);
        settingsPage.addInput("", "", 20, null, text, false);

        return settingsPage;
    }
}