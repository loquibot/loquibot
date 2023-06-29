package com.alphalaneous.Swing.Components;

import com.alphalaneous.ChatBot.ServerBot;
import com.alphalaneous.Services.GeometryDash.RequestFunctions;
import com.alphalaneous.Services.GeometryDash.Requests;
import com.alphalaneous.Services.Twitch.TwitchAccount;
import com.alphalaneous.Services.YouTube.YouTubeAccount;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Windows.Window;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

public class LevelContextMenu extends ContextMenu {

    public LevelContextMenu(int levelPos) {

        boolean isYouTube = RequestsTab.getRequest(levelPos).getLevelData().isYouTube();
        boolean isKick = RequestsTab.getRequest(levelPos).getLevelData().isKick();

        addButton(new ContextButton("Remove", () -> RequestFunctions.skipFunction(levelPos, false)));
        addButton(new ContextButton("Copy", () -> RequestFunctions.copyFunction(levelPos)));
        addButton(new ContextButton("Block ID", () -> RequestFunctions.blockFunction(levelPos)));
        addButton(new ContextButton("Report", () -> {



            reportID(RequestsTab.getRequest(levelPos).getID());
        }));
        //addButton(new ContextButton("Report", () -> RequestFunctions.reportFunction(levelPos)));
        //addButton(new ContextButton("Block User", () -> RequestFunctions.blockFunction(levelPos)));
        //addButton(new ContextButton("Block Creator", () -> RequestFunctions.blockFunction(levelPos)));
        if(!isYouTube && !isKick) addButton(new ContextButton("Moderation", () -> RequestsTab.showModPane(levelPos)));
        addButton(new ContextButton("View in GDBrowser", () -> RequestFunctions.openGDBrowser(levelPos)));
    }

    private static void reportID(long ID){


        JPanel panel = new JPanel();
        panel.setBackground(Defaults.COLOR6);
        panel.setBounds(0, 0,400,350);
        panel.setPreferredSize(new Dimension(400,400));

        panel.setLayout(null);
        FancyTextArea reason = new FancyTextArea(false, false);
        reason.setWrapStyleWord(true);
        reason.setLineWrap(true);
        LangLabel reportTitle = new LangLabel("");
        reportTitle.setTextLangFormat("$REPORT_TITLE$", ID);
        reportTitle.setBounds(10,10,380,30);
        reportTitle.setForeground(Defaults.FOREGROUND_A);
        reportTitle.setFont(Defaults.MAIN_FONT.deriveFont(20f));
        LangLabel label = new LangLabel("$REASON_INPUT$");
        label.setBounds(10,50,380,30);
        label.setForeground(Defaults.FOREGROUND_A);
        label.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        reason.setBounds(10,80, 380,200);

        CurvedButton cancelButton = new CurvedButton("$CANCEL$");
        cancelButton.addActionListener(e -> DialogBox.closeDialogBox());
        cancelButton.setBounds(10,300, 185, 40);
        cancelButton.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        cancelButton.setForeground(Defaults.FOREGROUND_A);
        cancelButton.setUI(Defaults.settingsButtonUI);

        CurvedButton submitButton = new CurvedButton("$SUBMIT$");
        submitButton.addActionListener(e -> {
            JSONObject object = new JSONObject();
            object.put("request_type", "report_id");
            object.put("id", String.valueOf(ID));
            object.put("reason", reason.getText());
            if(TwitchAccount.login != null) {
                object.put("username", TwitchAccount.login);
                object.put("userID", TwitchAccount.id);
                ServerBot.sendMessage(object.toString());
            }
            else if(YouTubeAccount.name != null){
                object.put("username", YouTubeAccount.name);
                object.put("userID", YouTubeAccount.ID);
                ServerBot.sendMessage(object.toString());
            }

            DialogBox.closeDialogBox();
            RequestFunctions.blockFunction(Requests.getPosFromID(ID));
        });
        submitButton.setBounds(205,300, 185, 40);
        submitButton.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        submitButton.setForeground(Defaults.FOREGROUND_A);
        submitButton.setUI(Defaults.settingsButtonUI);

        panel.add(reportTitle);
        panel.add(label);
        panel.add(reason);
        panel.add(cancelButton);
        panel.add(submitButton);

        DialogBox.showDialogBox(panel, true);



    }

}
