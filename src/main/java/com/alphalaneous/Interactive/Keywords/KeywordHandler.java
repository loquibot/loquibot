package com.alphalaneous.Interactive.Keywords;

import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Services.Twitch.TwitchChatListener;
import com.alphalaneous.Interactive.Commands.CommandHandler;
import com.alphalaneous.Utilities.Utilities;
import com.alphalaneous.Enums.UserLevel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeywordHandler {

    public static void run(ChatMessage message){
        new Thread(() -> {
            String reply = "";
            String foundWord = "";

            KeywordData foundKeyword = null;

            for(KeywordData data : KeywordData.getRegisteredKeywords()){

                Pattern p = Pattern.compile(data.getName());
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
                String response = foundKeyword.getMessage();

                HashMap<String, String> extraData = new HashMap<>();

                extraData.put("foundWord", foundWord);

                reply = CommandHandler.replaceBetweenParentheses(message, response, foundKeyword, extraData);

                startCooldown(foundKeyword);
            }
            if (!reply.equalsIgnoreCase("")) {
               TwitchChatListener.getCurrentListener().sendMessage(reply, message.getTag("id"));
            }
        }).start();
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
            return commandData.getName().equalsIgnoreCase(data.getName());
        }
        return false;
    }
    public static boolean checkUserLevel(KeywordData data, ChatMessage message){
        UserLevel commandLevel = data.getUserLevel();
        UserLevel messageLevel = message.getUserLevel();

        return UserLevel.checkLevel(commandLevel, messageLevel);
    }
}
