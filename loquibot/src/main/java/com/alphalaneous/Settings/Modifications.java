package com.alphalaneous.Settings;

import com.alphalaneous.Main;
import com.alphalaneous.Swing.Components.KeybindButton;
import com.alphalaneous.Swing.Components.SettingsComponent;
import com.alphalaneous.Swing.Components.SettingsPage;
import com.alphalaneous.Utils.MemoryHelper;
import com.alphalaneous.Utils.RegQuery;
import com.alphalaneous.Utils.Utilities;
import com.alphalaneous.Windows.DialogBox;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.Objects;

public class Modifications {

    public static JPanel createPanel() {

        SettingsPage settingsPage = new SettingsPage("$MODS_SETTINGS$");
        settingsPage.addButton("Get Integrated Loquibot on Geode", Modifications::getGeode);
        return settingsPage;
    }

    public static void getGeode(){
        Utilities.openURL(URI.create("https://geode-sdk.org"));
    }

}
