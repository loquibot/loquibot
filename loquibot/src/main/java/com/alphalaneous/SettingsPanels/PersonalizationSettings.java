package com.alphalaneous.SettingsPanels;

import com.alphalaneous.Components.CurvedButtonAlt;
import com.alphalaneous.Components.LangLabel;
import com.alphalaneous.Components.SettingsPage;
import com.alphalaneous.Defaults;
import com.alphalaneous.Settings;
import com.alphalaneous.ThemedComponents.ThemedCheckbox;
import com.alphalaneous.Themes;
import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Windows.Window;
import com.bric.swing.ColorPicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class PersonalizationSettings {

    private static final SettingsPage settingsPage = new SettingsPage("$PERSONALIZATION_SETTINGS$");


    public static JPanel createPanel() {
        settingsPage.addRadioOption("$THEME_TEXT$", "", new String[]{"$LIGHT_MODE$", "$DARK_MODE$", "$CUSTOM_MODE$" ,"$SYSTEM_MODE$"}, "theme", "SYSTEM_MODE", PersonalizationSettings::setTheme);

        settingsPage.addCheckbox("$ALWAYS_ON_TOP$", "$ON_TOP_DESCRIPTION$", "onTop", PersonalizationSettings::setOnTop);
        settingsPage.addCheckbox("$DISABLE_FOCUS$", "$DISABLE_FOCUS_DESCRIPTION$", "disableFocus", PersonalizationSettings::setFocusable);
        settingsPage.addCheckbox("$DISABLE_NOTIFICATIONS$", "$DISABLE_NOTIFICATIONS_DESCRIPTION$","disableNotifications");
        return settingsPage;
    }
    public static void setTheme(){
        settingsPage.removeButton("Customize");
        settingsPage.updateUI();
        String theme = Settings.getSettings("theme").asString();
        if (theme.equalsIgnoreCase("DARK_MODE"))Defaults.setDark();
        else if (theme.equalsIgnoreCase("LIGHT_MODE")) Defaults.setLight();
        else if (theme.equalsIgnoreCase("CUSTOM_MODE")) {
            Defaults.setCustom();
            showCustomizeButton();
        }
        else Defaults.setSystem();
    }

    public static void showCustomizeButton(){
        settingsPage.addButton("Customize", PersonalizationSettings::showCustomizationMenu);
        settingsPage.moveComponent(5, 2);
    }

    public static void setOnTop(){
        Window.setOnTop(Settings.getSettings("onTop").asBoolean());
    }
    public static void setFocusable(){
        Window.setFocusable(!Settings.getSettings("disableFocus").asBoolean());
    }

    private static JPanel panel;
    private static final CurvedButtonAlt curvedButtonAlt = new CurvedButtonAlt("$OKAY$");

    public static void showCustomizationMenu(){
        panel = new JPanel();
        panel.setBounds(0,0,300,450);
        panel.setLayout(null);
        panel.setBackground(Defaults.COLOR3);
        panel.add(new ColorComponent("$BACKGROUND_MAIN$", "color", 10));
        panel.add(new ColorComponent("$BACKGROUND_SECONDARY$", "color3", 50));
        panel.add(new ColorComponent("$BACKGROUND_SPECIAL$", "color6", 90));
        //panel.add(new ColorComponent("", "color1", 50));
        panel.add(new ColorComponent("$BUTTONS_MAIN$", "color2", 130));
        panel.add(new ColorComponent("$BUTTONS_SECONDARY$", "color4", 170));
        panel.add(new ColorComponent("$BUTTONS_SELECT$", "color5", 210));
        panel.add(new ColorComponent("$BUTTONS_HOVER$", "color7", 250));
        panel.add(new ColorComponent("$FOREGROUND_MAIN$", "foreground_a", 290));
        panel.add(new ColorComponent("$FOREGROUND_SECONDARY$", "foreground_b", 330));
        ThemedCheckbox checkbox = new ThemedCheckbox("$IS_LIGHT_SETTING$");
        checkbox.setBounds(10,370, 250, 30);
        checkbox.setChecked(Themes.getIsLight());
        checkbox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                Themes.writeTheme("is_light", String.valueOf(checkbox.getSelectedState()));
                Defaults.setCustom();
                panel.setBackground(Defaults.COLOR3);
                curvedButtonAlt.setBackground(Defaults.COLOR2);
                curvedButtonAlt.setForeground(Defaults.FOREGROUND_A);
                ColorComponent.refreshAll();
                DialogBox.refreshUI();
            }
        });
        checkbox.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        checkbox.refresh();
        panel.add(checkbox);

        curvedButtonAlt.setBounds(100,410,100,40);
        curvedButtonAlt.setUI(Defaults.settingsButtonUI);
        curvedButtonAlt.setBackground(Defaults.COLOR2);
        curvedButtonAlt.setForeground(Defaults.FOREGROUND_A);
        curvedButtonAlt.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        curvedButtonAlt.setBorder(BorderFactory.createEmptyBorder());

        curvedButtonAlt.addActionListener(e -> DialogBox.closeDialogBox());

        panel.add(curvedButtonAlt);

        DialogBox.showDialogBox(panel);
    }
    private static class ColorComponent extends JPanel{

        private static final ArrayList<ColorComponent> colorComponents = new ArrayList<>();
        private final JPanel colorPanel = new JPanel();
        private final LangLabel langLabel;

        public ColorComponent(String text, String setting, int y){
            setOpaque(false);
            setLayout(null);
            setBackground(new Color(0,0,0,0));
            langLabel = new LangLabel(text);
            langLabel.setForeground(Defaults.FOREGROUND_A);
            langLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));
            langLabel.setBounds(0,0,300,30);


            colorPanel.setBackground(Themes.getThemeSetting(setting));
            colorPanel.setBounds(250, 0, 30,30);
            colorPanel.setBorder(BorderFactory.createLineBorder(Defaults.FOREGROUND_B,1));
            colorPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    Color color = ColorPicker.showDialog(Window.getWindow(), Themes.getThemeSetting(setting));
                    if(color != null) {
                        colorPanel.setBackground(color);
                        Themes.writeTheme(setting, String.format("%06x", 0xFFFFFF & color.getRGB()));
                        Defaults.setCustom();
                        panel.setBackground(Defaults.COLOR3);
                        curvedButtonAlt.setBackground(Defaults.COLOR2);
                        curvedButtonAlt.setForeground(Defaults.FOREGROUND_A);
                        ColorComponent.refreshAll();
                        DialogBox.refreshUI();
                    }
                }
            });
            add(langLabel);
            add(colorPanel);
            setBounds(10, y, 300, 30);
            colorComponents.add(this);
        }
        private void refreshUI(){
            langLabel.setForeground(Defaults.FOREGROUND_A);
            colorPanel.setBorder(BorderFactory.createLineBorder(Defaults.FOREGROUND_B,1));
            updateUI();
        }
        public static void refreshAll(){
            for(ColorComponent component : colorComponents){
                component.refreshUI();
            }
        }
    }
}
