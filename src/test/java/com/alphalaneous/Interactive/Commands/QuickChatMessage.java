package com.alphalaneous.Interactive.Commands;

import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Enums.UserLevel;

public class QuickChatMessage extends ChatMessage {
    public QuickChatMessage(String message, UserLevel level, int cheerCount) {

        super(null, "username1", "UserName1", message, null, false, false, false, false, false, false);

        boolean isMod = false;
        boolean isSub = false;
        boolean isVIP = false;

        if(level.equals(UserLevel.MODERATOR) || level.equals(UserLevel.OWNER)){
            isMod = true;
            isSub = true;
            isVIP = true;
        }
        if(level.equals(UserLevel.VIP)){
            isVIP = true;
        }

        if(level.equals(UserLevel.SUBSCRIBER)){
            isSub = true;
        }

        setMod(isMod);
        setVIP(isVIP);
        setSub(isSub);
    }
}
