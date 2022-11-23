package com.alphalaneous.Settings;

import com.alphalaneous.ChatBot.ServerBot;
import com.alphalaneous.Main;
import com.alphalaneous.Services.GeometryDash.GDAPI;
import com.alphalaneous.Services.GeometryDash.RequestFunctions;
import com.alphalaneous.Services.GeometryDash.Requests;
import com.alphalaneous.Services.Twitch.TwitchAccount;
import com.alphalaneous.Settings.Logs.LoggedID;
import com.alphalaneous.Swing.Components.*;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Windows.DialogBox;
import jdash.common.entity.GDLevel;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ReportedIDs {

    private static final ListView listView = new ListView("$REPORTED_ID_SETTINGS$");

    public static JPanel createPanel(){

        return listView;
    }


    public static void loadIDs(){
        listView.clearElements();

        ArrayList<JSONObject> finalReportedIDs = new ArrayList<>(Requests.reportedIDs);

        for(JSONObject reportedID : finalReportedIDs) {
            if (!exists(reportedID.getString("id"))) {
                listView.addElement(createButton(String.valueOf(reportedID.getString("id"))));
            }
        }
    }

    private static boolean exists(String id){
        for(Component component : listView.getAddedComponents()){
            //if(component instanceof CurvedButton){
                if(((CurvedButton) component).getIdentifier().equalsIgnoreCase(id)){
                    return true;
                }
            //}
        }
        return false;
    }

    public static CurvedButton createButton(String text){
        ListButton button = new ListButton(text, 120);
        button.addActionListener(e -> new Thread(() -> {

            JPanel panel = new JPanel();
            panel.setBackground(Defaults.COLOR6);
            panel.setBounds(0, 0,400,350);
            panel.setPreferredSize(new Dimension(400,400));

            panel.setLayout(null);
            FancyTextArea reason = new FancyTextArea(false, false);
            reason.setEditable(false);
            reason.setWrapStyleWord(true);
            reason.setLineWrap(true);

            StringBuilder builder = new StringBuilder();
            builder.append("--------------------------------------------");
            builder.append("\n");

            for(JSONObject object : Requests.reportedIDs){
                if(object.getString("id").equalsIgnoreCase(text)){
                    builder.append("Username: ").append(object.getString("username"));
                    builder.append("\n");
                    builder.append("UserID: ").append(object.getString("userID"));
                    builder.append("\n");
                    builder.append("Reason: ").append(object.getString("reason"));
                    builder.append("\n");
                    builder.append("--------------------------------------------");
                    builder.append("\n");
                }
            }
            builder.deleteCharAt(builder.length()-1);
            reason.setText(builder.toString());

            LangLabel reportTitle = new LangLabel("");
            reportTitle.setTextLangFormat("$REPORTED_TITLE$", text);
            reportTitle.setBounds(10,10,380,30);
            reportTitle.setForeground(Defaults.FOREGROUND_A);
            reportTitle.setFont(Defaults.MAIN_FONT.deriveFont(20f));
            LangLabel label = new LangLabel("$REASON_INPUT$");
            label.setBounds(10,50,380,30);
            label.setForeground(Defaults.FOREGROUND_A);
            label.setFont(Defaults.MAIN_FONT.deriveFont(14f));
            reason.setBounds(10,80, 380,200);

            CurvedButton cancelButton = new CurvedButton("$CANCEL$");
            cancelButton.addActionListener(f -> DialogBox.closeDialogBox());
            cancelButton.setBounds(10,300, 185, 40);
            cancelButton.setFont(Defaults.MAIN_FONT.deriveFont(14f));
            cancelButton.setForeground(Defaults.FOREGROUND_A);
            cancelButton.setUI(Defaults.settingsButtonUI);

            CurvedButton globallyBlockButton = new CurvedButton("$GLOBALLY_BLOCK_SUBMIT$");
            globallyBlockButton.addActionListener(f -> {

                new Thread(() -> {
                    DialogBox.closeDialogBox();

                    JPanel enterReasonPanel = new JPanel();
                    enterReasonPanel.setBounds(0,0,400,270);
                    enterReasonPanel.setLayout(null);

                    LangLabel titleLabel = new LangLabel("Globally Block " + text + "?");
                    titleLabel.setFont(Defaults.MAIN_FONT.deriveFont(18f));
                    titleLabel.setForeground(Defaults.FOREGROUND_A);
                    titleLabel.setBounds(10,10,300,30);

                    enterReasonPanel.add(titleLabel);

                    LangLabel enterReasonLabel = new LangLabel("$REASON_INPUT$");
                    enterReasonLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));
                    enterReasonLabel.setForeground(Defaults.FOREGROUND_A);
                    enterReasonLabel.setBounds(10,50,300,30);

                    enterReasonPanel.add(enterReasonLabel);

                    FancyTextArea reasonInput = new FancyTextArea(false, false);
                    reasonInput.setBounds(10,90,380, 100);
                    enterReasonPanel.add(reasonInput);
                    reasonInput.setWrapStyleWord(true);
                    reasonInput.setLineWrap(true);

                    CurvedButton cancelButtonA = new CurvedButton("$CANCEL$");
                    cancelButtonA.addActionListener(g -> DialogBox.closeDialogBox());
                    cancelButtonA.setBounds(10,210, 185, 40);
                    cancelButtonA.setFont(Defaults.MAIN_FONT.deriveFont(14f));
                    cancelButtonA.setForeground(Defaults.FOREGROUND_A);
                    cancelButtonA.setUI(Defaults.settingsButtonUI);

                    enterReasonPanel.add(cancelButtonA);

                    CurvedButton submitButtonA = new CurvedButton("$SUBMIT$");
                    submitButtonA.addActionListener(g -> {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("request_type", "globally_block_id");
                        jsonObject.put("id", text);
                        jsonObject.put("reason", reasonInput.getText());
                        Main.sendToServer(jsonObject.toString());
                        DialogBox.closeDialogBox();

                    });
                    submitButtonA.setBounds(205,210, 185, 40);
                    submitButtonA.setFont(Defaults.MAIN_FONT.deriveFont(14f));
                    submitButtonA.setForeground(Defaults.FOREGROUND_A);
                    submitButtonA.setUI(Defaults.settingsButtonUI);

                    enterReasonPanel.add(submitButtonA);

                    DialogBox.showDialogBox(enterReasonPanel);



                }).start();
            });
            globallyBlockButton.setBounds(205,300, 185, 40);
            globallyBlockButton.setFont(Defaults.MAIN_FONT.deriveFont(14f));
            globallyBlockButton.setForeground(Defaults.FOREGROUND_A);
            globallyBlockButton.setUI(Defaults.settingsButtonUI);

            panel.add(reportTitle);
            panel.add(label);
            panel.add(reason);
            panel.add(cancelButton);
            panel.add(globallyBlockButton);

            DialogBox.showDialogBox(panel, true);

        }).start());
        return button;
    }
}
