package com.alphalaneous.ChatBot;

import com.alphalaneous.Services.Twitch.TwitchAPI;
import com.alphalaneous.Utils.Utilities;

import java.util.ArrayList;

public class ChatterActivity {

    private static final ArrayList<ChatterActivity> chatterActivities = new ArrayList<>();

    private final String username;

    public ChatterActivity(String username){
        this.username = username;

        ChatterActivity activity = getActivity(username);
        if(activity != null){
            chatterActivities.remove(activity);
        }

        new Thread(() -> {
            Utilities.sleep(120000);
            chatterActivities.remove(this);
        }).start();
        chatterActivities.add(this);
    }

    public String getUsername(){
        return username;
    }

    public static boolean checkIfActive(String username){
        for(ChatterActivity activity : chatterActivities){
            if(activity != null) {
                if (activity.getUsername().equalsIgnoreCase(username) || TwitchAPI.isViewer(username)) {
                    return true;
                }
            }
        }
        return false;
    }
    public static ChatterActivity getActivity(String username){
        for(ChatterActivity activity : chatterActivities){
            if(activity != null) {
                if (activity.getUsername().equalsIgnoreCase(username)) {
                    return activity;
                }
            }
        }
        return null;
    }
}
