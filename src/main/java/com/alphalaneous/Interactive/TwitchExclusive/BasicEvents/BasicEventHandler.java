package com.alphalaneous.Interactive.TwitchExclusive.BasicEvents;

import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.ChatBot.TwitchChatListener;
import com.alphalaneous.Interactive.Commands.CommandHandler;
import com.github.twitch4j.chat.events.channel.FollowEvent;
import com.github.twitch4j.chat.events.channel.RaidEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;

import java.util.HashMap;

public class BasicEventHandler {

    public static void run(FollowEvent event){
        new Thread(() -> {

            BasicEventData data = LoadBasicEvents.followData;

            String response = data.getMessage();

            ChatMessage message = new ChatMessage(new String[0], event.getUser().getName(), event.getUser().getName(), "", new String[0], true, true, true, false, false);

            HashMap<String, String> extraData = new HashMap<>();

            extraData.put("followTime", event.getFiredAtInstant().toString());

            String reply = CommandHandler.replaceBetweenParentheses(message, response, data, extraData);

            if (!reply.equalsIgnoreCase("")) {
                TwitchChatListener.getCurrentListener().sendMessage(reply);
            }

        }).start();
    }

    public static void run(RaidEvent event){
        new Thread(() -> {

            BasicEventData data = LoadBasicEvents.raidData;

            String response = data.getMessage();

            ChatMessage message = new ChatMessage(new String[0], event.getRaider().getName(), event.getRaider().getName(), "", new String[0], true, true, true, false, false);

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
        new Thread(() -> {

            BasicEventData data = LoadBasicEvents.subscribeData;

            String messageText = event.getMessage().orElse("");

            String response = data.getMessage();

            ChatMessage message = new ChatMessage(new String[0], event.getUser().getName(), event.getUser().getName(), messageText, new String[0], true, true, true, false, false);

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

}
