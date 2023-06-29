package com.alphalaneous.Interactive.Keywords;

import com.alphalaneous.Interactive.Commands.CommandHandler;
import com.alphalaneous.Main;
import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Utils.Utilities;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeywordHandler {

    public static void run(ChatMessage message){

        String reply = "";
        String foundWord = "";

        KeywordData foundKeyword = null;

        for(KeywordData data : KeywordData.getRegisteredKeywords()){

            Pattern p = Pattern.compile(data.getKeyword());
            Matcher m = p.matcher(message.getMessage());

            boolean found = false;
            if (m.find()) {
                foundWord = m.group(0);
                found = true;
            }

            if(found && data.isEnabled()) {
                foundKeyword = data;
                break;
            }
        }


        if (foundKeyword != null
                && foundKeyword.isEnabled()
                && !isCooldown(foundKeyword)
                && checkUserLevel(foundKeyword, message)) {
            foundKeyword.setFoundWord(foundWord);
            String response = foundKeyword.getMessage();
            String[] messageSplit = message.getMessage().split(" ");
            if (messageSplit.length > 1) {
                reply = CommandHandler.replaceBetweenParentheses(message, response, message.getMessage().split(" ", 2)[1].split(" "), null, foundKeyword);
            } else {
                reply = CommandHandler.replaceBetweenParentheses(message, response, new String[0], null, foundKeyword);
            }
            startCooldown(foundKeyword);
        }
        if (!reply.equalsIgnoreCase("")) {
            if(message.isYouTube()) Main.sendYTMessage(reply, null);
            else if(message.isKick()) Main.sendKickMessage(reply, null);
            else {
                Main.sendMessage(reply, message.getTag("id"));
            }
        }

    }
    private static final ArrayList<KeywordData> keywordDataList = new ArrayList<>();

    public static void startCooldown(KeywordData commandData){
        new Thread(() -> {
            keywordDataList.add(commandData);
            Utilities.sleep(commandData.getCooldown()*1000);
            keywordDataList.remove(commandData);
        }).start();

    }
    public static boolean isCooldown(KeywordData data){
        for(KeywordData commandData : keywordDataList){
            return commandData.getKeyword().equalsIgnoreCase(data.getKeyword());
        }
        return false;
    }
    public static boolean checkUserLevel(KeywordData data, ChatMessage message){
        String commandLevel = data.getUserLevel();
        String messageLevel = message.getUserLevel();

        ArrayList<String> userLevels = new ArrayList<>();
        userLevels.add("everyone");
        userLevels.add("subscriber");
        userLevels.add("twitch_vip");
        userLevels.add("moderator");
        userLevels.add("owner");

        ArrayList<String> userLevelsToRemove = new ArrayList<>();
        for(String userLevel : userLevels){
            if(commandLevel.equalsIgnoreCase(userLevel)){

                break;
            }
            userLevelsToRemove.add(userLevel);
        }
        userLevels.removeAll(userLevelsToRemove);
        return userLevels.contains(messageLevel);
    }
}
