package com.alphalaneous.Pages.SettingsSubPages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Components.ConfigCheckbox;
import com.alphalaneous.Components.EditCommandPanel;
import com.alphalaneous.Components.SettingsTitle;
import com.alphalaneous.Components.SmoothScrollPane;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Enums.UserLevel;
import com.alphalaneous.Interactive.Actions.ActionData;
import com.alphalaneous.Interactive.Commands.DefaultCommandData;
import com.alphalaneous.Interactive.CustomData;
import com.alphalaneous.Interactive.TwitchExclusive.BasicEvents.LoadBasicEvents;
import com.alphalaneous.Pages.SettingsPage;
import com.alphalaneous.Utilities.GraphicsFunctions;
import com.alphalaneous.Utilities.Utilities;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ChatSettingsPage {

    static SettingsSubPage page = new SettingsSubPage("$CHAT_TITLE$", true);
    static ThemeableJPanel commandsPanel = new ThemeableJPanel(){
        @Override
        public void paintComponent(Graphics g) {
            GraphicsFunctions.roundCorners(g, getBackground(), getSize(), 20);
            super.paintComponent(g);
        }
    };
    static ThemeableJPanel commandsChild = new ThemeableJPanel();
    static SmoothScrollPane smoothScrollPane = new SmoothScrollPane(commandsChild);
    static GridBagConstraints gbc = new GridBagConstraints();


    @OnLoad(order = 10005)
    public static void init(){

        page.addCheckbox("$DISABLE_MESSAGES$", "$DISABLE_MESSAGES_DESCRIPTION$", "disableMessages");
        page.addShortInput("$DEFAULT_COMMAND_PREFIX$", "$DEFAULT_COMMAND_PREFIX_DESCRIPTION$", "defaultCommandPrefix", "!", ChatSettingsPage::load);

        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        commandsPanel.setLayout(new BorderLayout());
        commandsPanel.setOpaque(false);
        commandsPanel.setBackground("background");
        commandsPanel.add(smoothScrollPane);

        smoothScrollPane.setOpaque(false);
        smoothScrollPane.getViewport().setOpaque(false);

        commandsChild.setOpaque(false);
        commandsChild.setBorder(new EmptyBorder(5, 5, 0, 5));
        commandsChild.setLayout(new BoxLayout(commandsChild, BoxLayout.Y_AXIS));

        page.addTitle("$DEFAULT_COMMANDS$", 20f);
        page.addComponent(commandsPanel);

        SettingsPage.addPage("$CHAT_TITLE$", "\uF162", page, ChatSettingsPage::load);

    }

    public static void load(){

        commandsChild.removeAll();
        for(DefaultCommandData commandData : DefaultCommandData.getRegisteredDefaultCommands()){
            ConfigCheckbox configCheckbox = new ConfigCheckbox(commandData, () -> showEditMenu(commandData), false, true);
            configCheckbox.setUserLevel(commandData.getUserLevel());

            commandsChild.add(configCheckbox, gbc);

            commandsChild.add(Box.createVerticalStrut(5), gbc);
        }
        commandsChild.updateUI();
        commandsChild.revalidate();
    }

    public static ThemeableJPanel getCommandsPanel(){
        return commandsPanel;
    }
    public static SmoothScrollPane getScrollPane(){
        return smoothScrollPane;
    }

    public static void showEditMenu(CustomData dataParam){

        String title = "$EDIT_DEFAULT_COMMAND$";

        EditCommandPanel editCommandPanel = new EditCommandPanel(title, dataParam, (kv, d, e) -> {

            DefaultCommandData data = (DefaultCommandData)d;
            Utilities.ifNotNull(kv.get("cooldown"), o -> data.setCooldown(Integer.parseInt((String) o)));
            Utilities.ifNotNull(kv.get("userlevel"), o -> data.setUserLevel(UserLevel.parse(Integer.parseInt((String) o))));
            data.save(true);
            e.close();
            load();
        }, true);

        editCommandPanel.removeDeleteButton();
        editCommandPanel.addDefaultCommandNameInput();
        editCommandPanel.addDefaultCommandDescription();
        editCommandPanel.addUserLevelsInput();
        editCommandPanel.addCooldownInput();
        editCommandPanel.setBounds(0,0,800,380);

        editCommandPanel.showMenu();
    }
}
