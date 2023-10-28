package com.alphalaneous;

import com.alphalaneous.Annotations.AnnotationHandler;
import com.alphalaneous.ChatBot.TwitchChatListener;
import com.alphalaneous.Services.Twitch.TwitchAccount;
import com.alphalaneous.Utilities.Utilities;

public class Main {


    static {
        System.setProperty("com.sun.webkit.useHTTP2Loader", "false");
        AnnotationHandler.loadStartingMethods();
        PluginHandler.loadPlugins();
        AnnotationHandler.loadPluginMethods();
    }


    public static void main(String[] args) {

        if (!SettingsHandler.getSettings("onboardingCompleted").exists()){
            Onboarding.init();
            Window.setVisible(true);
            Utilities.wait(Onboarding.isCompleted);
        }

        new Thread(() -> {
            TwitchAccount.setInfo();
            TwitchChatListener chatListener = new TwitchChatListener(TwitchAccount.login);
            chatListener.connect(SettingsHandler.getSettings("oauth").asString());
        }).start();


        if(SettingsHandler.getSettings("twitchUsername").exists()) {
            Window.loadChat(SettingsHandler.getSettings("twitchUsername").asString());
        }

        Window.setVisible(true);
    }



    public static void onExit(){
        System.exit(0);
    }
}