package com.alphalaneous.Tabs.ChatbotPages;

import com.alphalaneous.Interactive.Keywords.LoadKeywords;
import com.alphalaneous.Swing.Components.KeywordConfigCheckbox;
import com.alphalaneous.Swing.Components.ListView;
import com.alphalaneous.Interactive.Keywords.KeywordData;
import com.alphalaneous.Utils.Utilities;
import com.alphalaneous.Windows.Window;

import javax.swing.*;
import java.util.ArrayList;

public class CustomKeywords {

    private static final ListView listView = new ListView("$CUSTOM_KEYWORDS_SETTINGS$");

    public static JPanel createPanel(){

        listView.addButton("\uF0D1", null);
        listView.addButton("\uF0D1", () -> KeywordConfigCheckbox.openKeywordSettings(true));
        return listView;
    }

    public static void loadKeywords(){
        listView.clearElements();

        ArrayList<KeywordData> keywords = new ArrayList<>(LoadKeywords.getCustomKeywords());

        ArrayList<KeywordData> alphabetizedCommands = Utilities.alphabetizeKeywordData(keywords);

        for(KeywordData keywordData : alphabetizedCommands){
            KeywordConfigCheckbox commandConfigCheckbox = new KeywordConfigCheckbox(keywordData);
            commandConfigCheckbox.resize(Window.getWindow().getWidth());
            listView.addElement(commandConfigCheckbox);
        }
        listView.updateUI();
    }
}
