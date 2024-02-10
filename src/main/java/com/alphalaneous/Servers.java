package com.alphalaneous;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Services.Twitch.TwitchAPI;
import com.alphalaneous.Services.YouTube.YouTubeAccount;
import com.alphalaneous.Utilities.SettingsHandler;
import com.alphalaneous.Utilities.Utilities;
import org.json.JSONObject;

import java.net.URI;

public class Servers {

    private static ServerConnection connection;
    public static int waitTime = 1;

    public static boolean isFirst = true;
    @OnLoad(order = 0)
    public static void loadServers(){

        if(!isFirst) {
            Utilities.sleep(waitTime * 1000);
        } else {
            isFirst = false;
        }

        if(connection != null && connection.isOpen()) return;

        if (SettingsHandler.getSettings("useDebugServers").asBoolean()) {
            connection = new ServerConnection(URI.create("ws://localhost:2963"));
        } else {
            connection = new ServerConnection(URI.create("ws://164.152.25.111:2963"));
        }

    }

    public static void send(String text){
        if(connection != null && connection.isOpen()) {
            connection.send(text);
        }
    }

    public static void connectTwitch(){
        connection.connectTwitch();
    }

    public static void sendTwitchMessage(String message, String messageID){

        if(message.trim().isEmpty()) return;

        if(SettingsHandler.getSettings("disableChat").asBoolean()) return;

        if(SettingsHandler.getSettings("isTwitchLoggedIn").asBoolean()) {

            JSONObject messageObj = new JSONObject();
            messageObj.put("request_type", "send_message");
            messageObj.put("message", message);
            if(messageID != null && !messageID.trim().equalsIgnoreCase("")){
                messageObj.put("reply-id", messageID);
            }
            send(messageObj.toString());
        }
    }

    public static void sendYouTubeMessage(String message, String username){

        if(SettingsHandler.getSettings("disableChat").asBoolean()) return;

        if(SettingsHandler.getSettings("isYouTubeLoggedIn").asBoolean()) {

            JSONObject messageObj = new JSONObject();
            messageObj.put("request_type", "send_yt_message");
            messageObj.put("liveChatId", YouTubeAccount.liveChatId);

            String pingStart = "";

            if(username != null && !username.equalsIgnoreCase("")){
                pingStart = "@" + username;
            }
            messageObj.put("username", YouTubeAccount.name);
            messageObj.put("message", pingStart + message);

            if(YouTubeAccount.liveChatId != null) {
                send(messageObj.toString());
            }

            send(message);
        }
    }

}
