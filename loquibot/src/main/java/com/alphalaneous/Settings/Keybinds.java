package com.alphalaneous.Settings;

import com.alphalaneous.Swing.Components.KeybindButton;
import com.alphalaneous.Swing.Components.SettingsComponent;
import com.alphalaneous.Swing.Components.SettingsPage;

import javax.swing.*;
import java.awt.*;

public class Keybinds {

    public static JPanel createPanel() {
        SettingsPage settingsPage = new SettingsPage("$SHORTCUTS_SETTINGS$");

        settingsPage.addComponent(createKeybindComponent(new KeybindButton("$OPEN_SHORTCUT$", "openKeybind")));
        settingsPage.addComponent(createKeybindComponent(new KeybindButton("$SKIP_SHORTCUT$", "skipKeybind")));
        settingsPage.addComponent(createKeybindComponent(new KeybindButton("$UNDO_SHORTCUT$", "undoKeybind")));
        settingsPage.addComponent(createKeybindComponent(new KeybindButton("$RANDOM_SHORTCUT$", "randomKeybind")));
        settingsPage.addComponent(createKeybindComponent(new KeybindButton("$COPY_SHORTCUT$", "copyKeybind")));
        settingsPage.addComponent(createKeybindComponent(new KeybindButton("$BLOCK_SHORTCUT$", "blockKeybind")));
        settingsPage.addComponent(createKeybindComponent(new KeybindButton("$CLEAR_SHORTCUT$", "clearKeybind")));

        return settingsPage;
    }

    private static SettingsComponent createKeybindComponent(KeybindButton button){
        return new SettingsComponent(button, new Dimension(475,30)){
            @Override
            protected void resizeComponent(Dimension dimension){
                button.resizeButton(dimension.width);
            }
            @Override
            protected void refreshUI(){
                button.refreshUI();
            }
        };
    }
}
