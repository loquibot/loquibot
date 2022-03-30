package com.alphalaneous.ChatbotTab;

import com.alphalaneous.Components.ListView;
import com.alphalaneous.Components.TimerConfigCheckbox;

import javax.swing.*;

public class BlockedKeywords {

    private static final ListView listView = new ListView("$BLOCKED_KEYWORDS_SETTINGS$");

    public static JPanel createPanel(){

        listView.addButton("\uF0D1", null);
        return listView;
    }

}
