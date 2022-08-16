package com.alphalaneous.Windows;

import com.alphalaneous.*;
import com.alphalaneous.Services.GeometryDash.GDAPI;
import com.alphalaneous.Services.GeometryDash.Requests;
import com.alphalaneous.Swing.Components.*;
import com.alphalaneous.Swing.Components.LevelButton;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.Utils.Defaults;
import jdash.common.entity.GDLevel;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import static com.alphalaneous.Utils.Defaults.settingsButtonUI;

public class OfficerWindow {

    //todo Themeing and Functional blocking and unblocking with the servers

    private static final JPanel panel = new JPanel();

    private static final FancyTextArea idInput = new FancyTextArea(true,false);
    private static final FancyTextArea reasonInput = new FancyTextArea(false,true);
    private static final CurvedButton submitButton = createCurvedButton("$GLOBALLY_SUBMIT$");
    private static final CurvedButton blockID = createCurvedButton("$GLOBALLY_BLOCK_ID$");
    private static final CurvedButton unblockID = createCurvedButton("$GLOBALLY_UNBLOCK_ID$");


    private static final LangLabel enterIDLabel = new LangLabel("$ENTER_ID$");
    private static final LangLabel reasonLabel = new LangLabel("$ENTER_GLOBALLY_BLOCK_REASON$");
    private static final LangLabel areYouSureLabel = new LangLabel("$SURE_GLOBALLY_UNBLOCK$");



    private static boolean unblock = false;

    public static void create(){

        /*panel.setBounds(0,0,700,440);
        panel.setLayout(null);

        CurvedButton blockIDButton = createCurvedButton("Block ID");

        blockIDButton.setBounds(10,50,100,30);

        CurvedButton blockUserButton = createCurvedButton("Block User");

        blockUserButton.setBounds(10,90,100,30);

        CurvedButton IDBlocklistButton = createCurvedButton("ID Blocklist");

        IDBlocklistButton.setBounds(10,130,100,30);

        CurvedButton userBlocklistButton = createCurvedButton("User Blocklist");

        userBlocklistButton.setBounds(10,170,100,30);

        panel.add(blockIDButton);
        panel.add(blockUserButton);
        panel.add(IDBlocklistButton);
        panel.add(userBlocklistButton);*/

        panel.setBounds(0,0,680,460);
        panel.setLayout(null);
        panel.setBackground(Defaults.COLOR3);

        blockID.addActionListener(e -> {
            submitButton.setVisible(false);
            reasonInput.setVisible(false);
            areYouSureLabel.setVisible(false);
            reasonLabel.setVisible(false);
            if(Requests.globallyBlockedIDs.containsKey(Long.parseLong(idInput.getText()))){
                enterIDLabel.setForeground(Color.RED);
                enterIDLabel.setTextLang("$ENTER_ID$" + " (Level Already Blocked)");
            }
            else if(!idInput.getText().equalsIgnoreCase("")) {
                unblock = false;
                enterIDLabel.setForeground(Defaults.FOREGROUND_A);
                reasonLabel.setVisible(true);
                areYouSureLabel.setVisible(false);
                reasonInput.setVisible(true);
                submitButton.setBounds(30,200,100,30);
                submitButton.setVisible(true);
            }
            else{
                enterIDLabel.setForeground(Color.RED);
            }
        });


        unblockID.addActionListener(e -> {
            if(!idInput.getText().equalsIgnoreCase("")) {
                reasonLabel.setVisible(false);
                enterIDLabel.setForeground(Defaults.FOREGROUND_A);
                areYouSureLabel.setVisible(true);
                unblock = true;
                reasonInput.setVisible(false);
                submitButton.setBounds(30,160,100,30);
                submitButton.setVisible(true);
            }
            else{
                enterIDLabel.setForeground(Color.RED);
            }
        });

        enterIDLabel.setBounds(30,20,220,30);
        idInput.setBounds(30,50, 220, 30);

        blockID.setBounds(30,90,100,30);
        unblockID.setBounds(150,90,100,30);

        reasonLabel.setBounds(30,130,220,30);
        areYouSureLabel.setBounds(30,130,220,30);

        reasonInput.setBounds(30,160, 220, 30);

        submitButton.addActionListener(e -> {
            boolean failed = false;
            reasonLabel.setForeground(Defaults.FOREGROUND_A);
            enterIDLabel.setForeground(Defaults.FOREGROUND_A);
            if(reasonInput.getText().equalsIgnoreCase("") && !unblock){
                reasonLabel.setForeground(Color.RED);
                failed = true;
            }
            if(idInput.getText().equalsIgnoreCase("")){
                enterIDLabel.setForeground(Color.RED);
                failed = true;
            }



            if(failed) return;
            if(unblock){
                new Thread(() -> {
                    String levelName = "Unknown";
                    try{
                        GDLevel level = GDAPI.getLevel(Long.parseLong(idInput.getText()));
                        levelName = level.name();
                    }
                    catch (Exception ignored){}
                    String option = DialogBox.showDialogBox("Globally Unblock " + idInput.getText() + "?", "\"" + levelName + "\" will now be able to be requested anywhere using loquibot.", "" , new String[]{"$YES$", "$NO$"});
                    if(option.equalsIgnoreCase("YES")){
                        System.out.println("unblocked");
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("request_type", "globally_unblock_id");
                        jsonObject.put("id", idInput.getText());
                        Main.sendToServer(jsonObject.toString());
                        DialogBox.closeDialogBox();
                    }
                }).start();
            }
            else {

                    new Thread(() -> {
                        String levelName = "Unknown";
                        try {
                            GDLevel level = GDAPI.getLevel(Long.parseLong(idInput.getText()));
                            levelName = level.name();
                        } catch (Exception ignored) {
                        }
                        String option = DialogBox.showDialogBox("Globally Block " + idInput.getText() + "?", "\"" + levelName + "\" will no longer be able to be requested anywhere using loquibot. REASON: " + reasonInput.getText(), "", new String[]{"$YES$", "$NO$"});
                        if (option.equalsIgnoreCase("YES")) {
                            System.out.println("blocked");
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("request_type", "globally_block_id");
                            jsonObject.put("id", idInput.getText());
                            jsonObject.put("reason", reasonInput.getText());
                            Main.sendToServer(jsonObject.toString());
                            DialogBox.closeDialogBox();
                        }
                    }).start();

            }
        });

        reasonLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        areYouSureLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        enterIDLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));

        reasonLabel.setForeground(Defaults.FOREGROUND_A);
        areYouSureLabel.setForeground(Defaults.FOREGROUND_A);
        enterIDLabel.setForeground(Defaults.FOREGROUND_A);


        reasonLabel.setVisible(false);
        areYouSureLabel.setVisible(false);
        reasonInput.setVisible(false);
        submitButton.setVisible(false);
        panel.add(enterIDLabel);
        panel.add(reasonLabel);
        panel.add(areYouSureLabel);
        panel.add(blockID);
        panel.add(unblockID);
        panel.add(idInput);
        panel.add(reasonInput);
        panel.add(submitButton);
    }


    public static void refreshUI(){
        panel.setBackground(Defaults.COLOR3);
        reasonLabel.setForeground(Defaults.FOREGROUND_A);
        enterIDLabel.setForeground(Defaults.FOREGROUND_A);
        submitButton.setBackground(Defaults.COLOR2);
        submitButton.setForeground(Defaults.FOREGROUND_A);
        blockID.setBackground(Defaults.COLOR2);
        blockID.setForeground(Defaults.FOREGROUND_A);
        unblockID.setBackground(Defaults.COLOR2);
        unblockID.setForeground(Defaults.FOREGROUND_A);
    }

    public static void showWindow(){
        unblock = false;
        idInput.setText("");
        reasonInput.setText("");
        reasonLabel.setForeground(Defaults.FOREGROUND_A);
        enterIDLabel.setForeground(Defaults.FOREGROUND_A);
        reasonLabel.setVisible(false);
        areYouSureLabel.setVisible(false);
        reasonInput.setVisible(false);
        submitButton.setVisible(false);
        if(RequestsTab.getQueueSize() != 0) {
            idInput.setText(String.valueOf(RequestsTab.getRequest(LevelButton.selectedID).getLevelData().getGDLevel().id()));
        }
        DialogBox.showDialogBox(panel);
    }
    private static CurvedButton createCurvedButton(String text) {
        CurvedButton button = new CurvedButton(text);
        button.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setBackground(Defaults.COLOR2);
        button.setUI(settingsButtonUI);
        button.setForeground(Defaults.FOREGROUND_A);
        button.setOpaque(false);
        button.setPreferredSize(new Dimension(button.getPreferredSize().width + 30, 50));
        return button;
    }
}
