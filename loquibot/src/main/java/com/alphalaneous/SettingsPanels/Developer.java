package com.alphalaneous.SettingsPanels;

import com.alphalaneous.Swing.Components.SettingsPage;
import com.alphalaneous.ServerBot;

import javax.swing.*;

public class Developer {

    public static JPanel createPanel() {
        SettingsPage settingsPage = new SettingsPage("$DEVELOPER_SETTINGS$");

        settingsPage.addRadioOption("Server", "", new String[]{"main", "local"}, "dev_Server", "main", () -> {
            ServerBot.getCurrentServerBot().disconnect();
            new Thread(() -> new ServerBot().connect()).start();
        });


        return settingsPage;
    }

}
