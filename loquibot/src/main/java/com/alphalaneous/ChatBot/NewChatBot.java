package com.alphalaneous.ChatBot;

import com.alphalaneous.Services.Twitch.TwitchAPI;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.ChatConnectionStateEvent;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.client.websocket.domain.WebsocketConnectionState;

import java.util.ArrayList;
import java.util.Map;

public abstract class NewChatBot {

    private final String channel;


    public NewChatBot(String channel){
        this.channel = channel;
    }


    public void connect(String oauth){
        OAuth2Credential credential = new OAuth2Credential("twitch", oauth);

        TwitchAPI.twitchClient = TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .withEnableChat(true)
                .withDefaultAuthToken(credential)
                .withClientId(TwitchAPI.getClientID())
                .withChatAccount(credential)
                .build();


        TwitchAPI.twitchClient.getChat().joinChannel(channel);

        SimpleEventHandler eventHandler = TwitchAPI.twitchClient.getChat().getEventManager().getEventHandler(SimpleEventHandler.class);
        eventHandler.onEvent(IRCMessageEvent.class, this::parseChatEvent);
        eventHandler.onEvent(ChatConnectionStateEvent.class, this::parseConnectionEvent);


    }

    public void parseConnectionEvent(ChatConnectionStateEvent event){

        WebsocketConnectionState state = event.getState();

        if(state == WebsocketConnectionState.CONNECTED){
            onOpen();
        }
        if(state == WebsocketConnectionState.DISCONNECTED || state == WebsocketConnectionState.LOST){
            onClose();
        }
    }

    public void parseChatEvent(IRCMessageEvent event){

        if(event.getUser() == null) return;
        if(event.getUser().getName().equalsIgnoreCase("loquibot")) return;

        Map<String, String> map = event.getTags();

        ArrayList<String> tags = new ArrayList<>();

        for (String key : map.keySet()) {
            String tag = key + "=" + map.get(key);
            tags.add(tag);
        }

        Map<String, String> badgeMap = event.getBadges();

        ArrayList<String> badges = new ArrayList<>();

        for (String key : badgeMap.keySet()) {
            String badge = key + "/" + map.get(key);
            badges.add(badge);
        }

        int cheerCount = 0;
        String displayName = event.getUserName();
        boolean isFirstMessage = false;
        boolean isCustomReward = false;
        boolean isMod = false;
        boolean isSub = false;
        boolean isVIP = false;


        for (String tagA : tags) {
            if (tagA.split("=", 2)[0].equals("bits")) {
                cheerCount = Integer.parseInt(tagA.split("=", 2)[1]);
            }
            if (tagA.split("=", 2)[0].equals("display-name")) {
                displayName = tagA.split("=", 2)[1];
            }
            if (tagA.split("=", 2)[0].equals("first-msg")) {
                isFirstMessage = !tagA.split("=", 2)[1].equals("0");
            }
            if (tagA.split("=", 2)[0].equals("custom-reward-id")) {
                isCustomReward = true;
            }
        }

        for(String tag : tags){

            if(tag.split("=")[0].equalsIgnoreCase("mod") && Integer.parseInt(tag.split("=")[1]) > 0){
                isMod = true;
            }
            if(tag.split("=")[0].equalsIgnoreCase("subscriber") && Integer.parseInt(tag.split("=")[1]) > 0){
                isSub = true;
            }
            if(tag.split("=")[0].equalsIgnoreCase("vip") && Integer.parseInt(tag.split("=")[1]) > 0){
                isVIP = true;
            }
        }

        String strMessage = "";

        if(event.getMessage().isPresent()){
            strMessage = event.getMessage().get();
        }

        ChatMessage message = new ChatMessage(
                tags.toArray(new String[]{}),
                event.getUserName(),
                displayName,
                strMessage,
                badges.toArray(new String[]{}),
                isMod,
                isSub,
                isVIP,
                cheerCount,
                isFirstMessage,
                isCustomReward);

        message.addIRCEvent(event);
        if(event.getMessage().isPresent() && event.getUserName() != null && event.getCommandType().equalsIgnoreCase("PRIVMSG")){
            onMessage(message);
        }
        onRawMessage(event.getRawMessage());
    }

    public void sendMessage(String message){
        if(TwitchAPI.twitchClient != null){
            TwitchAPI.twitchClient.getChat().sendMessage(channel, message);
        }
    }
    public void sendRawMessage(String message){
        TwitchAPI.twitchClient.getChat().sendRaw(message);
    }
    public void disconnect(){
        if(TwitchAPI.twitchClient != null) {
            TwitchAPI.twitchClient.getChat().leaveChannel(channel);
            TwitchAPI.twitchClient.getChat().disconnect();
        }
    }
    public boolean isClosed(){
        return !TwitchAPI.twitchClient.getChat().isChannelJoined(channel);
    }

    public abstract void onOpen();
    public abstract void onClose();
    public abstract void onMessage(ChatMessage message);
    public abstract void onRawMessage(String message);

}
