package com.alphalaneous.Pages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Utilities.Assets;
import com.alphalaneous.Components.RoundedButton;
import com.alphalaneous.Components.SmoothScrollPane;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJLabel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Utilities.Fonts;
import com.alphalaneous.Interfaces.Function;
import com.alphalaneous.Pages.SettingsSubPages.SettingsSubPage;
import com.alphalaneous.Components.SidebarSwitcher;
import com.alphalaneous.Utilities.GraphicsFunctions;
import net.miginfocom.swing.MigLayout;

import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SettingsPage {

    private static final HashMap<RoundedButton, SettingsSubPage> pages = new HashMap<>();
    static ThemeableJPanel buttonsPanel = new ThemeableJPanel();
    static ThemeableJPanel buttonsScrollContainer = new ThemeableJPanel(){
        @Override
        public void paintComponent(Graphics g) {
            GraphicsFunctions.roundCorners(g, getBackground(), getSize());
            super.paintComponent(g);
        }
    };

    static SmoothScrollPane buttonsScroll = new SmoothScrollPane(buttonsPanel);

    public static final ThemeableJPanel pagePanel = new ThemeableJPanel();
    public static final ThemeableJPanel contentPanel = new ThemeableJPanel();

    static Page page = new Page("$SETTINGS_TITLE$", false);

    @OnLoad(order = 10000)
    public static void init() {

        page.setBackground("background");
        page.setShowChat(false);

        contentPanel.setLayout(new MigLayout());

        buttonsPanel.setOpaque(false);
        buttonsPanel.setLayout(new GridLayout(0,1,0,5));


        buttonsScroll.setOpaque(false);
        buttonsScroll.getViewport().setOpaque(false);

        buttonsScrollContainer.setBackground("background");

        buttonsScrollContainer.add(buttonsScroll);
        buttonsScrollContainer.setOpaque(false);
        buttonsScrollContainer.setBorder(new EmptyBorder(2,7,0,0));

        pagePanel.setOpaque(false);
        pagePanel.setLayout(new MigLayout("insets 0"));

        contentPanel.setOpaque(false);

        contentPanel.add(buttonsScrollContainer, "w 200px, h 100%, wmin 0");
        contentPanel.add(pagePanel, "w 100% - 200px, h 100%, wmin 0");

        page.getContentPane().setBorder(new EmptyBorder(0,0,14,0));
        page.getContentPane().add(contentPanel);

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
                pagePanel.remove(page1);
            }

            button.setSelected(true);
            settingsSubPage.setVisible(true);
            pagePanel.add(settingsSubPage, "w 100%, h 100%");

            if(!pageWasVisible) {
                if (clickFunction != null) clickFunction.run();
            }
        });

        pages.put(button, settingsSubPage);
        buttonsPanel.add(button);

        if(pages.size() == 1){
            button.setSelected(true);
            settingsSubPage.setVisible(true);
            pagePanel.add(settingsSubPage, "w 100%, h 100%");
            if (clickFunction != null) clickFunction.run();
        }
    }

    private static class SettingsButton extends RoundedButton {

        private final ThemeableJLabel label;


        SettingsButton(String text, String icon){
            super("");

            setArc(14);

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

            setPreferredSize(new Dimension(180, 32));
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        }

        public void resizeText(float size){
            label.setFont(label.getFont().deriveFont(size));
        }


    }
}
