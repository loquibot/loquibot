//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.alphalaneous.ChatBot;

import com.alphalaneous.Services.Twitch.TwitchAccount;
import com.alphalaneous.Enums.UserLevel;

public class ChatMessage {
    private final String[] tags;
    private final String sender;
    private final String displayName;
    private String message;
    private String[] args;
    private final String[] badges;
    private boolean isMod;
    private boolean isSub;
    private boolean isVIP;
    private final boolean isFirstMessage;
    private final boolean isCustomReward;

    private final int cheerCount;

    public ChatMessage(String[] tags, String sender, String displayName, String message, String[] badges, boolean isMod, boolean isSub, boolean isVIP, int cheerCount, boolean isFirstMessage, boolean isCustomReward) {
        this.tags = tags;
        this.sender = sender;
        this.displayName = displayName;
        this.message = message;
        this.badges = badges;
        this.isMod = isMod;
        this.isSub = isSub;
        this.isVIP = isVIP;
        this.cheerCount = cheerCount;
        this.isFirstMessage = isFirstMessage;
        this.isCustomReward = isCustomReward;
        this.args = message.split(" ");
    }
    public boolean isCustomReward() {
        return isCustomReward;
    }

    public String[] getArgs(){
        return args;
    }

    public String getTag(String tag) {
        if(this.tags != null) {
            for (String tagA : this.tags) {
                if (tagA.split("=", 2)[0].equals(tag)) {
                    return tagA.split("=", 2)[1];
                }
            }
        }
        return null;
    }

    public boolean isFirstMessage(){
        return isFirstMessage;
    }

    public UserLevel getUserLevel(){

        if (TwitchAccount.login != null && TwitchAccount.login.toLowerCase().equalsIgnoreCase(sender)) {
            return UserLevel.OWNER;
        }

        if(isMod) return UserLevel.MODERATOR;
        if(isVIP) return UserLevel.VIP;
        if(isSub) return UserLevel.SUBSCRIBER;
        return UserLevel.EVERYONE;
    }

    public boolean isMod() {
        return this.isMod;
    }

    public void setMod(boolean isMod){
        this.isMod = isMod;
    }
    public void setVIP(boolean isVIP){
        this.isVIP = isVIP;
    }
    public void setSub(boolean isSub){
        this.isSub = isSub;
    }
    public void setArgs(String[] args){
        this.args = args;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public boolean isSub() {
        return this.isSub;
    }

    public int getCheerCount() {
        return this.cheerCount;
    }

    public boolean hasBadge(String badge) {
        for (String badgeA : this.badges) {
            if (badgeA.split("/", 2)[0].equals(badge)) {
                return true;
            }
        }
        return false;
    }

    public String getSender() {
        return this.sender;
    }

    public String getSenderElseDisplay(){
        return sender;
    }

    public String getDisplayName() {
        return this.displayName;
    }


    public String getMessage() {
        return this.message;
    }
}
