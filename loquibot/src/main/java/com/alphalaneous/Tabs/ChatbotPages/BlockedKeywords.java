package com.alphalaneous.Tabs.ChatbotPages;

import com.alphalaneous.Swing.Components.ListView;

import javax.swing.*;

public class BlockedKeywords {

    private static final ListView listView = new ListView("$BLOCKED_KEYWORDS_SETTINGS$");

    public static JPanel createPanel(){

        listView.addButton("\uF0D1", null);
        return listView;
    }

}
