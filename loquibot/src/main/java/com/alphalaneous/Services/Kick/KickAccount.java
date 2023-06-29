package com.alphalaneous.Services.Kick;

public class KickAccount {

    public static int chatroomID = 0;
    public static String username = "";
    public static String profilePicURL = "";

    public static void setChatroomID(int chatroomID){
        KickAccount.chatroomID = chatroomID;
    }
    public static void setUsername(String username){
        KickAccount.username = username;
    }
    public static void setProfilePicURL(String profilePicURL){
        KickAccount.profilePicURL = profilePicURL;
    }

}
