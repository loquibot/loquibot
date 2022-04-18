package com.alphalaneous;

import com.alphalaneous.TwitchBot.ChatMessage;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeywordHandler {

    public static void run(ChatMessage message){

        String reply = "";
        if(message.getSender().equalsIgnoreCase("alphalaneous")) message.setMod(true);

        KeywordData foundKeyword = null;

        for(KeywordData data : KeywordData.getRegisteredKeywords()){

            Pattern p = Pattern.compile(data.getKeyword());
            Matcher m = p.matcher(message.getMessage());

            boolean found = false;

            while (m.find()) found = true;

            if(found) {
                foundKeyword = data;
                break;
            }
        }


        if (foundKeyword != null
                && foundKeyword.isEnabled()
                && !isCooldown(foundKeyword)
                && checkUserLevel(foundKeyword, message)) {
            String response = foundKeyword.getMessage();
            String[] messageSplit = message.getMessage().split(" ");
            if (messageSplit.length > 1) {
                reply = CommandNew.replaceBetweenParentheses(message, response, message.getMessage().split(" ", 2)[1].split(" "), null, foundKeyword);
            } else {
                reply = CommandNew.replaceBetweenParentheses(message, response, new String[0], null, foundKeyword);
            }
            startCooldown(foundKeyword);
        }
        if (!reply.equalsIgnoreCase("")) {
            Main.sendMessage(reply);
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
