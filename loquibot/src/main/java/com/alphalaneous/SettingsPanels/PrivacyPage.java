package com.alphalaneous.SettingsPanels;

import com.alphalaneous.Components.SettingsPage;
import com.alphalaneous.Main;
import com.alphalaneous.Utilities;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

public class PrivacyPage {

    private static final BufferedReader privacyFileReader = new BufferedReader(
            new InputStreamReader(Objects.requireNonNull(
                    Main.class.getClassLoader().getResourceAsStream("privacy.txt"))));

    public static JPanel createPanel() {
        SettingsPage settingsPage = new SettingsPage("$PRIVACY_SETTINGS$");
        String text = Utilities.readIntoString(privacyFileReader, true);
        settingsPage.addInput("", "", 20, null, text, false);

        return settingsPage;
    }
}
