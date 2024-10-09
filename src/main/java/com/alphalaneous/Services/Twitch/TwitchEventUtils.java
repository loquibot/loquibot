package com.alphalaneous.Services.Twitch;

import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Servers;
import com.alphalaneous.Utilities.SettingsHandler;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.common.util.EventManagerUtils;

public abstract class TwitchEventUtils {

    private String channel;

    public TwitchEventUtils(String channel){
        this.channel = channel;
    }


    public void connect(String oauth){

        OAuth2Credential credential = new OAuth2Credential("twitch", oauth);

        EventManager em = EventManagerUtils.initializeEventManager(SimpleEventHandler.class);
        em.getEventHandler(SimpleEventHandler.class).registerListener(new TwitchEvents(this));

        TwitchAPI.twitchClient = TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .withEnableChat(true)
                .withEnablePubSub(true)
                .withEventManager(em)
                .withDefaultAuthToken(credential)
                .withClientId(TwitchAPI.getClientID())
                .withChatAccount(credential)
                .build();


        TwitchAPI.twitchClient.getPubSub().listenForChannelPointsRedemptionEvents(credential, TwitchAccount.id);
        TwitchAPI.twitchClient.getPubSub().listenForRaidEvents(credential, TwitchAccount.id);
        TwitchAPI.twitchClient.getPubSub().listenForCheerEvents(credential, TwitchAccount.id);
        TwitchAPI.twitchClient.getPubSub().listenForSubscriptionEvents(credential, TwitchAccount.id);
        TwitchAPI.twitchClient.getPubSub().listenForFollowingEvents(credential, TwitchAccount.id);
    }

    public void sendMessage(String message){
        sendMessage(message, null);
    }
    public void sendMessage(String message, String messageID){
        if(SettingsHandler.getSettings("usingSelfBotAccount").asBoolean()) {
            if (!message.trim().isEmpty() && TwitchAPI.twitchClient != null) {
                TwitchAPI.twitchClient.getChat().sendMessage(channel, message);
            }
        }
        else{
            Servers.sendTwitchMessage(message, messageID);
        }
    }

    public void reconnect(String newChannel, String oauth){
        TwitchAPI.twitchClient.getPubSub().disconnect();
        TwitchAPI.twitchClient.getChat().disconnect();

        this.channel = newChannel;
        connect(oauth);
    }

    public boolean isClosed(){
        return !TwitchAPI.twitchClient.getChat().isChannelJoined(channel);
    }

    public abstract void onOpen();
    public abstract void onClose();
    public abstract void onMessage(ChatMessage message);
    public abstract void onRawMessage(String message);

}
