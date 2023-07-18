package com.alphalaneous.Utils;

import com.alphalaneous.Main;
import com.alphalaneous.Services.GeometryDash.RequestFunctions;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Swing.Components.KeybindButton;
import com.alphalaneous.Windows.LogWindow;
import com.alphalaneous.Windows.Window;
import com.tulskiy.keymaster.common.Provider;

import javax.swing.*;

public class MacKeyListener {

    public static Provider provider;

    static {
        if(Defaults.isMac()){
            provider = Provider.getCurrentProvider(true);
        }
    }

    public static void addKey(int keyCode) {
        System.out.println(keyCode);
        if (keyCode != -1) {
            if (Defaults.isMac())
                provider.register(KeyStroke.getKeyStroke(keyCode, 0), hotKey -> runKeyCheck(KeyStroke.getKeyStroke(keyCode, 0)));
        }
    }
    public static void removeKey(int keyCode){
        if(Defaults.isMac()) provider.unregister(KeyStroke.getKeyStroke(keyCode, 0));
    }

    public static void stop(){
        if(Defaults.isMac()) {

            provider.reset();
            provider.close();
        }
    }
    private static boolean ctrlPressed = true;

    public static void loadKeybinds(){
        if(Defaults.isMac()) {
            MacKeyListener.addKey(SettingsHandler.getSettings("openKeybind").asInteger());
            MacKeyListener.addKey(SettingsHandler.getSettings("skipKeybind").asInteger());
            MacKeyListener.addKey(SettingsHandler.getSettings("undoKeybind").asInteger());
            MacKeyListener.addKey(SettingsHandler.getSettings("randomKeybind").asInteger());
            MacKeyListener.addKey(SettingsHandler.getSettings("blockKeybind").asInteger());
            MacKeyListener.addKey(SettingsHandler.getSettings("clearKeybind").asInteger());
        }
    }

    public static void runKeyCheck(KeyStroke keyStroke) {

        int key = keyStroke.getKeyCode();

        System.out.println(key);

        if (key == 187) {
            key = 61;
        } else if (key == 189) {
            key = 45;
        } else if (key == 190) {
            key = 46;
        } else if (key == 188) {
            key = 44;
        } else if (key == 186) {
            key = 59;
        } else if (key == 220) {
            key = 92;
        } else if (key == 221) {
            key = 93;
        } else if (key == 219) {
            key = 91;
        } else if (key == 191) {
            key = 47;
        } else if (key == 46) {
            key = 127;
        } else if (key == 45) {
            key = 155;
        }
        if (!KeybindButton.getInFocus()) {
            if (key == SettingsHandler.getSettings("openKeybind").asInteger()) {
                Window.focus();
            }
            if (key == SettingsHandler.getSettings("skipKeybind").asInteger()) {
                RequestFunctions.skipFunction();
            }
            if (key == SettingsHandler.getSettings("undoKeybind").asInteger()) {
                RequestFunctions.undoFunction();
            }
            if (key == SettingsHandler.getSettings("randomKeybind").asInteger()) {
                RequestFunctions.randomFunction();
            }
            if (key == SettingsHandler.getSettings("copyKeybind").asInteger()) {
                RequestFunctions.copyFunction();
            }
            if (key == SettingsHandler.getSettings("blockKeybind").asInteger()) {
                RequestFunctions.blockFunction();
            }
            if (key == SettingsHandler.getSettings("clearKeybind").asInteger()) {
                RequestFunctions.clearFunction();
            }
        }
        if (Window.getWindow().isFocused()
                || LogWindow.getWindow().isFocused()
                || Main.getStartingFrame().isFocused()) {
            if (ctrlPressed && key == 123) {
                LogWindow.toggleLogWindow();
            }
        }
        ctrlPressed = keyStroke.getKeyCode() == 162 || keyStroke.getKeyCode() == 163;
    }
}
