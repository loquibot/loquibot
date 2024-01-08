package com.alphalaneous.Interactive.TwitchExclusive.Cheers;

import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Services.Twitch.TwitchChatListener;
import com.alphalaneous.Interactive.Commands.CommandHandler;
import com.github.twitch4j.chat.events.channel.CheerEvent;

import java.util.HashMap;

public class CheerHandler {

    public static void run(CheerEvent event){

        for(CheerData data : CheerData.getRegisteredCheers()) {

            if (data.isEnabled()) {

                new Thread(() -> {

                    boolean isInRange = data.isInRange(event.getBits()) || data.isAnyAmount();

                    if (isInRange) {
                        String messageText = event.getMessage();

                        String response = data.getMessage();

                        ChatMessage message = new ChatMessage(new String[0], event.getUser().getName(), event.getUser().getName(), messageText, new String[0], true, true, true, false, false);

                        HashMap<String, String> extraData = new HashMap<>();

                        extraData.put("cheerAmount", String.valueOf(event.getBits()));

                        String reply = CommandHandler.replaceBetweenParentheses(message, response, data, extraData);

                        if (!reply.equalsIgnoreCase("")) {
                            TwitchChatListener.getCurrentListener().sendMessage(reply);
                        }
                    }

                }).start();
            }
        }
    }
}
