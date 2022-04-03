//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.alphalaneous.TwitchBot;

import com.alphalaneous.TwitchAccount;

import java.util.Locale;

public class ChatMessage {
    private final String[] tags;
    private final String sender;
    private final String displayName;
    private String message;
    private String[] args;
    private final String[] badges;
    private boolean isMod;
    private final boolean isSub;
    private final boolean isVIP;
    private final boolean isFirstMessage;

    private final int cheerCount;

    public ChatMessage(String[] tags, String sender, String displayName, String message, String[] badges, boolean isMod, boolean isSub, boolean isVIP, int cheerCount, boolean isFirstMessage) {
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
        this.args = message.split(" ");
    }

    public String[] getArgs(){
        return args;
    }

    public String getTag(String tag) {

        for (String tagA : this.tags) {
            if (tagA.split("=", 2)[0].equals(tag)) {
                return tagA.split("=", 2)[1];
            }
        }

        return null;
    }

    public boolean isFirstMessage(){
        return isFirstMessage;
    }

    public String getUserLevel(){

        if(TwitchAccount.login.toLowerCase(Locale.ROOT).equalsIgnoreCase(sender)) return "owner";
        if(isMod) return "moderator";
        if(isVIP) return "twitch_vip";
        if(isSub) return "subscriber";
        return "everyone";
    }

    public boolean isMod() {
        return this.isMod;
    }

    public void setMod(boolean isMod){
        this.isMod = isMod;
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

    public String getDisplayName() {
        return this.displayName;
    }


    public String getMessage() {
        return this.message;
    }
}
