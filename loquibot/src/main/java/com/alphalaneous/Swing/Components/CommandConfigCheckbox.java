package com.alphalaneous.Swing.Components;

import com.alphalaneous.Interactive.Commands.LoadCommands;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Tabs.ChatbotPages.CustomCommands;
import com.alphalaneous.Tabs.ChatbotPages.DefaultCommands;

import com.alphalaneous.Interactive.Commands.CommandData;
import com.alphalaneous.Swing.ThemedComponents.ThemedConfigCheckbox;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Utils.Utilities;
import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Windows.Window;
import org.apache.commons.text.StringEscapeUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class CommandConfigCheckbox extends JPanel {

    private static final ArrayList<CommandConfigCheckbox> commandConfigCheckboxes = new ArrayList<>();

    private final ThemedConfigCheckbox themedCheckbox;


    public CommandConfigCheckbox(CommandData commandData){

        String prefix = "";
        String defaultCommandPrefix = "!";
        String geometryDashCommandPrefix = "!";

        if(SettingsHandler.getSettings("defaultCommandPrefix").exists()) defaultCommandPrefix = SettingsHandler.getSettings("defaultCommandPrefix").asString();
        if(SettingsHandler.getSettings("geometryDashCommandPrefix").exists()) geometryDashCommandPrefix = SettingsHandler.getSettings("geometryDashCommandPrefix").asString();

        if(commandData.isDefault()) prefix = defaultCommandPrefix;
        if(commandData.isGD()) prefix = geometryDashCommandPrefix;

        themedCheckbox = new ThemedConfigCheckbox(prefix + commandData.getCommand(), StringEscapeUtils.escapeHtml4(commandData.getDescription().replace("%p", prefix)), () -> openCommandSettings(commandData), true, commandData);

        themedCheckbox.setChecked(commandData.isEnabled());
        setLayout(null);
        setOpaque(false);
        themedCheckbox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                for(CommandData data : CommandData.getRegisteredCommands()){
                    if(data.getCommand().equalsIgnoreCase(commandData.getCommand())){
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
        commandConfigCheckboxes.add(this);
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
        for(CommandConfigCheckbox commandConfigCheckbox : commandConfigCheckboxes){
            commandConfigCheckbox.resize(width);
        }
    }

    private static final JPanel commandSettingsPanel = new JPanel();

    private static final CurvedButtonAlt userLevelButton = new CurvedButtonAlt("");
    private static String userLevel = "everyone";

    public static void openCommandSettings(CommandData data){
        openCommandSettings(data, false);
    }
    public static void openCommandSettings(boolean newCommand){
        openCommandSettings(null, newCommand);
    }

    public static void openCommandSettings(CommandData data, boolean newCommand){

        String commandName = "";
        String message = "";
        String userLevel = "everyone";
        List<Object> aliases = new ArrayList<>();
        int cooldown = 0;
        boolean isDefault = false;

        if(!newCommand && data != null){
            commandName = data.getCommand();
            message = data.getMessage();
            userLevel = data.getUserLevel();
            cooldown = data.getCooldown();
            aliases = data.getAliases();
            isDefault = data.isDefault();
        }

        commandSettingsPanel.removeAll();
        commandSettingsPanel.setLayout(null);
        commandSettingsPanel.setBounds(0,0,600,600);
        commandSettingsPanel.setBackground(Defaults.COLOR3);
        LangLabel langLabel;
        if(newCommand) langLabel = new LangLabel("$ADD_COMMAND$");
        else langLabel = new LangLabel("$EDIT_COMMAND$");
        langLabel.setFont(Defaults.MAIN_FONT.deriveFont(20f));
        langLabel.setBounds(10,5,400, 40);
        langLabel.setForeground(Defaults.FOREGROUND_A);
        commandSettingsPanel.add(langLabel);

        String prefix = "";
        String defaultCommandPrefix = "!";
        String geometryDashCommandPrefix = "!";

        if(SettingsHandler.getSettings("defaultCommandPrefix").exists()) defaultCommandPrefix = SettingsHandler.getSettings("defaultCommandPrefix").asString();
        if(SettingsHandler.getSettings("geometryDashCommandPrefix").exists()) geometryDashCommandPrefix = SettingsHandler.getSettings("geometryDashCommandPrefix").asString();
        if(data != null) {
            if (data.isDefault()) prefix = defaultCommandPrefix;
            if (data.isGD()) prefix = geometryDashCommandPrefix;
        }
        TextInput commandNameInput = new TextInput("$COMMAND_NAME_INPUT$", "$COMMAND_NAME_INPUT_DESC$", prefix + commandName, 1);
        commandNameInput.setBounds(10,50,600, commandNameInput.getPreferredSize().height);
        commandNameInput.textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyChar() == '\n') commandNameInput.textArea.setText(commandNameInput.textArea.getText().replace("\n", ""));
                if(e.getKeyChar() == ' ') commandNameInput.textArea.setText(commandNameInput.textArea.getText().replace(" ", ""));
            }
        });


        commandSettingsPanel.add(commandNameInput);

        TextInput messageInput = new TextInput("$MESSAGE_INPUT$", "$MESSAGE_INPUT_DESC$", message, 3);
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
            com.alphalaneous.Windows.Window.destroyContextMenu();
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

        TextInput aliasInput;
        if(aliases != null){
            aliasInput = new TextInput("$ALIAS_INPUT$", "$ALIAS_INPUT_DESC$", aliases.toString().replace("[", "").replace("]",""), 1);
        }
        else{
            aliasInput = new TextInput("$ALIAS_INPUT$", "$ALIAS_INPUT_DESC$", "", 1);
        }


        CurvedButtonAlt saveButton = new CurvedButtonAlt("$SAVE$");

        saveButton.setUI(Defaults.settingsButtonUI);
        saveButton.setBackground(Defaults.COLOR2);
        saveButton.setForeground(Defaults.FOREGROUND_A);
        saveButton.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        saveButton.setBorder(BorderFactory.createEmptyBorder());

        boolean finalIsDefault = isDefault;
        String finalCommandName = commandName;
        saveButton.addActionListener(e -> {

            if(data != null && ((checkIfDefaultExists(data) && !finalIsDefault) || checkIfNameExists(commandNameInput.getText(), finalCommandName)
                    || commandNameInput.getText().equalsIgnoreCase("") || commandNameInput.getText().trim().contains(" ") || commandNameInput.getText().trim().contains("\n"))){
                commandNameInput.setErrorRed();
            }
            else {
                if(newCommand){
                    if(checkIfNameExists(commandNameInput.getText(), finalCommandName) || commandNameInput.getText().equalsIgnoreCase("")
                            || commandNameInput.getText().trim().contains(" ") || commandNameInput.getText().trim().contains("\n")){
                        commandNameInput.setErrorRed();
                    }
                    else {
                        CommandData data1 = new CommandData(commandNameInput.getText().trim());
                        data1.setMessage(messageInput.getText().trim());
                        ArrayList<Object> aliasesToSave = new ArrayList<>();
                        String[] aliasSplit = aliasInput.getText().split(",");
                        for (String alias : aliasSplit) aliasesToSave.add(alias.trim());
                        data1.setAliases(aliasesToSave);
                        data1.setDescription(messageInput.getText().trim());
                        data1.setUserLevel(CommandConfigCheckbox.userLevel);
                        data1.setCooldown(slider.getValue());
                        data1.registerCommand();
                        LoadCommands.reloadCustomCommands();
                        CustomCommands.loadCommands();
                        DialogBox.closeDialogBox();
                    }
                }
                else {
                    for (CommandData existingData : CommandData.getRegisteredCommands()) {
                        if (existingData.getCommand().equalsIgnoreCase(finalCommandName)) {

                            if (!finalIsDefault) {
                                existingData.setCommand(commandNameInput.getText().trim());
                                existingData.setMessage(messageInput.getText().trim());
                                ArrayList<Object> aliasesToSave = new ArrayList<>();
                                String[] aliasSplit = aliasInput.getText().split(",");
                                for (String alias : aliasSplit) aliasesToSave.add(alias.trim());
                                existingData.setAliases(aliasesToSave);
                                existingData.setDescription(messageInput.getText().trim());
                            }
                            existingData.setUserLevel(CommandConfigCheckbox.userLevel);
                            existingData.setCooldown(slider.getValue());

                            CustomCommands.loadCommands();
                            DefaultCommands.loadCommands();
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
        deleteButton.setBounds(555,455,30,30);

        final boolean[] firstClick = {false};

        deleteButton.addActionListener(e -> {
            deleteButton.setBounds(465,455,120,30);

            deleteButton.setFont(Defaults.MAIN_FONT.deriveFont(14f));
            deleteButton.setText("Are you sure?");
            if(firstClick[0]) {
                for(CommandData data1 : CommandData.getRegisteredCommands()){
                    if(data1.getCommand().equalsIgnoreCase(finalCommandName)){
                        data1.deRegisterCommand();
                        LoadCommands.reloadCustomCommands();
                        CustomCommands.loadCommands();
                        break;
                    }
                }
                DialogBox.closeDialogBox();
            }
            firstClick[0] = true;

        });


        commandSettingsPanel.add(saveButton);
        commandSettingsPanel.add(cancelButton);


        if(isDefault){
            commandSettingsPanel.setBounds(0,0,600,300);
            userLevelButton.setBounds(110,120,475,30);
            userLevelText.setBounds(10,120, 450, 30);
            cooldownText.setBounds(10,160, 450, 30);
            sliderValue.setBounds(110, 190, 475, sliderValue.getPreferredSize().height + 5);
            slider.setBounds(108, 160, 480, 30);
            saveButton.setBounds(195,250,100,40);
            cancelButton.setBounds(305,250,100,40);
            commandNameInput.setEditable(false);
        }
        else{
            commandSettingsPanel.setBounds(0,0,600,500);
            userLevelButton.setBounds(110,250,475,30);
            userLevelText.setBounds(10,250, 450, 30);
            cooldownText.setBounds(10,300, 450, 30);
            sliderValue.setBounds(110, 330, 475, sliderValue.getPreferredSize().height + 5);
            slider.setBounds(108, 300, 480, 30);
            saveButton.setBounds(195,450,100,40);
            cancelButton.setBounds(305,450,100,40);
            helpButton.setBounds(15,455,30,30);
            aliasInput.setBounds(10,370,600, aliasInput.getPreferredSize().height);
            commandSettingsPanel.add(messageInput);
            commandSettingsPanel.add(aliasInput);
            commandSettingsPanel.add(helpButton);
            if(!newCommand) commandSettingsPanel.add(deleteButton);
        }
        DialogBox.showDialogBox(commandSettingsPanel, true);
    }

    public static boolean checkIfDefaultExists(CommandData data){
        boolean exists = false;
        for(CommandData existingData : CommandData.getRegisteredCommands()) {
            if (existingData.getCommand().equalsIgnoreCase(data.getCommand())) {
                if(existingData.isDefault()){
                    exists = true;
                    break;
                }
            }
        }
        return exists;
    }
    public static boolean checkIfNameExists(String newName, String originalName){
        if(newName.equalsIgnoreCase(originalName)){
            return false;
        }
        boolean exists = false;
        for(CommandData existingData : CommandData.getRegisteredCommands()) {
            if (existingData.getCommand().equalsIgnoreCase(newName)) {
                exists = true;
                break;
            }
        }
        if(CommandData.getRegisteredAliases().containsKey(newName)){
            exists = true;
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
        CommandConfigCheckbox.userLevel = userLevel;
    }

    public static class TextInput extends JPanel {
        private final FancyTextArea textArea;
        private final LangLabel titleText = new LangLabel("");
        TextInput(String text, String description, String defaultInput, int lines){
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
