package com.alphalaneous.Settings;

import com.alphalaneous.*;
import com.alphalaneous.Interactive.ChannelPoints.LoadPoints;
import com.alphalaneous.Services.Twitch.TwitchAPI;
import com.alphalaneous.Swing.BrowserWindow;
import com.alphalaneous.Swing.Components.*;
import com.alphalaneous.Interactive.ChannelPoints.ChannelPointData;
import com.alphalaneous.Interactive.ChannelPoints.ChannelPointReward;
import com.alphalaneous.Interactive.Commands.CommandHandler;
import com.alphalaneous.Services.Twitch.TwitchAccount;
import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Windows.DialogBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

public class ChannelPoints {

    private static ListView listView = new ListView("$POINTS_LIST$");
    private static final LangLabel notAvailable = new LangLabel("$CHANNEL_POINTS_UNAVAILABLE$");
    private static final SettingsComponent notAvailableComponent = new SettingsComponent(notAvailable, new Dimension(1500,32)){
        @Override
        protected void refreshUI(){
            notAvailable.setForeground(Defaults.FOREGROUND_A);
        }
    };;

    public static JPanel createPanel(){
        listView = new ListView("$POINTS_LIST$"); // Redundancy fixes weird text bug
        notAvailable.setForeground(Defaults.FOREGROUND_A);
        notAvailable.setFont(Defaults.MAIN_FONT.deriveFont(16f));
        listView.addButton("\uF078", ChannelPoints::refresh);

        refresh();
        return listView;
    }

    public static void clear(){
        listView.clearElements();
    }

    public static void refresh(){
        if(TwitchAccount.broadcaster_type != null) {
            if (SettingsHandler.getSettings("twitchEnabled").asBoolean()) {
                try {
                    listView.clearElements();
                    if (TwitchAccount.broadcaster_type.equalsIgnoreCase("affiliate")
                            || TwitchAccount.broadcaster_type.equalsIgnoreCase("partner")) {
                        ArrayList<ChannelPointReward> rewards = TwitchAPI.getChannelPoints();
                        for (ChannelPointReward reward : rewards) {
                            listView.addElement(createButton(reward.getTitle(), reward.getBgColor(), reward.getIcon(), reward.isDefaultIcon()));
                        }
                    } else {
                        listView.addElement(notAvailableComponent);
                    }
                } catch (Exception e) {
                    Main.logger.error(e.getLocalizedMessage(), e);

                }
            }
        }
    }


    public static CurvedButton createButton(String command, Color color, Icon icon, boolean defaultIcon){
        JButtonUI colorUI = new JButtonUI();
        colorUI.setBackground(color);
        colorUI.setHover(color);
        colorUI.setSelect(color.darker());
        CurvedButton button = new CurvedButton("");
        JLabel pointLabel = new JLabel("<html><div style=\"text-align: center;\" width=120; > " + command + " </div></html>", SwingConstants.CENTER);
        JLabel pointIcon = new JLabel(icon);
        pointLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        button.setBackground(color);
        button.setLayout(null);
        pointLabel.setBounds(0, 20, 120, 120);
        pointIcon.setBounds(0, -10, 120, 120);

        button.add(pointLabel);
        button.add(pointIcon);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setUI(colorUI);
        double brightness = Math.sqrt(color.getRed() * color.getRed() * .241 +
                color.getGreen() * color.getGreen() * .691 +
                color.getBlue() * color.getBlue() * .068);

        if (brightness > 130) {
            pointLabel.setForeground(Color.BLACK);
        } else {
            pointLabel.setForeground(Color.WHITE);
        }
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setPreferredSize(new Dimension(120, 120));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (brightness > 130) {
                    button.setBackground(button.getBackground().darker());
                } else {
                    button.setBackground(button.getBackground().brighter());

                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {

                    ChannelPointData data = null;
                    for (ChannelPointData existingData : ChannelPointData.getRegisteredPoints()) {
                        if (existingData.getName().equalsIgnoreCase(command)) {
                            data = existingData;
                            break;
                        }
                    }
                    if(data != null) {
                        ChatMessage message = new ChatMessage(new String[]{}, "PointHandler", "PointHandler", "", new String[0], true, true, true, 0, false, false);
                        Main.sendMessage(CommandHandler.replaceBetweenParentheses(message, data.getMessage(), data.getMessage().split(" "), null));
                    }
                }
            }
        });
        button.addActionListener(e -> {

            boolean found = false;

            for (ChannelPointData existingData : ChannelPointData.getRegisteredPoints()) {
                if (existingData.getName().equalsIgnoreCase(command)) {
                    openPointSettings(existingData);
                    found = true;
                    break;
                }
            }
            if(!found){

                ChannelPointData data = new ChannelPointData(command);

                openPointSettings(data, true);
            }
        });
        return button;
    }

    private static final JPanel commandSettingsPanel = new JPanel();

    public static void openPointSettings(ChannelPointData data){
        openPointSettings(data, false);
    }

    public static void openPointSettings(ChannelPointData data, boolean newCommand){

        String commandName = "";
        String message = "";

        if(!newCommand && data != null){
            commandName = data.getName();
            message = data.getMessage();
        }

        commandSettingsPanel.removeAll();
        commandSettingsPanel.setLayout(null);
        commandSettingsPanel.setBounds(0,0,600,400);
        commandSettingsPanel.setBackground(Defaults.COLOR3);
        LangLabel langLabel = new LangLabel("$EDIT_POINT$");
        langLabel.setFont(Defaults.MAIN_FONT.deriveFont(20f));
        langLabel.setBounds(10,5,400, 40);
        langLabel.setForeground(Defaults.FOREGROUND_A);
        commandSettingsPanel.add(langLabel);


        KeywordConfigCheckbox.TextInput keywordInput = new KeywordConfigCheckbox.TextInput("$POINT_NAME_INPUT$", "", commandName, 1);
        keywordInput.setEditable(false);
        if(data != null) keywordInput.textArea.setText(data.getName());
        keywordInput.setBounds(10,50,600, keywordInput.getPreferredSize().height);
        keywordInput.textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyChar() == '\n') keywordInput.textArea.setText(keywordInput.textArea.getText().replace("\n", ""));
                if(e.getKeyChar() == ' ') keywordInput.textArea.setText(keywordInput.textArea.getText().replace(" ", ""));
            }
        });


        commandSettingsPanel.add(keywordInput);

        KeywordConfigCheckbox.TextInput messageInput = new KeywordConfigCheckbox.TextInput("$MESSAGE_INPUT$", "$MESSAGE_INPUT_POINT_DESC$", message, 3);
        messageInput.setBounds(10,120,600, messageInput.getPreferredSize().height);

        CurvedButton saveButton = new CurvedButton("$SAVE$");

        saveButton.setUI(Defaults.settingsButtonUI);
        saveButton.setBackground(Defaults.COLOR2);
        saveButton.setForeground(Defaults.FOREGROUND_A);
        saveButton.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        saveButton.setBorder(BorderFactory.createEmptyBorder());

        String finalCommandName = commandName;
        saveButton.addActionListener(e -> {
                if(newCommand){
                    ChannelPointData data1 = new ChannelPointData(keywordInput.getText().trim());
                    data1.setMessage(messageInput.getText().trim());
                    data1.registerPoint();
                    LoadPoints.reloadCustomPoints();
                    ChannelPointData.saveCustomPoints();
                }
                else {
                    for (ChannelPointData existingData : ChannelPointData.getRegisteredPoints()) {
                        if (existingData.getName().equalsIgnoreCase(finalCommandName)) {
                            existingData.setName(keywordInput.getText().trim());
                            existingData.setMessage(messageInput.getText().trim());
                            LoadPoints.reloadCustomPoints();
                            ChannelPointData.saveCustomPoints();
                            break;
                        }
                    }
                }
            DialogBox.closeDialogBox();

        });

        CurvedButton helpButton = new CurvedButton("\uF0A3");

        helpButton.setUI(Defaults.settingsButtonUI);
        helpButton.setBackground(Defaults.COLOR2);
        helpButton.setForeground(Defaults.FOREGROUND_A);
        helpButton.setFont(Defaults.SYMBOLS.deriveFont(14f));
        helpButton.setBorder(BorderFactory.createEmptyBorder());

        helpButton.addActionListener(e -> {
            new BrowserWindow("https://loquibot.com/Docs.html");
        });


        CurvedButton cancelButton = new CurvedButton("$CANCEL$");

        cancelButton.setUI(Defaults.settingsButtonUI);
        cancelButton.setBackground(Defaults.COLOR2);
        cancelButton.setForeground(Defaults.FOREGROUND_A);
        cancelButton.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        cancelButton.setBorder(BorderFactory.createEmptyBorder());

        cancelButton.addActionListener(e -> {
            DialogBox.closeDialogBox();
        });

        commandSettingsPanel.add(saveButton);
        commandSettingsPanel.add(cancelButton);


        commandSettingsPanel.setBounds(0,0,600,320);
        saveButton.setBounds(195,270,100,40);
        cancelButton.setBounds(305,270,100,40);
        helpButton.setBounds(15,275,30,30);
        commandSettingsPanel.add(messageInput);
        commandSettingsPanel.add(helpButton);

        DialogBox.showDialogBox(commandSettingsPanel, true);
    }
}
