package com.alphalaneous.ChatBot;

import com.alphalaneous.Interactive.Commands.CommandHandler;
import com.alphalaneous.Interactive.Keywords.KeywordHandler;
import com.alphalaneous.KickAPI.Casterlabs.ApiException;
import com.alphalaneous.KickAPI.Casterlabs.KickChannel;
import com.alphalaneous.KickAPI.Casterlabs.KickUser;
import com.alphalaneous.KickAPI.Channel;
import com.alphalaneous.KickAPI.KickClient;
import com.alphalaneous.KickAPI.MessageEvent;
import com.alphalaneous.Services.Kick.KickAccount;
import com.alphalaneous.Settings.SettingsHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KickBot {

    private static KickBot currentState = null;

    private String channel;
    private boolean connectionSucceeded = false;
    KickClient kickClient;

    public KickBot(String channel){
        this.channel = channel;
        currentState = this;
    }

    public void connect(){
        kickClient = new KickClient();
        kickClient.connectListener();

        try {
            KickChannel channelInfo = kickClient.getEndpoints().getChannelInfo(channel);
            KickUser user = channelInfo.getUser();

            KickAccount.setUsername(user.getUsername());
            KickAccount.setProfilePicURL(user.getProfilePicURL());
            KickAccount.setChatroomID((int) channelInfo.getChatRoomId());

            Channel channel = kickClient.getChat().addChannel(KickAccount.chatroomID);

            connectionSucceeded = true;

            channel.addEventListener(e -> {
                String username = ((MessageEvent)e).getSender();
                String slug = ((MessageEvent)e).getSlug();
                String message = ((MessageEvent)e).getMessage();

                String[] messageParts = message.split(" ");

                StringBuilder newMessage = new StringBuilder();


                for(String str : messageParts){

                    Pattern pattern = Pattern.compile("\\[emote:[0-9]+:[A-Za-z0-9]+]", Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(str);

                    if(matcher.find()){
                        continue;
                    }

                    newMessage.append(str).append(" ");
                }

                ChatMessage chatMessage = new ChatMessage(null, slug, username, newMessage.toString(), null, ((MessageEvent) e).isMod(), ((MessageEvent) e).isSub(), ((MessageEvent) e).isVIP(), 0, false, false);
                chatMessage.setKick(true);
                if (!slug.equalsIgnoreCase("loquibot")) {

                    if (SettingsHandler.getSettings("multiMode").asBoolean()) {
                        new Thread(() -> waitOnMessage(chatMessage)).start();
                    } else {
                        waitOnMessage(chatMessage);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void waitOnMessage(ChatMessage chatMessage){
        BotHandler.onMessage(chatMessage);
        CommandHandler.run(chatMessage);
        KeywordHandler.run(chatMessage);
    }

    public void disconnect(){
        if(kickClient != null) kickClient.disconnectListener();
    }

    public String getChannel(){
        return channel;
    }

    public boolean didConnectionSucceed(){
        return connectionSucceeded;
    }

    public static KickBot getCurrentState(){
        return currentState;
    }
}
