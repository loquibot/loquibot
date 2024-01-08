package com.alphalaneous.Pages.InteractionPages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Components.ConfigCheckbox;
import com.alphalaneous.Components.EditCommandPanel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Interactive.CustomData;
import com.alphalaneous.Interactive.Keywords.KeywordData;
import com.alphalaneous.Interactive.TwitchExclusive.BasicEvents.LoadBasicEvents;
import com.alphalaneous.Pages.StreamInteractionsPage;
import com.alphalaneous.Pages.CommandPages.ChatPageComponent;
import com.alphalaneous.Utilities.Utilities;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BasicEventsPage {

    static ChatPageComponent page = new ChatPageComponent();
    static ThemeableJPanel buttonPanel = new ThemeableJPanel();
    static GridBagConstraints gbc = new GridBagConstraints();

    @OnLoad(order = 10)
    public static void init() {

        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridBagLayout());

        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        page.getContentPane().add(buttonPanel, BorderLayout.NORTH);

        StreamInteractionsPage.addPage("$BASIC_EVENTS_TITLE$", page, BasicEventsPage::load, () -> {});
    }

    public static void showEditMenu(){
        showEditMenu(new KeywordData(null));
    }
    public static void showEditMenu(CustomData dataParam){

        String title = "$EDIT_EVENT_ACTION$";

        EditCommandPanel editCommandPanel = new EditCommandPanel(title, dataParam, (kv, d, e) -> {

            Utilities.ifNotNull(kv.get("message"), o -> d.setMessage((String) o));
            d.save(true);
            e.close();

        });
        editCommandPanel.removeDeleteButton();
        editCommandPanel.addNameInput("$EVENT_ACTION_NAME_INPUT$", "$EVENT_ACTION_NAME_DESC$");
        editCommandPanel.addMessageInput();
        editCommandPanel.setBounds(0,0,800,380);

        editCommandPanel.showMenu();
    }


    public static void load(){

        StreamInteractionsPage.disableRightButton(true);
        StreamInteractionsPage.setRightButtonIcon("+");

        buttonPanel.removeAll();

        buttonPanel.add(new ConfigCheckbox(LoadBasicEvents.followData, () -> showEditMenu(LoadBasicEvents.followData), false), gbc);
        buttonPanel.add(Box.createVerticalStrut(5), gbc);
        buttonPanel.add(new ConfigCheckbox(LoadBasicEvents.subscribeData, () -> showEditMenu(LoadBasicEvents.subscribeData), false), gbc);
        buttonPanel.add(Box.createVerticalStrut(5), gbc);
        buttonPanel.add(new ConfigCheckbox(LoadBasicEvents.raidData, () -> showEditMenu(LoadBasicEvents.raidData), false), gbc);
        buttonPanel.add(Box.createVerticalStrut(5), gbc);
        buttonPanel.add(new ConfigCheckbox(LoadBasicEvents.cheerData, () -> showEditMenu(LoadBasicEvents.cheerData), false), gbc);
        buttonPanel.add(Box.createVerticalStrut(5), gbc);
        buttonPanel.add(new ConfigCheckbox(LoadBasicEvents.rewardData, () -> showEditMenu(LoadBasicEvents.rewardData), false), gbc);
        buttonPanel.add(Box.createVerticalStrut(5), gbc);
        buttonPanel.updateUI();
    }


}
