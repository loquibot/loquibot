package com.alphalaneous.Pages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Assets;
import com.alphalaneous.Components.RoundedButton;
import com.alphalaneous.Components.SmoothScrollPane;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJLabel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Fonts;
import com.alphalaneous.Interfaces.Function;
import com.alphalaneous.Pages.SettingsSubPages.SettingsSubPage;
import com.alphalaneous.SidebarSwitcher;
import com.alphalaneous.Utilities.GraphicsFunctions;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SettingsPage {

    private static final HashMap<RoundedButton, SettingsSubPage> pages = new HashMap<>();
    static ThemeableJPanel buttonsPanel = new ThemeableJPanel();
    static ThemeableJPanel buttonsContainer = new ThemeableJPanel();
    static ThemeableJPanel buttonsScrollContainer = new ThemeableJPanel(){
        @Override
        public void paintComponent(Graphics g) {
            GraphicsFunctions.roundCorners(g, getBackground(), getSize());
            super.paintComponent(g);
        }
    };

    static SmoothScrollPane buttonsScroll = new SmoothScrollPane(buttonsContainer);

    public static final ThemeableJPanel pagePanel = new ThemeableJPanel();
    public static final ThemeableJPanel contentPanel = new ThemeableJPanel();

    static GridBagConstraints gbc = new GridBagConstraints();
    static Page page = new Page("Settings", true);

    @OnLoad(order = 2)
    public static void init() {

        page.setBackground("background");
        page.setShowChat(false);

        contentPanel.setLayout(new MigLayout());

        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        buttonsPanel.setLayout(new GridBagLayout());
        buttonsPanel.setBorder(new EmptyBorder(0,7,0,0));
        buttonsPanel.setOpaque(false);

        buttonsContainer.setOpaque(false);
        buttonsScroll.setOpaque(false);
        buttonsScroll.getViewport().setOpaque(false);

        buttonsContainer.add(buttonsPanel);

        buttonsScrollContainer.setBackground("background");

        buttonsScrollContainer.add(buttonsScroll);
        buttonsScrollContainer.setPreferredSize(new Dimension(300, 10));
        buttonsScrollContainer.setOpaque(false);

        contentPanel.setBorder(new EmptyBorder(0,0,14,0));


        contentPanel.setOpaque(false);

        contentPanel.add(buttonsScrollContainer, "w 200px, h 100%");
        page.add(contentPanel);

        SidebarSwitcher.addPage(Assets.getImage("settings-button"), page, () -> {});
    }

    public static void addPage(String title, String icon, SettingsSubPage settingsSubPage, Function clickFunction){

        SettingsButton button = new SettingsButton(title, icon);

        settingsSubPage.setVisible(false);

        button.addActionListener(e -> {

            boolean pageWasVisible = settingsSubPage.isVisible();

            for (Map.Entry<RoundedButton, SettingsSubPage> set : pages.entrySet()){

                RoundedButton button1 = set.getKey();
                button1.setSelected(false);

                SettingsSubPage page1 = set.getValue();
                page1.setVisible(false);
            }

            button.setSelected(true);
            settingsSubPage.setVisible(true);

            if(!pageWasVisible) {
                if (clickFunction != null) clickFunction.run();
            }
        });

        pages.put(button, settingsSubPage);
        buttonsPanel.add(button, gbc);
        buttonsPanel.add(Box.createVerticalStrut(5), gbc);

        if(pages.size() == 1){
            button.setSelected(true);
            settingsSubPage.setVisible(true);
            if (clickFunction != null) clickFunction.run();
        }

        pagePanel.add(settingsSubPage);
    }

    private static class SettingsButton extends RoundedButton {

        private final ThemeableJLabel label;


        SettingsButton(String text, String icon){
            super("");

            label = new ThemeableJLabel(text);
            label.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));
            if(icon == null) label.setBounds(15, 7, 208, 20);
            else label.setBounds(40, 7, 208, 20);
            label.setForeground("foreground");

            ThemeableJLabel iconLabel = new ThemeableJLabel(icon);

            iconLabel.setFont(Fonts.getFont("Glyphs").deriveFont(14f));
            iconLabel.setBounds(15, 6, 20, 20);
            iconLabel.setForeground("foreground");

            setLayout(null);
            add(label);
            if(icon != null) add(iconLabel);

            setBackground("list-background-normal", "list-background-selected");
            setHoverColor("list-hover-normal","list-hover-selected");
            setClicked("list-clicked-normal","list-clicked-selected");
            setForeground("list-foreground-normal", "list-background-selected");
            setBorder(BorderFactory.createEmptyBorder());
            setPreferredSize(new Dimension(180, 32));
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        }

        public void resizeText(float size){
            label.setFont(label.getFont().deriveFont(size));
        }


    }
}
