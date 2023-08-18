package com.alphalaneous.Moderation;

import com.alphalaneous.Main;
import com.alphalaneous.Settings.SettingsHandler;

import java.util.HashMap;

public class Warning {

    private final HashMap<String, Integer> warningMap = new HashMap<>();

    private final String username;

    public Warning(String username){
        this.username = username;
    }
    public String getUsername(){
        return username;
    }

    public void addWarning(String type){
        if(warningMap.containsKey(type)) warningMap.put(type, warningMap.get(type)+1);
        else warningMap.put(type, 1);

        if(warningMap.get(type) >= SettingsHandler.getSettings(type+"Warnings").asInteger()){

            int timeoutDuration = SettingsHandler.getSettings(type+ "TimeoutDuration").asInteger();
            if(timeoutDuration != 0) {
                Main.sendMessage("/timeout " + username + " " + timeoutDuration);
            }
        }
        new Thread(() -> {
            try {
                Thread.sleep(300000);
            } catch (InterruptedException e) {
                Main.logger.error(e.getLocalizedMessage(), e);
            }
            warningMap.put(type, warningMap.get(type)-1);
        }).start();
    }
}
