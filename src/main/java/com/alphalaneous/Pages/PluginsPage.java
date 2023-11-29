package com.alphalaneous.Pages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Utilities.Assets;
import com.alphalaneous.Components.RoundedButton;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Interfaces.Function;
import com.alphalaneous.Pages.CommandPages.ChatPageComponent;
import com.alphalaneous.Components.SidebarSwitcher;

import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PluginsPage {

    private static final HashMap<RoundedButton, ChatPageComponent> pages = new HashMap<>();
    static Page page = new Page();
    static ThemeableJPanel buttonsPanel = new ThemeableJPanel();

    static Function currentFunction;

    public static final ThemeableJPanel pagePanel = new ThemeableJPanel();

    @OnLoad(order = 3)
    public static void init() {

        page.setBackground("background");
        page.setShowChat(false);

        buttonsPanel.setLayout(new GridLayout(1,3,5,5));
        buttonsPanel.setBorder(new EmptyBorder(5,5,5,5));
        buttonsPanel.setOpaque(false);

        page.getTitleCard().setLayout(new BorderLayout());

        page.getTitleCard().add(buttonsPanel);

        pagePanel.setLayout(null);
        pagePanel.setOpaque(false);

        page.getContentPane().add(pagePanel);

        SidebarSwitcher.addPage(Assets.getImage("plugins-button"), page, () -> {});
    }

    public static void addPage(String title, ChatPageComponent commandPage, Function editFunction, Function addFunction){

        RoundedButton button = new RoundedButton(title);

        commandPage.setVisible(false);

        button.addActionListener(e -> {

            boolean pageWasVisible = commandPage.isVisible();

            for (Map.Entry<RoundedButton, ChatPageComponent> set : pages.entrySet()){

                RoundedButton button1 = set.getKey();
                button1.setSelected(false);

                ChatPageComponent page1 = set.getValue();
                page1.setVisible(false);
            }

            button.setSelected(true);
            commandPage.setVisible(true);

            if(!pageWasVisible) {
                currentFunction = addFunction;
                if (editFunction != null) editFunction.run();
            }
        });

        pages.put(button, commandPage);
        buttonsPanel.add(button);

        if(pages.size() == 1){
            button.setSelected(true);
            commandPage.setVisible(true);
            currentFunction = addFunction;
            if (editFunction != null) editFunction.run();
        }

        pagePanel.add(commandPage);
    }

    public static ArrayList<ChatPageComponent> getPages(){
        ArrayList<ChatPageComponent> pages1 = new ArrayList<>();

        for (Map.Entry<RoundedButton, ChatPageComponent> set : pages.entrySet()){
            pages1.add(set.getValue());
        }

        return pages1;
    }

}
