package com.alphalaneous.Pages.CommandPages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Components.ConfigCheckbox;
import com.alphalaneous.Components.EditCommandPanel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Enums.UserLevel;
import com.alphalaneous.Interactive.Commands.CommandData;
import com.alphalaneous.Interactive.Keywords.KeywordData;
import com.alphalaneous.Pages.ChatPage;
import com.alphalaneous.Utilities.Utilities;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class KeywordsPage {

    static ChatPageComponent page = new ChatPageComponent();
    static ThemeableJPanel buttonPanel = new ThemeableJPanel();
    static GridBagConstraints gbc = new GridBagConstraints();

    @OnLoad(order = 8)
    public static void init() {

        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridBagLayout());

        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        page.getContentPane().add(buttonPanel, BorderLayout.NORTH);

        ChatPage.addPage("$KEYWORDS_TITLE$", page, KeywordsPage::load, KeywordsPage::showEditMenu);
    }

    public static void showEditMenu(){
        showEditMenu(new KeywordData(null));
    }
    public static void showEditMenu(KeywordData dataParam){

        String title = "$EDIT_KEYWORD$";

        if(dataParam.getName() == null) title = "$ADD_KEYWORD$";

        EditCommandPanel editCommandPanel = new EditCommandPanel(title, dataParam, (kv, d, e) -> {

            KeywordData data;

            AtomicBoolean hasName = new AtomicBoolean(false);

            KeywordData.getRegisteredKeywords().forEach(c -> {
                if(dataParam.getName() == null && kv.get("name").equalsIgnoreCase(c.getName())) hasName.set(true);
            });

            if(hasName.get()){
                e.setTitleLabelError(true);
            }
            else {
                if (d.getName() == null) {
                    data = new KeywordData(kv.get("name"));
                    data.register();
                } else {
                    data = (KeywordData) d;
                }

                Utilities.ifNotNull(kv.get("message"), o -> data.setMessage((String) o));
                Utilities.ifNotNull(kv.get("userlevel"), o -> data.setUserLevel(UserLevel.parse(Integer.parseInt((String) o))));
                Utilities.ifNotNull(kv.get("cooldown"), o -> data.setCooldown(Integer.parseInt((String) o)));
                data.setName(kv.get("name"));

                data.save(true);
                e.close();
            }
        });
        editCommandPanel.addNameInput("$KEYWORD_NAME_INPUT$", "$KEYWORD_NAME_DESC$");
        editCommandPanel.addMessageInput();
        editCommandPanel.addUserLevelsInput();
        editCommandPanel.addCooldownInput();
        editCommandPanel.setBounds(0,0,800,470);

        editCommandPanel.showMenu();
    }

    public static void load(){

        buttonPanel.removeAll();
        for(KeywordData keywordData : KeywordData.getRegisteredKeywords()){

            buttonPanel.add(new ConfigCheckbox(keywordData, () -> showEditMenu(keywordData), false), gbc);
            buttonPanel.add(Box.createVerticalStrut(5), gbc);
        }
        buttonPanel.updateUI();
    }
}
