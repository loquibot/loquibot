package com.alphalaneous.SettingsPanels;

import com.alphalaneous.Swing.Components.KeybindButton;
import com.alphalaneous.Swing.Components.SettingsComponent;
import com.alphalaneous.Swing.Components.SettingsPage;

import javax.swing.*;
import java.awt.*;

public class MediaShareShortcuts {

    public static JPanel createPanel() {
        SettingsPage settingsPage = new SettingsPage("$MEDIA_SHARE_SHORTCUTS_SETTINGS$");

        settingsPage.addComponent(createKeybindComponent(new KeybindButton("$MS_SKIP_SHORTCUT$", "mediaShareSkipKeybind")));
        settingsPage.addComponent(createKeybindComponent(new KeybindButton("$MS_UNDO_SHORTCUT$", "mediaShareUndoKeybind")));
        settingsPage.addComponent(createKeybindComponent(new KeybindButton("$MS_RANDOM_SHORTCUT$", "mediaShareRandomKeybind")));
        settingsPage.addComponent(createKeybindComponent(new KeybindButton("$MS_PAUSE_SHORTCUT$", "mediaSharePauseKeybind")));

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
