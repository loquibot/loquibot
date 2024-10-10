package com.alphalaneous.Interactive.TwitchExclusive.ChannelPoints;

import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Services.Twitch.TwitchChatListener;
import com.alphalaneous.Interactive.Commands.CommandHandler;
import com.github.twitch4j.pubsub.domain.ChannelPointsRedemption;
import com.github.twitch4j.pubsub.domain.ChannelPointsReward;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;

import java.util.HashMap;

public class ChannelPointHandler {

    public static void run(RewardRedeemedEvent event){
        new Thread(() -> {

            System.out.println(event.toString());

            ChannelPointData data = null;
            ChannelPointsRedemption redemption = event.getRedemption();
            ChannelPointsReward reward = redemption.getReward();

            for(ChannelPointData channelPointData : ChannelPointData.getRegisteredChannelPoints()){
                if(channelPointData.getId().equals(reward.getId())){
                    data = channelPointData;
                    break;
                }
            }

            if(data != null) {
                String messageText = "";

                if(reward.getIsUserInputRequired()){
                    messageText = redemption.getUserInput();
                }

                String response = data.getMessage();

                ChatMessage message = new ChatMessage(new String[0], redemption.getUser().getLogin(), redemption.getUser().getDisplayName(), messageText, new String[0], true, true, true, false, false, false);

                HashMap<String, String> extraData = new HashMap<>();

                extraData.put("rewardTime", redemption.getRedeemedAt());
                extraData.put("rewardCost", String.valueOf(reward.getCost()));
                extraData.put("rewardTitle", reward.getTitle());


                String reply = CommandHandler.replaceBetweenParentheses(message, response, data, extraData);

                if (!reply.equalsIgnoreCase("")) {
                    TwitchChatListener.getCurrentListener().sendMessage(reply);
                }
            }

        }).start();
    }
}
