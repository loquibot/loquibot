package com.alphalaneous.ChatbotTab;

import com.alphalaneous.*;
import com.alphalaneous.Components.CommandConfigCheckbox;
import com.alphalaneous.Components.KeywordConfigCheckbox;
import com.alphalaneous.Components.ListView;
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
            System.out.println(keywordData.getKeyword());
            KeywordConfigCheckbox commandConfigCheckbox = new KeywordConfigCheckbox(keywordData);
            commandConfigCheckbox.resize(Window.getWindow().getWidth());
            listView.addElement(commandConfigCheckbox);
        }
        listView.updateUI();
    }
}
