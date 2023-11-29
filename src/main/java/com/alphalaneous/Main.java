package com.alphalaneous;

import com.alphalaneous.Annotations.AnnotationHandler;
import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.ChatBot.TwitchChatListener;
import com.alphalaneous.Pages.InteractionPages.ChannelPointsPage;
import com.alphalaneous.Pages.SettingsSubPages.AccountsPage;
import com.alphalaneous.Pages.SettingsSubPages.PersonalizationPage;
import com.alphalaneous.Services.Twitch.TwitchAccount;
import com.alphalaneous.Utilities.*;
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class Main {

    /*
    todo
      - Clean up accounts panels
      - Add YouTube support
      - Use loquibot account unless changed in account settings
      - Settings Categories
      - Onboarding and account management
      - Plugins Tab
     */


    static {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException ex) {
            Logging.getLogger().error(ex.getLocalizedMessage(), ex);
        }

        System.setProperty("sun.awt.noerasebackground", "true");
        System.setProperty("com.sun.webkit.useHTTP2Loader", "false");
        System.setProperty("sun.java2d.noddraw", "true");

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
            AccountsPage.setTwitchAccountInfo();
            TwitchChatListener chatListener = new TwitchChatListener(TwitchAccount.login);
            chatListener.connect(SettingsHandler.getSettings("oauth").asString());
            ChannelPointsPage.load();
        }).start();


        if(SettingsHandler.getSettings("twitchUsername").exists()) {
            Window.loadChat(SettingsHandler.getSettings("twitchUsername").asString());
        }

        hideStartingWindow();

        PersonalizationPage.setTheme();
        Window.setVisible(true);
    }

    private static JFrame starting;

    @OnLoad(order = -99999)
    public static void createStartingWindow(){

        starting = new JFrame("loquibot");
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

    public static void onExit(){
        System.exit(0);
    }
}