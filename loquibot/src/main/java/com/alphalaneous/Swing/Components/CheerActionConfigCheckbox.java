package com.alphalaneous.Swing.Components;

import com.alphalaneous.Interactive.CheerActions.CheerActionData;
import com.alphalaneous.Interactive.CheerActions.LoadCheerActions;
import com.alphalaneous.Interactive.Keywords.KeywordData;
import com.alphalaneous.Interactive.Keywords.LoadKeywords;
import com.alphalaneous.Swing.BrowserWindow;
import com.alphalaneous.Swing.ThemedComponents.ThemedConfigCheckbox;
import com.alphalaneous.Tabs.ChatbotPages.CustomCheerActions;
import com.alphalaneous.Tabs.ChatbotPages.CustomKeywords;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Windows.Window;
import org.apache.commons.text.StringEscapeUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class CheerActionConfigCheckbox extends JPanel {

    private static final ArrayList<CheerActionConfigCheckbox> keywordConfigCheckboxes = new ArrayList<>();

    private final ThemedConfigCheckbox themedCheckbox;

    public CheerActionConfigCheckbox(CheerActionData cheerActionData){


        themedCheckbox = new ThemedConfigCheckbox(cheerActionData.getName(), StringEscapeUtils.escapeHtml4(cheerActionData.getMessage()), () -> openCheerActionSettings(cheerActionData), false, null, cheerActionData);

        themedCheckbox.setChecked(cheerActionData.isEnabled());
        setLayout(null);
        setOpaque(false);
        themedCheckbox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                for(CheerActionData data : CheerActionData.getRegisteredCheerActions()){
                    if(data.getName().equalsIgnoreCase(cheerActionData.getName())){
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
        for(CheerActionConfigCheckbox commandConfigCheckbox : keywordConfigCheckboxes){
            commandConfigCheckbox.resize(width);
        }
    }

    private static final JPanel commandSettingsPanel = new JPanel();

    private static final CurvedButton userLevelButton = new CurvedButton("");
    private static String userLevel = "everyone";

    public static void openCheerActionSettings(CheerActionData data){
        openCheerActionSettings(data, false);
    }
    public static void openCheerActionSettings(boolean newCommand){
        openCheerActionSettings(null, newCommand);
    }

    public static void openCheerActionSettings(CheerActionData data, boolean newCommand){

        String commandName = "";
        long cheerAmount = 1;
        String message = "";
        String userLevel = "everyone";

        if(!newCommand && data != null){
            commandName = data.getName();
            cheerAmount = data.getCheerAmount();
            message = data.getMessage();
            userLevel = data.getUserLevel();
        }

        commandSettingsPanel.removeAll();
        commandSettingsPanel.setLayout(null);
        commandSettingsPanel.setBounds(0,0,600,400);
        commandSettingsPanel.setBackground(Defaults.COLOR3);
        LangLabel langLabel;
        if(newCommand) langLabel = new LangLabel("$ADD_CHEER_ACTION$");
        else langLabel = new LangLabel("$EDIT_CHEER_ACTION$");
        langLabel.setFont(Defaults.MAIN_FONT.deriveFont(20f));
        langLabel.setBounds(10,5,400, 40);
        langLabel.setForeground(Defaults.FOREGROUND_A);
        commandSettingsPanel.add(langLabel);


        TextInput cheerNameInput = new TextInput("$CHEER_ACTION_NAME_INPUT$", "$CHEER_ACTION_NAME_INPUT_DESC$", commandName, 1);
        cheerNameInput.setBounds(10,50,600, cheerNameInput.getPreferredSize().height);
        cheerNameInput.textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyChar() == '\n') cheerNameInput.textArea.setText(cheerNameInput.textArea.getText().replace("\n", ""));
                if(e.getKeyChar() == ' ') cheerNameInput.textArea.setText(cheerNameInput.textArea.getText().replace(" ", ""));
            }
        });

        commandSettingsPanel.add(cheerNameInput);

        TextInput cheerAmountInput = new TextInput("$CHEER_ACTION_AMOUNT_INPUT$", "$CHEER_ACTION_AMOUNT_INPUT_DESC$", String.valueOf(cheerAmount), 1, true);
        cheerAmountInput.setBounds(10,120,600, cheerNameInput.getPreferredSize().height);
        cheerAmountInput.textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyChar() == '\n') cheerNameInput.textArea.setText(cheerNameInput.textArea.getText().replace("\n", ""));
                if(e.getKeyChar() == ' ') cheerNameInput.textArea.setText(cheerNameInput.textArea.getText().replace(" ", ""));
            }
        });

        commandSettingsPanel.add(cheerAmountInput);

        TextInput messageInput = new TextInput("$MESSAGE_INPUT$", "$MESSAGE_INPUT_CHEER_DESC$", message, 3);
        messageInput.setBounds(10,190,600, messageInput.getPreferredSize().height);

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

        CurvedButton saveButton = new CurvedButton("$SAVE$");

        saveButton.setUI(Defaults.settingsButtonUI);
        saveButton.setBackground(Defaults.COLOR2);
        saveButton.setForeground(Defaults.FOREGROUND_A);
        saveButton.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        saveButton.setBorder(BorderFactory.createEmptyBorder());

        String finalCommandName = commandName;
        saveButton.addActionListener(e -> {

            if(checkIfNameExists(cheerNameInput.getText(), finalCommandName)
                    || cheerNameInput.getText().equalsIgnoreCase("") || cheerAmountInput.getText().equalsIgnoreCase("") ||cheerNameInput.getText().trim().contains(" ") || cheerNameInput.getText().trim().contains("\n")){
                cheerNameInput.setErrorRed();
            }
            else {
                if(newCommand){
                    if(checkIfNameExists(cheerNameInput.getText(), finalCommandName) || cheerNameInput.getText().equalsIgnoreCase("")
                            || cheerNameInput.getText().trim().contains(" ") || cheerNameInput.getText().trim().contains("\n")){
                        cheerNameInput.setErrorRed();
                    }
                    else {
                        CheerActionData data1 = new CheerActionData(cheerNameInput.getText().trim());
                        data1.setMessage(messageInput.getText().trim());
                        data1.setUserLevel(CheerActionConfigCheckbox.userLevel);
                        data1.setCheerAmount(Long.parseLong(cheerAmountInput.getText()));
                        data1.registerCheerAction();
                        LoadCheerActions.reloadCustomCheerActions();
                        CheerActionData.saveCustomCheerActions();
                        CustomCheerActions.loadCheerActions();
                        DialogBox.closeDialogBox();
                    }
                }
                else {
                    for (CheerActionData existingData : CheerActionData.getRegisteredCheerActions()) {
                        if (existingData.getName().equalsIgnoreCase(finalCommandName)) {
                            existingData.setName(cheerNameInput.getText().trim());
                            existingData.setCheerAmount(Long.parseLong(cheerAmountInput.getText()));
                            existingData.setMessage(messageInput.getText().trim());
                            existingData.setUserLevel(CheerActionConfigCheckbox.userLevel);
                            LoadCheerActions.reloadCustomCheerActions();
                            CheerActionData.saveCustomCheerActions();
                            CustomCheerActions.loadCheerActions();
                            break;
                        }
                    }
                    DialogBox.closeDialogBox();
                }
            }
        });

        CurvedButton helpButton = new CurvedButton("\uF0A3");

        helpButton.setUI(Defaults.settingsButtonUI);
        helpButton.setBackground(Defaults.COLOR2);
        helpButton.setForeground(Defaults.FOREGROUND_A);
        helpButton.setFont(Defaults.SYMBOLS.deriveFont(14f));
        helpButton.setBorder(BorderFactory.createEmptyBorder());

        helpButton.addActionListener(e -> new BrowserWindow("https://loquibot.com/Docs.html"));


        CurvedButton cancelButton = new CurvedButton("$CANCEL$");

        cancelButton.setUI(Defaults.settingsButtonUI);
        cancelButton.setBackground(Defaults.COLOR2);
        cancelButton.setForeground(Defaults.FOREGROUND_A);
        cancelButton.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        cancelButton.setBorder(BorderFactory.createEmptyBorder());

        cancelButton.addActionListener(e -> DialogBox.closeDialogBox());

        CurvedButton deleteButton = new CurvedButton("\uF0CE");

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
                for(CheerActionData data1 : CheerActionData.getRegisteredCheerActions()){
                    if(data1.getName().equalsIgnoreCase(finalCommandName)){
                        data1.deregisterCheerAction();
                        LoadCheerActions.reloadCustomCheerActions();
                        CustomCheerActions.loadCheerActions();
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
        userLevelButton.setBounds(110,320,475,30);
        userLevelText.setBounds(10,320, 450, 30);
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
        for(CheerActionData existingData : CheerActionData.getRegisteredCheerActions()) {
            if (existingData.getName().equalsIgnoreCase(newName)) {
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
        userLevelButton.setText(text);
        CheerActionConfigCheckbox.userLevel = userLevel;
    }

    public static class TextInput extends JPanel {
        public final FancyTextArea textArea;
        private final LangLabel titleText = new LangLabel("");
        public TextInput(String text, String description, String defaultInput, int lines){
            this(text, description, defaultInput, lines, false);
        }
        public TextInput(String text, String description, String defaultInput, int lines, boolean intOnly){
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

            textArea = new FancyTextArea(intOnly, true);
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
