package com.alphalaneous.Services.Twitch;

import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Interactive.TwitchExclusive.BasicEvents.BasicEventHandler;
import com.alphalaneous.Interactive.TwitchExclusive.ChannelPoints.ChannelPointHandler;
import com.alphalaneous.Interactive.TwitchExclusive.Cheers.CheerHandler;
import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.chat.events.ChatConnectionStateEvent;
import com.github.twitch4j.chat.events.channel.*;
import com.github.twitch4j.client.websocket.domain.WebsocketConnectionState;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;

import java.util.ArrayList;
import java.util.Map;

public class TwitchEvents {

    private final TwitchEventUtils twitchEventUtils;

    public TwitchEvents(TwitchEventUtils twitchEventUtils){
        this.twitchEventUtils = twitchEventUtils;
    }

    @EventSubscriber
    public void parseChannelPointRedeemEvent(RewardRedeemedEvent event){

        ChannelPointHandler.run(event);
        BasicEventHandler.run(event);
    }

    @EventSubscriber
    public void parseRaidEvent(RaidEvent event){
        BasicEventHandler.run(event);
    }

    @EventSubscriber
    public void parseCheerEvent(CheerEvent event){
        CheerHandler.run(event);
        BasicEventHandler.run(event);
    }

    @EventSubscriber
    public void parseSubscriptionEvent(SubscriptionEvent event){
        BasicEventHandler.run(event);
    }

    @EventSubscriber
    public void parseFollowEvent(FollowEvent event){
        BasicEventHandler.run(event);
    }

    @EventSubscriber
    public void parseConnectionEvent(ChatConnectionStateEvent event){

        WebsocketConnectionState state = event.getState();

        if(state == WebsocketConnectionState.CONNECTED){
            twitchEventUtils.onOpen();
        }
        if(state == WebsocketConnectionState.DISCONNECTED || state == WebsocketConnectionState.LOST){
            twitchEventUtils.onClose();
        }
    }

    @EventSubscriber
    public void parseChatEvent(ChannelMessageEvent eventA){
        if(eventA.getUser() == null) return;

        IRCMessageEvent event = eventA.getMessageEvent();

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
                isFirstMessage,
                isCustomReward,
                false);

        if(event.getMessage().isPresent() && event.getUserName() != null && event.getCommandType().equalsIgnoreCase("PRIVMSG")){
            twitchEventUtils.onMessage(message);
        }
        twitchEventUtils.onRawMessage(event.getRawMessage());
    }
}
