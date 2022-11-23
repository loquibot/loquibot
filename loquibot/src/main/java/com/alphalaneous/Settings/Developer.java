package com.alphalaneous.Settings;

import com.alphalaneous.Services.Twitch.TwitchAPI;
import com.alphalaneous.Swing.Components.SettingsPage;
import com.alphalaneous.ChatBot.ServerBot;
import org.json.JSONObject;

import javax.swing.*;

public class Developer {

    public static JPanel createPanel() {
        SettingsPage settingsPage = new SettingsPage("$DEVELOPER_SETTINGS$");

        settingsPage.addRadioOption("Server", "", new String[]{"main", "local"}, "dev_Server", "main", () -> {
            ServerBot.getCurrentServerBot().disconnect();
            new Thread(() -> new ServerBot().connect()).start();
        });

        settingsPage.addButton("Reconnect", ()->{
            ServerBot.getCurrentServerBot().disconnect();
            new Thread(() -> new ServerBot().connect()).start();
        });


        settingsPage.addButton("Reboot Servers", () -> {
            JSONObject response = new JSONObject();
            response.put("request_type", "restart");
            ServerBot.getCurrentServerBot().sendMessage(String.valueOf(response));
        });

        settingsPage.addButton("Update oauth", () -> {
            new Thread(() -> {

                JSONObject response = new JSONObject();
                response.put("request_type", "reset_oauth");

                String oauth = TwitchAPI.getOauth();
                response.put("oauth", oauth);

                if (oauth != null){
                    ServerBot.getCurrentServerBot().sendMessage(String.valueOf(response));
                }
            }).start();

        });



        return settingsPage;
    }

}
