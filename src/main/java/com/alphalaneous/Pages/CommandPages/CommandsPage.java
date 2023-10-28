package com.alphalaneous.Pages.CommandPages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Components.ConfigCheckbox;
import com.alphalaneous.Components.EditCommandPanel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Enums.UserLevel;
import com.alphalaneous.Interactive.Commands.CommandData;
import com.alphalaneous.Pages.ChatPage;
import com.alphalaneous.Utilities.Utilities;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;

public class CommandsPage {

    static ChatPageComponent page = new ChatPageComponent();
    static ThemeableJPanel buttonPanel = new ThemeableJPanel();
    static GridBagConstraints gbc = new GridBagConstraints();

    @OnLoad(order = 7)
    public static void init() {

        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridBagLayout());

        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        page.getContentPane().add(buttonPanel, BorderLayout.NORTH);

        ChatPage.addPage("Commands", page, CommandsPage::load, CommandsPage::showEditMenu);
    }


    public static void load(){

        buttonPanel.removeAll();
        for(CommandData commandData : CommandData.getRegisteredCommands()){

            buttonPanel.add(new ConfigCheckbox(commandData, () -> showEditMenu(commandData), false), gbc);
            buttonPanel.add(Box.createVerticalStrut(5), gbc);
        }
        buttonPanel.updateUI();
    }

    public static void showEditMenu() {
        showEditMenu(new CommandData(null));
    }

    public static void showEditMenu(CommandData dataParam){

        String title = "Edit Command";

        if(dataParam.getName() == null) title = "Add Command";

        EditCommandPanel editCommandPanel = new EditCommandPanel(title, dataParam, (kv, d, e) -> {

            CommandData data;
            if(d.getName() == null){
                data = new CommandData(kv.get("name"));
                data.register();
            }
            else{
                data = (CommandData) d;
            }

            Utilities.ifNotNull(kv.get("aliases"), o -> data.setAliases(Arrays.asList(((String)o).split(","))));
            Utilities.ifNotNull(kv.get("cooldown"), o -> data.setCooldown(Integer.parseInt((String)o)));
            Utilities.ifNotNull(kv.get("userlevel"), o -> data.setUserLevel(UserLevel.parse(Integer.parseInt((String)o))));
            Utilities.ifNotNull(kv.get("message"), o -> data.setMessage((String)o));
            data.setName(kv.get("name"));

            //todo check name collisions

            data.save(true);
            e.close();
        });
        editCommandPanel.addNameInput("Command:", "The command name, what must be typed in chat to run.");
        editCommandPanel.addMessageInput();
        editCommandPanel.addUserLevelsInput();
        editCommandPanel.addCooldownInput();
        editCommandPanel.addAliasesInput();
        editCommandPanel.setBounds(0,0,800,550);

        editCommandPanel.showMenu();
    }


}
