package com.alphalaneous.Pages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Utilities.Assets;
import com.alphalaneous.Components.RoundedButton;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Utilities.Fonts;
import com.alphalaneous.Interfaces.Function;
import com.alphalaneous.Pages.CommandPages.ChatPageComponent;
import com.alphalaneous.Components.SidebarSwitcher;

import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StreamInteractionsPage {

    private static final HashMap<RoundedButton, ChatPageComponent> pages = new HashMap<>();
    static Page page = new Page();
    static ThemeableJPanel buttonsPanel = new ThemeableJPanel();

    static Function currentFunction;

    public static final ThemeableJPanel pagePanel = new ThemeableJPanel();
    private static final RoundedButton addButton = new RoundedButton("+");

    @OnLoad(order = 2)
    public static void init() {

        page.setBackground("background");
        page.setShowChat(false);

        buttonsPanel.setLayout(new GridLayout(1,3,5,5));
        buttonsPanel.setBorder(new EmptyBorder(5,5,5,5));
        buttonsPanel.setOpaque(false);

        page.getTitleCard().setLayout(new BorderLayout());

        page.getTitleCard().add(buttonsPanel);

        ThemeableJPanel addButtonPanel = new ThemeableJPanel();
        addButtonPanel.setOpaque(false);
        addButtonPanel.setLayout(new BorderLayout());
        addButtonPanel.setBorder(new EmptyBorder(10,5,10,10));
        addButtonPanel.setPreferredSize(new Dimension(50,50));

        addButton.setFont(Fonts.getFont("Glyphs").deriveFont(14f));
        addButton.addActionListener(e -> currentFunction.run());

        addButtonPanel.add(addButton);

        page.getTitleCard().add(addButtonPanel, BorderLayout.EAST);

        pagePanel.setLayout(null);
        pagePanel.setOpaque(false);

        page.getContentPane().add(pagePanel);
        SidebarSwitcher.addPage(Assets.getImage("stream-interaction-button"), page, () -> {});

        setEnabled(false);

    }

    public static void disableRightButton(boolean disable){

        if(disable) {
            addButton.setForeground("foreground-disabled", "foreground-disabled");
            addButton.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        else {
            addButton.setForeground("foreground", "foreground");
            addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        addButton.setEnabled(!disable);
    }

    public static void setRightButtonIcon(String text){
        addButton.setText(text);
    }

    public static void setEnabled(boolean enabled){
        SidebarSwitcher.togglePage(page, enabled);
    }

    public static void addPage(String title, ChatPageComponent commandPage, Function loadFunction, Function addFunction){

        RoundedButton button = new RoundedButton(title);

        button.setArc(16);

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
                if (loadFunction != null) loadFunction.run();
            }
        });

        pages.put(button, commandPage);
        buttonsPanel.add(button);

        if(pages.size() == 1){
            button.setSelected(true);
            commandPage.setVisible(true);
            currentFunction = addFunction;
            if (loadFunction != null) loadFunction.run();
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
