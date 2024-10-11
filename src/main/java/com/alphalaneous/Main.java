package com.alphalaneous;

import com.alphalaneous.Annotations.AnnotationHandler;
import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Services.Twitch.TwitchChatListener;
import com.alphalaneous.Pages.InteractionPages.ChannelPointsPage;
import com.alphalaneous.Pages.SettingsSubPages.AccountsPage;
import com.alphalaneous.Pages.SettingsSubPages.PersonalizationPage;
import com.alphalaneous.Pages.StreamInteractionsPage;
import com.alphalaneous.Services.Twitch.TwitchAccount;
import com.alphalaneous.Services.YouTube.YouTubeAccount;
import com.alphalaneous.Utilities.*;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    /*
    todo
      - Add default commands section in settings
      - GD Level Requests
     */

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException ignored) {
        }

        System.setProperty("sun.java2d.noddraw", "true");
        System.setProperty("sun.awt.noerasebackground", "true");
        System.setProperty("com.sun.webkit.useHTTP2Loader", "false");

        AnnotationHandler.loadStartingMethods();
        PluginHandler.loadPlugins();
        AnnotationHandler.loadPluginMethods();
    }

    public static void main(String[] args) {

        if (!SettingsHandler.getSettings("onboardingCompleted").exists()){
            Onboarding.init();
            Window.setVisible(true);
            hideStartingWindow();
            Utilities.wait(Onboarding.isCompleted);
        }
        else {
            if (SettingsHandler.getSettings("isTwitchLoggedIn").asBoolean()) {
                new Thread(() -> TwitchAccount.setInfo(() -> {
                    AccountsPage.setTwitchAccountInfo();
                    TwitchChatListener chatListener = new TwitchChatListener(TwitchAccount.login);
                    chatListener.connect(SettingsHandler.getSettings("oauth").asString());
                    ChannelPointsPage.load();
                    Window.loadTwitchChat(TwitchAccount.login);
                })).start();

                StreamInteractionsPage.setEnabled(true);
            }

            if (SettingsHandler.getSettings("isYouTubeLoggedIn").asBoolean()) {
                new Thread(() -> {
                    try {
                        YouTubeAccount.setCredential(false, false);
                        AccountsPage.setYouTubeAccountInfo();
                    } catch (Exception e) {
                        YouTubeAccount.setCredential(true, true);
                    }

                }).start();
            }
        }

        hideStartingWindow();
        new Thread(Main::runKeyboardHook).start();

        PersonalizationPage.setTheme();
        Window.setVisible(true);
    }

    private static JFrame starting;

    @OnLoad(order = -99999)
    public static void createStartingWindow(){

        starting = new JFrame("Loquibot");
        ArrayList<Image> iconImages = new ArrayList<>();
        iconImages.add(Assets.getImage("loquibot-small-icon").getImage());
        iconImages.add(Assets.getImage("loquibot-medium-icon").getImage());
        iconImages.add(Assets.getImage("loquibot-large-icon").getImage());

        starting.setSize(200, 200);
        starting.setResizable(false);
        starting.setLocationRelativeTo(null);
        starting.setUndecorated(true);
        starting.setBackground(new Color(0, 0, 0, 0));
        starting.add(new JLabel(Assets.getImage("loquibot-splash-icon")));
        starting.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        starting.setIconImages(iconImages);
        starting.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        starting.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });
        starting.setVisible(true);
    }

    public static void hideStartingWindow(){
        starting.setVisible(false);
        starting.dispose();
    }

    @OnLoad
    public static void checkIfOpen(){
        String appId = "com.alphalaneous.loquibot";
        boolean alreadyRunning;
        try {
            JUnique.acquireLock(appId, message -> {
                if(message.equals("setVisible")){
                    Logging.getLogger().info("Another Instanced opened, making main instance visible.");
                    Window.getFrame().setState(JFrame.NORMAL);
                    Window.setVisible(true);
                }
                return null;
            });

            alreadyRunning = false;
        } catch (AlreadyLockedException e) {
            alreadyRunning = true;
        }

        if (alreadyRunning) {
            JUnique.sendMessage(appId, "setVisible");
            System.exit(0);
        }
    }

    public static Thread keyboardHookThread;
    private static boolean failedKeyboardHook = false;
    private static void runKeyboardHook() {

        AtomicBoolean runHook = new AtomicBoolean(true);

        if (keyboardHookThread != null) {
            if (keyboardHookThread.isAlive()) {
                runHook.set(false);
            }
        }
        try {
            if (GlobalScreen.isNativeHookRegistered()) {
                GlobalScreen.unregisterNativeHook();
            }
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new KeyListener());
            while (GlobalScreen.isNativeHookRegistered()) {
                Utilities.sleep(100);
            }
        } catch (Exception e){
            Logging.getLogger().error(e.getMessage(), e);
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException e1) {
                Logging.getLogger().error(e1.getMessage(), e1);
            }
            failedKeyboardHook = true;
        }

        keyboardHookThread = new Thread(() -> {
            while (runHook.get()) {
                if (failedKeyboardHook) {
                    runKeyboardHook();
                }
                Utilities.sleep(100);
            }
        });
        keyboardHookThread.start();

    }

    public static void onExit(){
        System.exit(0);
    }
}