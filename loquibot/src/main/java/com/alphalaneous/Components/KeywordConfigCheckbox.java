package com.alphalaneous.Components;

import com.alphalaneous.ChatbotTab.CustomCommands;
import com.alphalaneous.ChatbotTab.CustomKeywords;
import com.alphalaneous.ChatbotTab.DefaultCommands;
import com.alphalaneous.*;
import com.alphalaneous.Panels.ContextButton;
import com.alphalaneous.Panels.ContextMenu;
import com.alphalaneous.ThemedComponents.ThemedConfigCheckbox;
import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Windows.Window;
import org.apache.commons.text.StringEscapeUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class KeywordConfigCheckbox extends JPanel {

    private static final ArrayList<KeywordConfigCheckbox> keywordConfigCheckboxes = new ArrayList<>();

    private final ThemedConfigCheckbox themedCheckbox;

    public KeywordConfigCheckbox(KeywordData keywordData){


        themedCheckbox = new ThemedConfigCheckbox(keywordData.getKeyword(), StringEscapeUtils.escapeHtml4(keywordData.getMessage()), () -> openKeywordSettings(keywordData), false, null, keywordData);

        themedCheckbox.setChecked(keywordData.isEnabled());
        setLayout(null);
        setOpaque(false);
        themedCheckbox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                for(KeywordData data : KeywordData.getRegisteredKeywords()){
                    if(data.getKeyword().equalsIgnoreCase(keywordData.getKeyword())){
                        data.setEnabled(themedCheckbox.getSelectedState());
                    }
                }
            }
        });

        setPreferredSize(new Dimension(450, 72));

        themedCheckbox.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        themedCheckbox.setBounds(0,0, 450, 70);
        setBackground(new Color(255,255,255,20));
        themedCheckbox.refresh();
        add(themedCheckbox);
        keywordConfigCheckboxes.add(this);
    }
    public void refreshUI(){
        setBackground(new Color(255,255,255,20));
    }
    public void resize(int width){
        setPreferredSize(new Dimension(width-300, 72));
        themedCheckbox.setBounds(0,0, width-300, 70);
        themedCheckbox.resize(width-300);
    }

    public static void resizeAll(int width){
        for(KeywordConfigCheckbox commandConfigCheckbox : keywordConfigCheckboxes){
            commandConfigCheckbox.resize(width);
        }
    }

    private static final JPanel commandSettingsPanel = new JPanel();

    private static final CurvedButtonAlt userLevelButton = new CurvedButtonAlt("");
    private static String userLevel = "everyone";

    public static void openKeywordSettings(KeywordData data){
        openKeywordSettings(data, false);
    }
    public static void openKeywordSettings(boolean newCommand){
        openKeywordSettings(null, newCommand);
    }

    public static void openKeywordSettings(KeywordData data, boolean newCommand){

        String commandName = "";
        String message = "";
        String userLevel = "everyone";
        int cooldown = 0;
        boolean isDefault = false;

        if(!newCommand && data != null){
            commandName = data.getKeyword();
            message = data.getMessage();
            userLevel = data.getUserLevel();
            cooldown = data.getCooldown();
        }

        commandSettingsPanel.removeAll();
        commandSettingsPanel.setLayout(null);
        commandSettingsPanel.setBounds(0,0,600,400);
        commandSettingsPanel.setBackground(Defaults.COLOR3);
        LangLabel langLabel;
        if(newCommand) langLabel = new LangLabel("$ADD_KEYWORD$");
        else langLabel = new LangLabel("$EDIT_KEYWORD$");
        langLabel.setFont(Defaults.MAIN_FONT.deriveFont(20f));
        langLabel.setBounds(10,5,400, 40);
        langLabel.setForeground(Defaults.FOREGROUND_A);
        commandSettingsPanel.add(langLabel);


        TextInput keywordInput = new TextInput("$KEYWORD_NAME_INPUT$", "$KEYWORD_NAME_INPUT_DESC$", commandName, 1);
        keywordInput.setBounds(10,50,600, keywordInput.getPreferredSize().height);
        keywordInput.textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyChar() == '\n') keywordInput.textArea.setText(keywordInput.textArea.getText().replace("\n", ""));
                if(e.getKeyChar() == ' ') keywordInput.textArea.setText(keywordInput.textArea.getText().replace(" ", ""));
            }
        });


        commandSettingsPanel.add(keywordInput);

        TextInput messageInput = new TextInput("$MESSAGE_INPUT$", "$MESSAGE_INPUT_KEYWORD_DESC$", message, 3);
        messageInput.setBounds(10,120,600, messageInput.getPreferredSize().height);

        LangLabel userLevelText = new LangLabel("$USER_LEVEL_SETTING$");
        userLevelText.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        userLevelText.setOpaque(false);
        userLevelText.setForeground(Defaults.FOREGROUND_A);
        userLevelText.setPreferredSize(new Dimension(450, 30));

        commandSettingsPanel.add(userLevelText);

        setUserLevel(userLevel);
        userLevelButton.setUI(Defaults.settingsButtonUI);
        userLevelButton.setBackground(Defaults.COLOR2);
        userLevelButton.setForeground(Defaults.FOREGROUND_A);
        userLevelButton.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        userLevelButton.setBorder(BorderFactory.createEmptyBorder());
        userLevelButton.addActionListener(e -> {
            Window.destroyContextMenu();
            Window.addContextMenu(new UserLevelMenu());
        });

        commandSettingsPanel.add(userLevelButton);

        LangLabel cooldownText = new LangLabel("$COOLDOWN_LABEL$");
        cooldownText.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        cooldownText.setOpaque(false);
        cooldownText.setForeground(Defaults.FOREGROUND_A);
        cooldownText.setPreferredSize(new Dimension(450, 30));


        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 300, 0);
        LangLabel sliderValue = new LangLabel("");

        sliderValue.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        sliderValue.setTextLangFormat("$COOLDOWN$", 0);
        sliderValue.setForeground(Defaults.FOREGROUND_A);

        slider.setUI(new LightSliderUI(slider));

        slider.setBackground(Defaults.COLOR3);
        slider.setBorder(BorderFactory.createEmptyBorder());
        slider.addChangeListener(e -> {
            if (slider.getValue() == 1) {
                sliderValue.setTextLangFormat("$COOLDOWN_SINGULAR$", slider.getValue());

            } else {
                sliderValue.setTextLangFormat("$COOLDOWN$", slider.getValue());
            }
        });
        slider.setValue(cooldown);
        commandSettingsPanel.add(cooldownText);
        commandSettingsPanel.add(slider);
        commandSettingsPanel.add(sliderValue);

        CurvedButtonAlt saveButton = new CurvedButtonAlt("$SAVE$");

        saveButton.setUI(Defaults.settingsButtonUI);
        saveButton.setBackground(Defaults.COLOR2);
        saveButton.setForeground(Defaults.FOREGROUND_A);
        saveButton.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        saveButton.setBorder(BorderFactory.createEmptyBorder());

        String finalCommandName = commandName;
        saveButton.addActionListener(e -> {

            if(checkIfNameExists(keywordInput.getText(), finalCommandName)
                    || keywordInput.getText().equalsIgnoreCase("") || keywordInput.getText().trim().contains(" ") || keywordInput.getText().trim().contains("\n")){
                keywordInput.setErrorRed();
            }
            else {
                if(newCommand){
                    if(checkIfNameExists(keywordInput.getText(), finalCommandName) || keywordInput.getText().equalsIgnoreCase("")
                            || keywordInput.getText().trim().contains(" ") || keywordInput.getText().trim().contains("\n")){
                        keywordInput.setErrorRed();
                    }
                    else {
                        KeywordData data1 = new KeywordData(keywordInput.getText().trim());
                        data1.setMessage(messageInput.getText().trim());
                        data1.setUserLevel(KeywordConfigCheckbox.userLevel);
                        data1.setCooldown(slider.getValue());
                        data1.registerKeyword();
                        LoadKeywords.reloadCustomKeywords();
                        CustomKeywords.loadKeywords();
                        DialogBox.closeDialogBox();
                    }
                }
                else {
                    for (KeywordData existingData : KeywordData.getRegisteredKeywords()) {
                        if (existingData.getKeyword().equalsIgnoreCase(finalCommandName)) {
                            existingData.setKeyword(keywordInput.getText().trim());
                            existingData.setMessage(messageInput.getText().trim());
                            existingData.setUserLevel(KeywordConfigCheckbox.userLevel);
                            existingData.setCooldown(slider.getValue());
                            LoadKeywords.reloadCustomKeywords();
                            CustomKeywords.loadKeywords();
                            break;
                        }
                    }
                    DialogBox.closeDialogBox();
                }
            }
        });

        CurvedButtonAlt helpButton = new CurvedButtonAlt("\uF0A3");

        helpButton.setUI(Defaults.settingsButtonUI);
        helpButton.setBackground(Defaults.COLOR2);
        helpButton.setForeground(Defaults.FOREGROUND_A);
        helpButton.setFont(Defaults.SYMBOLS.deriveFont(14f));
        helpButton.setBorder(BorderFactory.createEmptyBorder());

        helpButton.addActionListener(e -> {
            try {
                Utilities.openURL(new URI("https://loquibot.com/Docs.html"));
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
        });


        CurvedButtonAlt cancelButton = new CurvedButtonAlt("$CANCEL$");

        cancelButton.setUI(Defaults.settingsButtonUI);
        cancelButton.setBackground(Defaults.COLOR2);
        cancelButton.setForeground(Defaults.FOREGROUND_A);
        cancelButton.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        cancelButton.setBorder(BorderFactory.createEmptyBorder());

        cancelButton.addActionListener(e -> {
            DialogBox.closeDialogBox();
        });

        CurvedButtonAlt deleteButton = new CurvedButtonAlt("\uF0CE");

        deleteButton.setUI(Defaults.settingsButtonUI);
        deleteButton.setBackground(Defaults.COLOR2);
        deleteButton.setForeground(Color.RED);
        deleteButton.setFont(Defaults.SYMBOLS.deriveFont(16f));
        deleteButton.setBorder(BorderFactory.createEmptyBorder());
        deleteButton.setBounds(555,375,30,30);

        final boolean[] firstClick = {false};

        deleteButton.addActionListener(e -> {
            deleteButton.setBounds(465,375,120,30);

            deleteButton.setFont(Defaults.MAIN_FONT.deriveFont(14f));
            deleteButton.setText("Are you sure?");
            if(firstClick[0]) {
                for(KeywordData data1 : KeywordData.getRegisteredKeywords()){
                    if(data1.getKeyword().equalsIgnoreCase(finalCommandName)){
                        data1.deregisterKeyword();
                        LoadKeywords.reloadCustomKeywords();
                        CustomKeywords.loadKeywords();
                        break;
                    }
                }
                DialogBox.closeDialogBox();
            }
            firstClick[0] = true;

        });


        commandSettingsPanel.add(saveButton);
        commandSettingsPanel.add(cancelButton);


        commandSettingsPanel.setBounds(0,0,600,420);
        userLevelButton.setBounds(110,250,475,30);
        userLevelText.setBounds(10,250, 450, 30);
        cooldownText.setBounds(10,300, 450, 30);
        sliderValue.setBounds(110, 330, 475, sliderValue.getPreferredSize().height + 5);
        slider.setBounds(108, 300, 480, 30);
        saveButton.setBounds(195,370,100,40);
        cancelButton.setBounds(305,370,100,40);
        helpButton.setBounds(15,375,30,30);
        commandSettingsPanel.add(messageInput);
        commandSettingsPanel.add(helpButton);
        if(!newCommand) commandSettingsPanel.add(deleteButton);

        DialogBox.showDialogBox(commandSettingsPanel, true);
    }

    private static boolean checkIfNameExists(String newName, String originalName){
        if(newName.equalsIgnoreCase(originalName)){
            return false;
        }
        boolean exists = false;
        for(KeywordData existingData : KeywordData.getRegisteredKeywords()) {
            if (existingData.getKeyword().equalsIgnoreCase(newName)) {
                exists = true;
                break;
            }
        }
        return exists;
    }

    private static void setUserLevel(String userLevel){
        String text;
        switch (userLevel){
            case "owner":{
                text = "Broadcaster";
                break;
            }
            case "moderator":{
                text = "Moderators";
                break;
            }
            case "twitch_vip":{
                text = "VIPs";
                break;
            }
            case "subscriber":{
                text = "Subscribers";
                break;
            }
            default: text = "Everyone";
        }
        userLevelButton.setTextLang(text);
        KeywordConfigCheckbox.userLevel = userLevel;
    }

    public static class TextInput extends JPanel {
        public final FancyTextArea textArea;
        private final LangLabel titleText = new LangLabel("");
        public TextInput(String text, String description, String defaultInput, int lines){
            int height = 0;
            for(int i = 0; i < lines; i++){
                height += 32;
            }



            LangLabel descriptionText = new LangLabel("");
            descriptionText.setTextLang("<html><div WIDTH=450> " + description + " </div></html>");
            descriptionText.setFont(Defaults.MAIN_FONT.deriveFont(13f));
            descriptionText.setForeground(Defaults.FOREGROUND_B);
            descriptionText.setOpaque(false);
            descriptionText.setPreferredSize(new Dimension(450, descriptionText.getPreferredSize().height));
            descriptionText.setBounds(100, height+4, 450, descriptionText.getPreferredSize().height);
            add(descriptionText);


            titleText.setTextLang(text);
            titleText.setFont(Defaults.MAIN_FONT.deriveFont(14f));
            titleText.setOpaque(false);
            titleText.setForeground(Defaults.FOREGROUND_A);
            titleText.setPreferredSize(new Dimension(450, 30));
            titleText.setBounds(0,0, 450, 30);

            textArea = new FancyTextArea(false, false);
            textArea.setText(defaultInput);
            textArea.clearUndo();
            textArea.setBounds(100,0,475, height);
            setPreferredSize(new Dimension(600, height+descriptionText.getPreferredSize().height+20));

            setLayout(null);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            setBackground(Defaults.COLOR3);
            add(textArea);
            add(titleText);
        }
        public String getText(){
            return textArea.getText();
        }
        public void setEditable(boolean editable){
            textArea.setEditable(editable);
        }
        public void setErrorRed(){
            titleText.setForeground(Color.RED);
        }
    }
    private static class UserLevelMenu extends ContextMenu {

        public UserLevelMenu() {

            addButton(new ContextButton("Everyone", () -> setUserLevel("everyone")));
            addButton(new ContextButton("Subscribers", () -> setUserLevel("subscriber")));
            addButton(new ContextButton("VIPs", () -> setUserLevel("twitch_vip")));
            addButton(new ContextButton("Moderators", () -> setUserLevel("moderator")));
            addButton(new ContextButton("Broadcaster", () -> setUserLevel("owner")));
        }
    }
}
