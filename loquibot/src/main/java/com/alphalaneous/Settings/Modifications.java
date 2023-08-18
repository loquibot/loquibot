package com.alphalaneous.Settings;

import com.alphalaneous.Main;
import com.alphalaneous.Memory.Global;
import com.alphalaneous.Memory.Hacks;
import com.alphalaneous.Memory.Level;
import com.alphalaneous.Memory.MemoryHelper;
import com.alphalaneous.Swing.Components.KeybindButton;
import com.alphalaneous.Swing.Components.SettingsComponent;
import com.alphalaneous.Swing.Components.SettingsPage;
import com.alphalaneous.Utilities;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Utils.Find;
import com.alphalaneous.Utils.RegQuery;
import com.alphalaneous.Windows.DialogBox;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.Objects;

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
        settingsPage.addButton("Install loquibot GD Mod", Modifications::installLoquiMod);

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

    public static void installLoquiMod(){
        new Thread(() -> {
            String path = "C:\\Program Files (x86)\\Steam\\steamapps\\common\\Geometry Dash\\GeometryDash.exe";
            String installDir;
            boolean skip = false;
            if(!Files.exists(Paths.get(path))){

                String steamPath = RegQuery.getSteamLocation();
                if(steamPath != null){
                    path = steamPath + "\\steamapps\\common\\Geometry Dash\\GeometryDash.exe";
                }
                if(!Files.exists(Paths.get(path))) {

                    String exePath = MemoryHelper.getExePath();
                    if(exePath.equalsIgnoreCase("")) {
                        DialogBox.showDialogBox("Oops", "Please open Geometry Dash to install!", "", new String[]{"Okay"});
                        skip = true;
                    }
                    else path = exePath;
                }
            }
            if(!skip) {
                boolean isGDOpen = Global.isGDOpen();

                if(isGDOpen) {
                    String choice = DialogBox.showDialogBox("Woah, wait a sec", "This will restart GD to install!", "", new String[]{"Okay", "Cancel"});

                    if (choice.equalsIgnoreCase("Cancel")) return;
                }
                else{
                    String choice = DialogBox.showDialogBox("Install?", "Are you sure you want to install this mod?", "", new String[]{"Okay", "Cancel"});

                    if (choice.equalsIgnoreCase("Cancel")) return;
                }
                try {
                    Main.logger.info("Closing GD");
                    com.alphalaneous.Utils.Utilities.runCommand("taskkill", "/IM", "GeometryDash.exe", "/F");
                }
                catch (Exception e){
                    Main.logger.info("GD Already Closed");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (Files.exists(Paths.get(Paths.get(path).getParent().toString() + "\\hackpro.dll"))) {
                    Main.logger.info("User has MegaHack");
                    installDir = "extensions";
                }
                else if (Files.exists(Paths.get(Paths.get(path).getParent().toString() + "\\ToastedMarshmellow.dll"))) {
                    Main.logger.info("User has HackerMode");
                    installDir = ".GDHM\\dll";
                }
                else if (Files.exists(Paths.get(Paths.get(path).getParent().toString() + "\\GDMenu.dll"))) {
                    Main.logger.info("User has MegaOverlay");
                    installDir = "GDMenu\\dll";
                }
                else {
                    Main.logger.info("User has no mod loader");
                    URL inputUrl = Main.class.getClassLoader()
                            .getResource("xinput9_1_0.dll");
                    File dest = new File(Paths.get(path).getParent().toString() + "\\xinput9_1_0.dll");
                    assert inputUrl != null;
                    try {
                        FileUtils.copyURLToFile(inputUrl, dest);
                        installDir = "adaf-dll";
                    }
                    catch (IOException e){
                        Main.logger.error("Failed to copy xinput");
                        installDir = null;
                    }
                }

                if(installDir != null){
                    installMod(Paths.get(path).getParent().toString() + "\\" + installDir);
                    if(isGDOpen) com.alphalaneous.Utils.Utilities.openSteamApp(322170, true);
                }
            }
        }).start();

    }
    public static void installMod(String installDir) {

        try {
            FileUtils.copyURLToFile(Objects.requireNonNull(Main.class.getClassLoader()
                    .getResource("LoquiExtension.dll")), Path.of(installDir + "\\LoquiExtension.dll").toFile());
        } catch (Exception e) {
            Main.logger.error(e.getLocalizedMessage(), e);

            DialogBox.showDialogBox("Error", "Failed to install mod!", e.toString(), new String[] {"Okay"});
        }
    }


    private static void deleteDirectoryRecursion(Path path) throws IOException {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteDirectoryRecursion(entry);
                }
            }
        }
        Files.delete(path);
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
        if(Defaults.isMac()) return;

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
