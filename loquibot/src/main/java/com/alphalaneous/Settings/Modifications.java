package com.alphalaneous.Settings;

import com.alphalaneous.Memory.Global;
import com.alphalaneous.Memory.Hacks;
import com.alphalaneous.Memory.Level;
import com.alphalaneous.Memory.MemoryHelper;
import com.alphalaneous.Swing.Components.KeybindButton;
import com.alphalaneous.Swing.Components.SettingsComponent;
import com.alphalaneous.Swing.Components.SettingsPage;
import com.alphalaneous.Utilities;

import javax.swing.*;
import java.awt.*;

public class Modifications {

    public static JPanel createPanel() {

        loadMods();

        SettingsPage settingsPage = new SettingsPage("$MODS_SETTINGS$");

        settingsPage.addCheckbox("$IN_GAME_NOW_PLAYING$", "$IN_GAME_NOW_PLAYING_DESC$", "inGameNowPlaying");
        settingsPage.addCheckbox("$PRACTICE_MUSIC_HACK$", "$PRACTICE_MUSIC_HACK_DESC$", "practiceMusicHack",
                () -> Hacks.setPracticeMusicHack(SettingsHandler.getSettings("practiceMusicHack").asBoolean()));
        settingsPage.addCheckbox("$SAFE_NOCLIP_HACK$", "$SAFE_NOCLIP_HACK_DESC$", "safeNoclipHack",
                () -> {
            setSafeMode();
            Hacks.setNoclip(SettingsHandler.getSettings("safeNoclipHack").asBoolean());
                });
        settingsPage.addComponent(createKeybindComponent(new KeybindButton("$SAFE_NOCLIP_SHORTCUT$", "safeNoclipKeybind")));


        return settingsPage;
    }
    static Thread inLevel;
    static Thread onDeath;
    public static void setSafeMode(){
        boolean isSafe = getSafeMode();

        if(Global.isInLevel() && !isSafe){
            inLevel = Global.onLeaveLevel(() -> {
                Hacks.setSafeMode(false);
                inLevel.stop();
            });
            onDeath = Level.onDeath(() -> {
                Hacks.setSafeMode(false);
                onDeath.stop();
            });
        }
        else{
            Hacks.setSafeMode(isSafe);
        }
    }

    public static boolean getSafeMode(){

        boolean isSafe = false;

        if(SettingsHandler.getSettings("safeNoclipHack").asBoolean()){
            isSafe = true;
        }

        //more soon
        return isSafe;
    }


    public static void loadMods(){


        new Thread(() -> {
            boolean GDOpen = false;
            while (true){
                if(MemoryHelper.isGDOpen() && !GDOpen){

                    if(getSafeMode()){
                        Hacks.setSafeMode(true);
                    }

                    if(SettingsHandler.getSettings("practiceMusicHack").asBoolean()){
                        Hacks.setPracticeMusicHack(true);
                    }

                    if(SettingsHandler.getSettings("safeNoclipHack").asBoolean()){
                        Hacks.setNoclip(true);
                    }

                    GDOpen = true;

                }
                else if(GDOpen){
                    GDOpen = false;
                }
                Utilities.sleep(100);
            }
        }).start();
    }
    private static SettingsComponent createKeybindComponent(KeybindButton button){
        return new SettingsComponent(button, new Dimension(475,30)){
            @Override
            protected void resizeComponent(Dimension dimension){
                button.resizeButton(dimension.width);
            }
            @Override
            protected void refreshUI(){
                button.refreshUI();
            }
        };
    }
}
