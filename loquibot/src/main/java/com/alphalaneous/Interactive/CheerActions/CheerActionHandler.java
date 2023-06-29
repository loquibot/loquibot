package com.alphalaneous.Interactive.CheerActions;

import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Interactive.Commands.CommandHandler;
import com.alphalaneous.Interactive.Keywords.KeywordData;
import com.alphalaneous.Main;
import com.alphalaneous.Utils.Utilities;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheerActionHandler {

    public static void run(ChatMessage message){

        String reply = "";
        String foundWord = "";

        CheerActionData foundCheerAction = null;

        for(CheerActionData data : CheerActionData.getRegisteredCheerActions()){
            if(data.getCheerAmount() == message.getCheerCount()) {
                foundCheerAction = data;
                break;
            }
        }
        if(foundCheerAction == null){
            for(CheerActionData data : CheerActionData.getRegisteredCheerActions()){
                if (data.getCheerAmount() == -1 && message.getCheerCount() > 0){
                    foundCheerAction = data;
                    break;
                }
            }
        }


        if (foundCheerAction != null
                && foundCheerAction.isEnabled()
                && checkUserLevel(foundCheerAction, message)) {
            String response = foundCheerAction.getMessage();
            String[] messageSplit = message.getMessage().split(" ");
            if (messageSplit.length > 1) {
                reply = CommandHandler.replaceBetweenParentheses(message, response, message.getMessage().split(" ", 2)[1].split(" "), null, foundCheerAction);
            } else {
                reply = CommandHandler.replaceBetweenParentheses(message, response, new String[0], null, foundCheerAction);
            }
        }
        if (!reply.equalsIgnoreCase("")) {
            //if(message.isYouTube()) Main.sendYTMessage(reply, null);
            //else if(m)
            Main.sendMessage(reply);
        }

    }
    private static final ArrayList<KeywordData> keywordDataList = new ArrayList<>();
    public static boolean checkUserLevel(CheerActionData data, ChatMessage message){
        String commandLevel = data.getUserLevel();
        String messageLevel = message.getUserLevel();

        ArrayList<String> userLevels = new ArrayList<>();
        userLevels.add("everyone");
        userLevels.add("subscriber");
        userLevels.add("twitch_vip");
        userLevels.add("moderator");
        userLevels.add("owner");

        if(message.getUserLevel().equals("admin")) return true;

        for(String userLevel : userLevels){
            if(commandLevel.equalsIgnoreCase(userLevel)){
                break;
            }
            userLevels.remove(userLevel);
        }
        return userLevels.contains(messageLevel);
    }
}
