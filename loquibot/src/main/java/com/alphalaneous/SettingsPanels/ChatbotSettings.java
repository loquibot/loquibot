package com.alphalaneous.SettingsPanels;

import com.alphalaneous.Components.KeybindButton;
import com.alphalaneous.Components.SettingsComponent;
import com.alphalaneous.Components.SettingsPage;
import com.alphalaneous.Components.SmallInputButton;

import javax.swing.*;
import java.awt.*;

public class ChatbotSettings {

    public static JPanel createPanel() {
        SettingsPage settingsPage = new SettingsPage("$CHATBOT_SETTINGS$");
        settingsPage.addCheckbox("$SILENT_MODE$", "$SILENT_MODE_DESC$", "silentMode");
        settingsPage.addCheckbox("$MULTI_THREAD$", "$MULTI_THREAD_DESC$", "multiMode", true, null);
        settingsPage.addCheckbox("$ANTI_DOX$", "$ANTI_DOX_DESC$", "antiDox", true, null);
        settingsPage.addComponent(createKeybindComponent(new SmallInputButton("$DEFAULT_COMMAND_PREFIX$", "defaultCommandPrefix", "!")));
        settingsPage.addComponent(createKeybindComponent(new SmallInputButton("$GEOMETRY_DASH_COMMAND_PREFIX$", "geometryDashCommandPrefix", "!")));

        return settingsPage;
    }



    private static SettingsComponent createKeybindComponent(SmallInputButton button){
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
