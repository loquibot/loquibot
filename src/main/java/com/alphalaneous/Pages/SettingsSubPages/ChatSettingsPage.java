package com.alphalaneous.Pages.SettingsSubPages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Components.SettingsTitle;
import com.alphalaneous.Components.SmoothScrollPane;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Pages.SettingsPage;
import com.alphalaneous.Utilities.GraphicsFunctions;

import javax.swing.*;
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


    @OnLoad(order = 10005)
    public static void init(){

        page.addCheckbox("$DISABLE_MESSAGES$", "$DISABLE_MESSAGES_DESCRIPTION$", "disableMessages");
        page.addShortInput("$DEFAULT_COMMAND_PREFIX$", "$DEFAULT_COMMAND_PREFIX_DESCRIPTION$", "defaultCommandPrefix", "!");

        commandsPanel.setLayout(new BorderLayout());
        commandsPanel.setOpaque(false);
        commandsPanel.setBackground("background");
        commandsPanel.add(smoothScrollPane);

        smoothScrollPane.setOpaque(false);
        commandsChild.setOpaque(false);

        commandsChild.setLayout(new BoxLayout(commandsChild, BoxLayout.Y_AXIS));

        for(int i = 0; i < 10; i++){
            commandsChild.add(new SettingsTitle("test"));

            System.out.println(commandsChild.getPreferredSize().height);
        }

        page.addTitle("$DEFAULT_COMMANDS$", 20f);
        page.addComponent(commandsPanel);

        SettingsPage.addPage("$CHAT_TITLE$", "\uF162", page, null);

    }

    public static ThemeableJPanel getCommandsPanel(){
        return commandsPanel;
    }
    public static SmoothScrollPane getScrollPane(){
        return smoothScrollPane;
    }

}
