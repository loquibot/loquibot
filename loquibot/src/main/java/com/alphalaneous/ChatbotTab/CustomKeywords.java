package com.alphalaneous.ChatbotTab;

import com.alphalaneous.Components.ListView;

import javax.swing.*;

public class CustomKeywords {

    private static final ListView listView = new ListView("$CUSTOM_KEYWORDS_SETTINGS$");

    public static JPanel createPanel(){

        listView.addButton("\uF0D1", null);
        return listView;
    }

}
