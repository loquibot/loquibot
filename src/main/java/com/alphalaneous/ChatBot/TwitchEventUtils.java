package com.alphalaneous.ChatBot;

import com.alphalaneous.Services.Twitch.TwitchAPI;
import com.alphalaneous.Services.Twitch.TwitchAccount;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.ChatConnectionStateEvent;
import com.github.twitch4j.chat.events.channel.*;
import com.github.twitch4j.client.websocket.domain.WebsocketConnectionState;
import com.github.twitch4j.common.util.EventManagerUtils;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;

import java.util.ArrayList;
import java.util.Map;

public abstract class TwitchEventUtils {

    private final String channel;

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
                .withEnableEventSocket(true)
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
        if(!message.trim().isEmpty()) {
            if (TwitchAPI.twitchClient != null) {
                TwitchAPI.twitchClient.getChat().sendMessage(channel, message);
            }
        }
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
