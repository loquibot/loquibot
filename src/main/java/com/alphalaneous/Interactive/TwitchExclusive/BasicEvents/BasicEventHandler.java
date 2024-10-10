package com.alphalaneous.Interactive.TwitchExclusive.BasicEvents;

import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Services.Twitch.TwitchChatListener;
import com.alphalaneous.Interactive.Commands.CommandHandler;
import com.github.twitch4j.chat.events.channel.CheerEvent;
import com.github.twitch4j.chat.events.channel.FollowEvent;
import com.github.twitch4j.chat.events.channel.RaidEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.github.twitch4j.pubsub.domain.ChannelPointsRedemption;
import com.github.twitch4j.pubsub.domain.ChannelPointsReward;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;

import java.util.HashMap;

public class BasicEventHandler {

    public static void run(FollowEvent event){

        if(!LoadBasicEvents.followData.isEnabled()) return;

        new Thread(() -> {

            BasicEventData data = LoadBasicEvents.followData;

            String response = data.getMessage();

            ChatMessage message = new ChatMessage(new String[0], event.getUser().getName(), event.getUser().getName(), "", new String[0], true, true, true, false, false, false);

            HashMap<String, String> extraData = new HashMap<>();

            extraData.put("followTime", event.getFiredAtInstant().toString());

            String reply = CommandHandler.replaceBetweenParentheses(message, response, data, extraData);

            if (!reply.equalsIgnoreCase("")) {
                TwitchChatListener.getCurrentListener().sendMessage(reply);
            }

        }).start();
    }

    public static void run(RaidEvent event){

        if(!LoadBasicEvents.raidData.isEnabled()) return;

        new Thread(() -> {

            BasicEventData data = LoadBasicEvents.raidData;

            String response = data.getMessage();

            ChatMessage message = new ChatMessage(new String[0], event.getRaider().getName(), event.getRaider().getName(), "", new String[0], true, true, true, false, false, false);

            HashMap<String, String> extraData = new HashMap<>();

            extraData.put("raidTime", event.getFiredAtInstant().toString());
            extraData.put("raidViewers", String.valueOf(event.getViewers()));

            String reply = CommandHandler.replaceBetweenParentheses(message, response, data, extraData);

            if (!reply.equalsIgnoreCase("")) {
                TwitchChatListener.getCurrentListener().sendMessage(reply);
            }

        }).start();
    }

    public static void run(SubscriptionEvent event){

        if(!LoadBasicEvents.subscribeData.isEnabled()) return;

        new Thread(() -> {

            BasicEventData data = LoadBasicEvents.subscribeData;

            String messageText = event.getMessage().orElse("");

            String response = data.getMessage();

            ChatMessage message = new ChatMessage(new String[0], event.getUser().getName(), event.getUser().getName(), messageText, new String[0], true, true, true, false, false, false);

            HashMap<String, String> extraData = new HashMap<>();

            extraData.put("subscriptionTime", event.getFiredAtInstant().toString());
            extraData.put("subscriptionGifted", event.getGifted().toString());
            extraData.put("subscriptionGiftedBy", event.getGiftedBy().getName());
            extraData.put("subscriptionGiftedMonths", String.valueOf(event.getGiftMonths()));
            extraData.put("subscriptionPlan", event.getSubscriptionPlan());
            extraData.put("subscriptionStreak", String.valueOf(event.getSubStreak()));
            extraData.put("subscriptionMultiMonthDuration", String.valueOf(event.getMultiMonthDuration()));
            extraData.put("subscriptionMultiMonthTenure", String.valueOf(event.getMultiMonthTenure()));
            extraData.put("subscriptionMonths", String.valueOf(event.getMonths()));
            extraData.put("subscriptionLength", event.getFiredAtInstant().toString());

            String reply = CommandHandler.replaceBetweenParentheses(message, response, data, extraData);

            if (!reply.equalsIgnoreCase("")) {
                TwitchChatListener.getCurrentListener().sendMessage(reply);
            }
        }).start();
    }
    public static void run(RewardRedeemedEvent event){

        if(!LoadBasicEvents.rewardData.isEnabled()) return;

        new Thread(() -> {

            BasicEventData data = LoadBasicEvents.rewardData;

            ChannelPointsRedemption redemption = event.getRedemption();
            ChannelPointsReward reward = redemption.getReward();

            String response = data.getMessage();

            ChatMessage message = new ChatMessage(new String[0], event.getRedemption().getUser().getLogin(), event.getRedemption().getUser().getDisplayName(), "", new String[0], true, true, true, false, false, false);

            HashMap<String, String> extraData = new HashMap<>();

            extraData.put("rewardTime", redemption.getRedeemedAt());
            extraData.put("rewardCost", String.valueOf(reward.getCost()));
            extraData.put("rewardTitle", reward.getTitle());
            extraData.put("rewardId", reward.getId());

            String reply = CommandHandler.replaceBetweenParentheses(message, response, data, extraData);

            if (!reply.equalsIgnoreCase("")) {
                TwitchChatListener.getCurrentListener().sendMessage(reply);
            }

        }).start();
    }

    public static void run(CheerEvent event){

        if(!LoadBasicEvents.cheerData.isEnabled()) return;

        new Thread(() -> {

            BasicEventData data = LoadBasicEvents.cheerData;

            String response = data.getMessage();

            ChatMessage message = new ChatMessage(new String[0], event.getUser().getName(), event.getUser().getName(), "", new String[0], true, true, true, false, false, false);

            HashMap<String, String> extraData = new HashMap<>();

            extraData.put("cheerAmount", String.valueOf(event.getBits()));

            String reply = CommandHandler.replaceBetweenParentheses(message, response, data, extraData);

            if (!reply.equalsIgnoreCase("")) {
                TwitchChatListener.getCurrentListener().sendMessage(reply);
            }

        }).start();
    }



}
