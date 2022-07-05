package com.alphalaneous.SettingsPanels;

import com.alphalaneous.Components.SettingsPage;
import com.alphalaneous.Defaults;
import com.alphalaneous.Settings;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OutputSettings {

    public static JPanel createPanel() {
        SettingsPage settingsPage = new SettingsPage("$OUTPUTS_SETTINGS$");
        settingsPage.addInput("$OUTPUTS_TEXT$", "", 7, "outputString", "Currently playing $(level 0 name) ($(level 0 id)) by $(level 0 author)!");
        settingsPage.addInput("$NO_LEVELS_TEXT$", "", 7, "noLevelsString", "There are no levels in the queue!");
        settingsPage.addInput("$FILE_LOCATION$", "", 2, "outputFileLocation", Paths.get(Defaults.saveDirectory + "\\loquibot").toString());
        return settingsPage;
    }

    public static void setOutputStringFile(String text) {
        Path file = Paths.get(Settings.getSettings("outputFileLocation").asString() + "\\output.txt");
        try {
            if (!Files.exists(file)) {
                Files.createFile(file);
            }
            Files.write(file, text.getBytes());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
