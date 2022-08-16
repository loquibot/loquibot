package com.alphalaneous.Settings;

import com.alphalaneous.Swing.Components.CurvedButton;
import com.alphalaneous.Swing.Components.FancyTextArea;
import com.alphalaneous.Swing.Components.LangLabel;
import com.alphalaneous.Swing.Components.SettingsPage;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Utils.Utilities;
import com.alphalaneous.Windows.DialogBox;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class SpamProtection {
    private static final SettingsPage settingsPage = new SettingsPage("$SPAM_PROTECTION_SETTINGS$");

    public static JPanel createPanel(){

        createBasicConfigCheckbox("$CAPITAL_LETTER_SPAM$", "$CAPITAL_LETTER_SPAM_DESC$", "capitalFilterEnabled", "$CAPITAL_LETTERS$", "capital");
        createBasicConfigCheckbox("$SYMBOL_SPAM$", "$SYMBOL_SPAM_DESC$", "symbolFilterEnabled", "$SYMBOLS$", "symbol");
        createBasicConfigCheckbox("$EMOTE_SPAM$", "$EMOTE_SPAM_DESC$", "emoteFilterEnabled", "$EMOTES$", "emote");

        settingsPage.addConfigCheckbox("$LINK_SPAM$", "$LINK_SPAM_DESC$", "linkFilterEnabled", SpamProtection::openLinkFilterSettings);
        settingsPage.addConfigCheckbox("$GIBBERISH_SPAM$", "$GIBBERISH_SPAM_DESC$", "gibberishFilterEnabled", SpamProtection::openGibberishFilterSettings);
        settingsPage.addCheckbox("$BIG_FOLLOWS_SPAM$", "", "autoDeleteBigFollows");
        settingsPage.addCheckbox("$AUTO_DELETE_GD_LEVEL_IDS$", "", "autoDeleteGDLevelIDs");

        return settingsPage;
    }

    private static void createBasicConfigCheckbox(String text, String description, String setting, String type, String settingsPrefix){
        settingsPage.addConfigCheckbox(text, description, setting, () -> openSpamFilterSettings(text, type, settingsPrefix));
    }

    private static void openSpamFilterSettings(String settingTitle, String type, String settingPrefix){
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0,0,400,390);
        panel.setBackground(Defaults.COLOR3);
        LangLabel langLabel = new LangLabel(settingTitle);
        langLabel.setFont(Defaults.MAIN_FONT.deriveFont(20f));
        langLabel.setBounds(10,5,400, 40);
        langLabel.setForeground(Defaults.FOREGROUND_A);
        panel.add(langLabel);

        String countDesc = Utilities.format("$ANTISPAM_COUNT_INPUT_DESC$", Utilities.format(type));
        String percentDesc = Utilities.format("$ANTISPAM_PERCENT_INPUT_DESC$", Utilities.format(type));

        TextInput countInput = new TextInput("$ANTISPAM_COUNT_INPUT$", countDesc, false, 9999, settingPrefix + "Count", "5", false);
        countInput.setBounds(10,50,400, 60);
        panel.add(countInput);

        TextInput percentInput = new TextInput("$ANTISPAM_PERCENT_INPUT$", percentDesc, true, 100, settingPrefix + "Percent", "50", false);
        percentInput.setBounds(10,120,400, 60);
        panel.add(percentInput);


        TextInput warningsInput = new TextInput("$ANTISPAM_WARNINGS_INPUT$", "$ANTISPAM_WARNINGS_INPUT_DESC$", false, 9999, settingPrefix + "Warnings", "3", false);
        warningsInput.setBounds(10,190,400, 60);
        panel.add(warningsInput);

        TextInput timeoutDurationInout = new TextInput("$ANTISPAM_TIMEOUT_DURATION_INPUT$", "$ANTISPAM_TIMEOUT_DURATION_INPUT_DESC$", false, 1209600, settingPrefix + "TimeoutDuration", "600", false);
        timeoutDurationInout.setBounds(10,260,400, 60);
        panel.add(timeoutDurationInout);

        CurvedButton CurvedButton = new CurvedButton("$OKAY$");
        CurvedButton.setBounds(150,340,100,40);
        CurvedButton.setUI(Defaults.settingsButtonUI);
        CurvedButton.setBackground(Defaults.COLOR2);
        CurvedButton.setForeground(Defaults.FOREGROUND_A);
        CurvedButton.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        CurvedButton.setBorder(BorderFactory.createEmptyBorder());

        CurvedButton.addActionListener(e -> DialogBox.closeDialogBox());

        panel.add(CurvedButton);

        DialogBox.showDialogBox(panel);
    }

    private static void openGibberishFilterSettings(){
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0,0,400,320);
        panel.setBackground(Defaults.COLOR3);
        LangLabel langLabel = new LangLabel("$GIBBERISH_SPAM$");
        langLabel.setFont(Defaults.MAIN_FONT.deriveFont(20f));
        langLabel.setBounds(10,5,320, 40);
        langLabel.setForeground(Defaults.FOREGROUND_A);
        panel.add(langLabel);

        TextInput countInput = new TextInput("$GIBBERISH_THRESHOLD_INPUT$", "$GIBBERISH_THRESHOLD_INPUT_DESC$", true, 9999, "gibberishFilterThreshold", "99.5", true);
        countInput.setBounds(10,50,400, 60);
        panel.add(countInput);


        TextInput warningsInput = new TextInput("$ANTISPAM_WARNINGS_INPUT$", "$ANTISPAM_WARNINGS_INPUT_DESC$", false, 9999, "gibberishFilter" + "Warnings", "3", false);
        warningsInput.setBounds(10,120,400, 60);
        panel.add(warningsInput);

        TextInput timeoutDurationInout = new TextInput("$ANTISPAM_TIMEOUT_DURATION_INPUT$", "$ANTISPAM_TIMEOUT_DURATION_INPUT_DESC$", false, 1209600, "gibberishFilter" + "TimeoutDuration", "600", false);
        timeoutDurationInout.setBounds(10,190,400, 60);
        panel.add(timeoutDurationInout);

        CurvedButton CurvedButton = new CurvedButton("$OKAY$");
        CurvedButton.setBounds(150,270,100,40);
        CurvedButton.setUI(Defaults.settingsButtonUI);
        CurvedButton.setBackground(Defaults.COLOR2);
        CurvedButton.setForeground(Defaults.FOREGROUND_A);
        CurvedButton.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        CurvedButton.setBorder(BorderFactory.createEmptyBorder());

        CurvedButton.addActionListener(e -> DialogBox.closeDialogBox());

        panel.add(CurvedButton);

        DialogBox.showDialogBox(panel);
    }

    private static void openLinkFilterSettings(){
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0,0,400,250);
        panel.setBackground(Defaults.COLOR3);
        LangLabel langLabel = new LangLabel("$LINK_SPAM$");
        langLabel.setFont(Defaults.MAIN_FONT.deriveFont(20f));
        langLabel.setBounds(10,5,400, 40);
        langLabel.setForeground(Defaults.FOREGROUND_A);
        panel.add(langLabel);

        TextInput warningsInput = new TextInput("$ANTISPAM_WARNINGS_INPUT$", "$ANTISPAM_WARNINGS_INPUT_DESC$", false, 9999, "linkFilterWarnings", "3", false);
        warningsInput.setBounds(10,50,400, 60);
        panel.add(warningsInput);

        TextInput timeoutDurationInout = new TextInput("$ANTISPAM_TIMEOUT_DURATION_INPUT$", "$ANTISPAM_TIMEOUT_DURATION_INPUT_DESC$", false, 1209600, "linkFilterTimeoutDuration", "600", false);
        timeoutDurationInout.setBounds(10,120,400, 60);
        panel.add(timeoutDurationInout);

        CurvedButton CurvedButton = new CurvedButton("$OKAY$");
        CurvedButton.setBounds(150,200,100,40);
        CurvedButton.setUI(Defaults.settingsButtonUI);
        CurvedButton.setBackground(Defaults.COLOR2);
        CurvedButton.setForeground(Defaults.FOREGROUND_A);
        CurvedButton.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        CurvedButton.setBorder(BorderFactory.createEmptyBorder());

        CurvedButton.addActionListener(e -> DialogBox.closeDialogBox());

        panel.add(CurvedButton);

        DialogBox.showDialogBox(panel);
    }
    public static class TextInput extends JPanel {
        private final FancyTextArea textArea;

        TextInput(String text, String description, boolean isPercentage, int numberLimit, String setting, String defaultInput, boolean allowDecimal){
            int height = 32;

            LangLabel descriptionText = new LangLabel("");
            descriptionText.setTextLang("<html><div WIDTH=450> " + description + " </div></html>");
            descriptionText.setFont(Defaults.MAIN_FONT.deriveFont(13f));
            descriptionText.setForeground(Defaults.FOREGROUND_B);
            descriptionText.setOpaque(false);
            descriptionText.setPreferredSize(new Dimension(450, descriptionText.getPreferredSize().height));
            descriptionText.setBounds(0, 30, 450, descriptionText.getPreferredSize().height);
            add(descriptionText);

            LangLabel titleText = new LangLabel("");
            titleText.setTextLang(text);
            titleText.setFont(Defaults.MAIN_FONT.deriveFont(14f));
            titleText.setOpaque(false);
            titleText.setForeground(Defaults.FOREGROUND_A);
            titleText.setPreferredSize(new Dimension(450, 30));
            titleText.setBounds(0,0, 450, 30);

            textArea = new FancyTextArea(true, false, allowDecimal, numberLimit);
            textArea.getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                    changed();
                }
                public void removeUpdate(DocumentEvent e) {
                    changed();
                }
                public void insertUpdate(DocumentEvent e) {
                    changed();
                }
                public void changed() {
                    if(textArea.getText().equalsIgnoreCase("")){
                        SettingsHandler.writeSettings(setting, textArea.getText());
                    }
                    else {
                        if (isPercentage) SettingsHandler.writeSettings(setting, String.valueOf(Double.parseDouble(textArea.getText()) / 100));
                        else SettingsHandler.writeSettings(setting, textArea.getText());
                    }
                }
            });

            textArea.setBounds(320,10,60, height);
            setPreferredSize(new Dimension(400, 60));

            setLayout(null);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            if(isPercentage){
                if (SettingsHandler.getSettings(setting).exists()) {
                    if (!allowDecimal) textArea.setText(String.valueOf((int) (SettingsHandler.getSettings(setting).asDouble() * 100)));
                    else textArea.setText(String.valueOf(SettingsHandler.getSettings(setting).asDouble() * 100));
                }
                else {
                    SettingsHandler.writeSettings(setting, defaultInput);
                    textArea.setText(defaultInput);
                }
            }
            else {
                if (SettingsHandler.getSettings(setting).exists()) textArea.setText(SettingsHandler.getSettings(setting).asString());
                else {
                    SettingsHandler.writeSettings(setting, defaultInput);
                    textArea.setText(defaultInput);
                }
            }

            setBackground(Defaults.COLOR3);
            add(textArea);
            add(titleText);
        }
    }
}
