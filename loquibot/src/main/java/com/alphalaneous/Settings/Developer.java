package com.alphalaneous.Settings;

import com.alphalaneous.Main;
import com.alphalaneous.Services.Twitch.TwitchAPI;
import com.alphalaneous.Swing.Components.SettingsPage;
import com.alphalaneous.ChatBot.ServerBot;
import org.json.JSONObject;

import javax.swing.*;

public class Developer {

    public static JPanel createPanel() {
        SettingsPage settingsPage = new SettingsPage("$DEVELOPER_SETTINGS$");

        settingsPage.addRadioOption("Server", "", new String[]{"main", "local"}, "dev_Server", "main", () -> {
            ServerBot.disconnect();
            ServerBot.connect();
        });

        settingsPage.addButton("Reconnect", ()->{
            ServerBot.disconnect();
            ServerBot.connect();
        });


        settingsPage.addButton("Reboot Servers", () -> {
            JSONObject response = new JSONObject();
            response.put("request_type", "restart");
            ServerBot.sendMessage(String.valueOf(response));
        });

        settingsPage.addButton("Update oauth", () -> {
            new Thread(() -> {

                JSONObject response = new JSONObject();
                response.put("request_type", "reset_oauth");

                String oauth = TwitchAPI.getBotOauth();
                Main.logger.info("New oauth is: " + oauth);
                response.put("oauth", oauth);

                if (oauth != null){
                    ServerBot.sendMessage(String.valueOf(response));
                }
            }).start();

        });



        return settingsPage;
    }

}
