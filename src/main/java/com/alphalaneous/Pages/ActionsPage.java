package com.alphalaneous.Pages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Utilities.Assets;
import com.alphalaneous.Components.ConfigCheckbox;
import com.alphalaneous.Components.EditCommandPanel;
import com.alphalaneous.Components.RoundedButton;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Utilities.Fonts;
import com.alphalaneous.Interactive.Actions.ActionData;
import com.alphalaneous.Components.SidebarSwitcher;
import com.alphalaneous.Utilities.Utilities;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ActionsPage {

    static Page page = new Page("Actions", true);
    static ThemeableJPanel buttonPanel = new ThemeableJPanel();
    static GridBagConstraints gbc = new GridBagConstraints();

    @OnLoad(order = 0)
    public static void init() {

        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        page.setShowChat(false);

        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridBagLayout());

        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        page.getContentPane().add(buttonPanel, BorderLayout.NORTH);

        ThemeableJPanel addButtonPanel = new ThemeableJPanel();
        addButtonPanel.setOpaque(false);
        addButtonPanel.setLayout(new BorderLayout());
        addButtonPanel.setBorder(new EmptyBorder(10,5,10,10));
        addButtonPanel.setPreferredSize(new Dimension(50,50));

        RoundedButton addButton = new RoundedButton("+");
        addButton.setFont(Fonts.getFont("Glyphs").deriveFont(14f));
        addButton.addActionListener(e -> showEditMenu());

        addButtonPanel.add(addButton);

        page.getTitleCard().add(addButtonPanel, BorderLayout.EAST);
        page.setOpaque(false);

        SidebarSwitcher.addPage(Assets.getImage("run-button"), page, ActionsPage::load);
    }

    public static void showEditMenu() {
        showEditMenu(new ActionData(null));
    }

    public static void showEditMenu(ActionData dataParam){

        String title = "$EDIT_ACTION$";

        if(dataParam.getName() == null) title = "$ADD_ACTION$";

        EditCommandPanel editCommandPanel = new EditCommandPanel(title, dataParam, (kv, d, e) -> {

            ActionData data;
            if(d.getName() == null){
                data = new ActionData(kv.get("name"));
                data.register();
            }
            else{
                data = (ActionData) d;
            }

            Utilities.ifNotNull(kv.get("message"), o -> data.setMessage((String)o));
            data.setName(kv.get("name"));
            data.setKeyBind(Integer.parseInt(kv.get("keybind")));
            data.setUsesCtrl(Boolean.parseBoolean(kv.get("keybindCtrl")));
            data.setUsesAlt(Boolean.parseBoolean(kv.get("keybindAlt")));
            data.setUsesShift(Boolean.parseBoolean(kv.get("keybindShift")));

            data.save(true);
            e.close();
        });
        editCommandPanel.addNameInput("$ACTION_NAME_INPUT$", "$ACTION_NAME_DESC$");
        editCommandPanel.addMessageInput();
        editCommandPanel.addKeybindInput();
        editCommandPanel.setBounds(0,0,800,450);

        editCommandPanel.showMenu();
    }

    public static void load(){

        buttonPanel.removeAll();
        for(ActionData actionData : ActionData.getRegisteredActions()){

            buttonPanel.add(new ConfigCheckbox(actionData, () -> {
                showEditMenu(actionData);
            }, true), gbc);
            buttonPanel.add(Box.createVerticalStrut(5), gbc);
        }
        buttonPanel.updateUI();
    }
}
