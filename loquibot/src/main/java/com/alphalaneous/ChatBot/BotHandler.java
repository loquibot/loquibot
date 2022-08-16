package com.alphalaneous.ChatBot;

import com.alphalaneous.Services.GeometryDash.Requests;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Windows.Window;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BotHandler {

    public static void onMessage(ChatMessage chatMessage) {
        String messageNoComma = chatMessage.getMessage().replace(",", "").replace(".","");

        Matcher m = Pattern.compile("\\s*(\\d{6,})\\s*").matcher(messageNoComma);

        String defaultCommandPrefix = "!";
        String geometryDashCommandPrefix = "!";
        String mediaShareCommandPrefix = "!";


        if(SettingsHandler.getSettings("defaultCommandPrefix").exists()) defaultCommandPrefix = SettingsHandler.getSettings("defaultCommandPrefix").asString();
        if(SettingsHandler.getSettings("geometryDashCommandPrefix").exists()) geometryDashCommandPrefix = SettingsHandler.getSettings("geometryDashCommandPrefix").asString();
        if(SettingsHandler.getSettings("mediaShareCommandPrefix").exists()) mediaShareCommandPrefix = SettingsHandler.getSettings("mediaShareCommandPrefix").asString();

        if(chatMessage.getMessage().toLowerCase().startsWith(defaultCommandPrefix) ||
                chatMessage.getMessage().toLowerCase().startsWith(geometryDashCommandPrefix) ||
                chatMessage.getMessage().toLowerCase().startsWith(mediaShareCommandPrefix)) return;

        if(!SettingsHandler.getSettings("requestsChannelPointsOnly").asBoolean()) {
            if (m.find()) {
                try {
                    String[] messages = chatMessage.getMessage().split(" ");
                    String mention = "";
                    for (String s : messages) {
                        if (s.contains("@")) {
                            mention = s;
                            break;
                        }
                    }
                    if (!mention.contains(m.group(1))) {
                        if (SettingsHandler.getSettings("gdMode").asBoolean() && Window.getWindow().isVisible()) {
                            long chatIDL = 0;
                            String chatID = chatMessage.getTag("user-id");
                            if (chatID != null) {
                                chatIDL = Long.parseLong(chatID);
                            }
                            Requests.addRequest(Long.parseLong(m.group(1).replaceFirst("^0+(?!$)", "")), chatMessage.getSender(), chatMessage.isMod(), chatMessage.isSub(), chatMessage.getMessage(), chatMessage.getTag("id"), chatIDL, false, chatMessage);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
