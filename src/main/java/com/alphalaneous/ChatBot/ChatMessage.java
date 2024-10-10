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
    private final boolean isYouTube;


    public ChatMessage(String[] tags, String sender, String displayName, String message, String[] badges, boolean isMod, boolean isSub, boolean isVIP, boolean isFirstMessage, boolean isCustomReward, boolean isYouTube) {
        this.tags = tags;
        this.sender = sender;
        this.displayName = displayName;
        this.message = message;
        this.badges = badges;
        this.isMod = isMod;
        this.isSub = isSub;
        this.isVIP = isVIP;
        this.isYouTube = isYouTube;
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

    public boolean isYouTube() {
        return isYouTube;
    }

    public UserLevel getUserLevel(){

        if (TwitchAccount.login != null && TwitchAccount.login.equalsIgnoreCase(sender)) {
            return UserLevel.OWNER;
        }

        if(isMod) return UserLevel.MODERATOR;
        if(isVIP) return UserLevel.VIP;
        if(isSub) return UserLevel.SUBSCRIBER;
        return UserLevel.EVERYONE;
    }

    public boolean isMod() {

        if (TwitchAccount.login != null && TwitchAccount.login.equalsIgnoreCase(sender)) {
            return true;
        }

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
