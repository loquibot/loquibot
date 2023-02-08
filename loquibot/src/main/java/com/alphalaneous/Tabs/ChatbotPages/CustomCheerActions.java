package com.alphalaneous.Tabs.ChatbotPages;

import com.alphalaneous.Interactive.CheerActions.CheerActionData;
import com.alphalaneous.Interactive.CheerActions.LoadCheerActions;
import com.alphalaneous.Swing.Components.CheerActionConfigCheckbox;
import com.alphalaneous.Swing.Components.ListView;
import com.alphalaneous.Utils.Utilities;
import com.alphalaneous.Windows.Window;

import javax.swing.*;
import java.util.ArrayList;

public class CustomCheerActions {

    private static final ListView listView = new ListView("$CUSTOM_CHEER_ACTIONS_SETTINGS$");

    public static JPanel createPanel(){

        listView.addButton("\uF0D1", null);
        listView.addButton("\uF0D1", () -> CheerActionConfigCheckbox.openCheerActionSettings(true));
        return listView;
    }

    public static void loadCheerActions(){
        listView.clearElements();

        ArrayList<CheerActionData> keywords = new ArrayList<>(LoadCheerActions.getCustomCheerActions());

        ArrayList<CheerActionData> alphabetizedCommands = Utilities.alphabetizeCheerActionData(keywords);

        for(CheerActionData keywordData : alphabetizedCommands){
            CheerActionConfigCheckbox commandConfigCheckbox = new CheerActionConfigCheckbox(keywordData);
            commandConfigCheckbox.resize(Window.getWindow().getWidth());
            listView.addElement(commandConfigCheckbox);
        }
        listView.updateUI();
    }
}
