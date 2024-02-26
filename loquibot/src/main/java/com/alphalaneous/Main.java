package com.alphalaneous;

import com.alphalaneous.ChatBot.ServerBot;
import com.alphalaneous.Interactive.CheerActions.LoadCheerActions;
import com.alphalaneous.Services.GeometryDash.*;
import com.alphalaneous.Images.Assets;
import com.alphalaneous.Interactive.ChannelPoints.LoadPoints;
import com.alphalaneous.Interactive.Commands.LoadCommands;
import com.alphalaneous.Interactive.Keywords.LoadKeywords;
import com.alphalaneous.Interactive.Timers.LoadTimers;
import com.alphalaneous.Interactive.Timers.TimerHandler;
import com.alphalaneous.Services.Twitch.TwitchAPI;
import com.alphalaneous.Settings.Account;
import com.alphalaneous.Settings.ChannelPoints;
import com.alphalaneous.Settings.Outputs;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Settings.Logs.LoggedID;
import com.alphalaneous.Swing.Components.LevelDetailsPanel;
import com.alphalaneous.Services.Twitch.TwitchChatListener;
import com.alphalaneous.Services.YouTube.YouTubeChatListener;
import com.alphalaneous.Services.YouTube.YouTubeAccount;
import com.alphalaneous.Tabs.*;
import com.alphalaneous.Services.Twitch.TwitchAccount;
import com.alphalaneous.Services.Twitch.TwitchListener;
import com.alphalaneous.Theming.Themes;
import com.alphalaneous.Utils.*;
import com.alphalaneous.Utils.KeyListener;
import com.alphalaneous.Windows.*;
import com.alphalaneous.Windows.Window;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.io.IoBuilder;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    public static String logFile = "";

    static{

        Date now = new Date();
        SimpleDateFormat format =
                new SimpleDateFormat ("yyyy.MM.dd-HH.mm.ss.SSSS");
        try {
            if(!Files.isDirectory(Paths.get(Defaults.saveDirectory + "/loquibot"))){
                Files.createDirectory(Paths.get(Defaults.saveDirectory + "/loquibot"));
            }
            if(!Files.isDirectory(Paths.get(Defaults.saveDirectory + "/loquibot/logs/"))){
                Files.createDirectory(Paths.get(Defaults.saveDirectory + "/loquibot/logs/"));
            }
        } catch (IOException e) {
            System.out.println("Failed to create log file directories");
        }

        String formatted = format.format(now);

        logFile = Defaults.saveDirectory + "/loquibot/logs/" + formatted;
        ThreadContext.put("filePath", logFile);
    }

    public static final Logger logger = LogManager.getLogger(Main.class);

    static {
        System.setProperty("sun.java2d.noddraw", "true");
        System.setProperty("io.netty.tryReflectionSetAccessible", "true");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }
        LogWindow.createWindow();
    }

    public static boolean programLoaded = false;
    public static boolean sendMessages = false;
    public static boolean allowRequests = false;
    public static Thread keyboardHookThread;

    private static TwitchListener channelPointListener;
    private static TwitchChatListener chatReader;
    private static boolean failed = false;
    private static final ArrayList<Image> iconImages = new ArrayList<>();

    private static final JFrame starting = new JFrame("loquibot");
    private static ConnectorSocket streamDeckSocket;

    public static void main(String[] args) throws IOException, InterruptedException {

        long time = System.currentTimeMillis();

        System.setErr(IoBuilder.forLogger(LogManager.getRootLogger()).setLevel(org.apache.logging.log4j.Level.ERROR).buildPrintStream());

        SettingsHandler.loadSettings();

        logger.info("Settings Loaded");

        boolean reopen = SettingsHandler.getSettings("hasUpdated").asBoolean();
        SettingsHandler.writeSettings("hasUpdated", "false");

        try {
            Assets.load();
            Assets.loadAssets();

            ImageIcon icon = Assets.loquibot;
            ImageIcon iconLarge = Assets.loquibotLarge;
            Image newIcon16 = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            Image newIcon32 = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            Image newIcon512 = iconLarge.getImage().getScaledInstance(512, 512, Image.SCALE_SMOOTH);

            iconImages.add(newIcon16);
            iconImages.add(newIcon32);
            iconImages.add(newIcon512);

        }
        catch (Exception e){
            Main.logger.error(e.getLocalizedMessage(), e);

        }
        logger.info("Loaded Icons");

        starting.setSize(200, 200);
        starting.setResizable(false);
        starting.setLocationRelativeTo(null);
        starting.setUndecorated(true);
        starting.setBackground(new Color(0, 0, 0, 0));
        starting.add(new JLabel(Assets.loquibotLarge));
        starting.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        starting.setIconImages(iconImages);
        starting.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        starting.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Main.close();
            }
        });
        starting.setVisible(true);

        //if(!SettingsHandler.getSettings("runAtStartup").asBoolean() || reopen) starting.setVisible(true);

        /*new LoquibotSocket();
        try {
            URI originalURI = new URI("ws://127.0.0.1:18562");
            CheckIfRunning checkIfRunning = new CheckIfRunning(originalURI);
            checkIfRunning.connectBlocking();
        } catch (Exception e) {
            Main.logger.error(e.getLocalizedMessage(), e);
        }*/

        if(Defaults.isMac()) {
            Taskbar taskbar = Taskbar.getTaskbar();
            try {
                taskbar.setIconImage(Main.getIconImages().get(2));
            } catch (UnsupportedOperationException | SecurityException e) {
                Main.logger.error(e.getLocalizedMessage(), e);

            }
        }

        //Initialize JavaFX Graphics Toolkit (Hacky Solution)
        if(!Defaults.isMac()) new Thread(JFXPanel::new).start();

        setUI();

        logger.info("UI Set");

        new Thread(() -> {
            Utilities.sleep(21600000);
            if(SettingsHandler.getSettings("runAtStartup").asBoolean() && !Window.getWindow().isVisible()) {
                restart();
            }
        }).start();

        if(SettingsHandler.getSettings("channel").exists() && !SettingsHandler.getSettings("twitchEnabled").exists()){
            SettingsHandler.writeSettings("twitchEnabled", "true");
        }

        Defaults.setSystem(false);

        logger.info("System Theme Set");

        new Thread(Main::runKeyboardHook).start();

        logger.info("Windows Keyboard Hook Initialized");


        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            Main.logger.error(e.getLocalizedMessage(), e);

        }

        logger.info("Look and Feel Set");



        logger.info("Start");

        LoadGD.load();
        GDAPI.startBatchListener();
        Themes.loadTheme();

        logger.info("Themes Loaded");

        if (SettingsHandler.getSettings("onboarding").exists()) {

            if(!SettingsHandler.getSettings("oauth").exists()){
                if (SettingsHandler.getSettings("twitchEnabled").asBoolean()) TwitchAPI.setOauth();
            }

            if (SettingsHandler.getSettings("youtubeEnabled").asBoolean()) {
                YouTubeAccount.setCredential(false);
            }
        }

        TwitchAccount.setInfo();
        ChannelPoints.refresh();
        YouTubeAccount.setInfo();
        Account.refreshYouTube(YouTubeAccount.name);

        logger.info("Twitch Loaded");
        try {
            try {
                Language.loadLanguage();
                Language.startFileChangeListener();
            }
            catch (IllegalArgumentException e){
                logger.info("Language Change Listener Failed");
            }

            new Thread(Defaults::startMainThread).start();
            new Thread(streamDeckSocket = new ConnectorSocket()).start();

            logger.info("Main Threads Started");

            Window.loadSettings();


            logger.info("Window Settings Loaded");
            try {
                Window.initFrame();
            }
            catch (Exception e){
                Main.logger.error(e.getLocalizedMessage(), e);
            }

            logger.info("Window initialized");
            RequestsTab.createPanel();
            logger.info("Requests Tab created");
            ChatbotTab.createPanel();
            logger.info("Chatbot Tab created");
            SettingsTab.createPanel();
            logger.info("Settings Tab created");
            OfficerWindow.create();
            logger.info("Officers Window created");
            Window.loadTopComponent();
            logger.info("Top Tab shown");
            LoadCommands.loadCommands();
            logger.info("Commands Loaded");
            LoadTimers.loadTimers();
            logger.info("Timers Loaded");
            LoadPoints.loadPoints();
            logger.info("Channel Points Loaded");
            LoadKeywords.loadKeywords();
            logger.info("Keywords Loaded");
            LoadCheerActions.loadCheerActions();
            logger.info("Cheer Actions Loaded");
            LoggedID.loadLoggedIDs();
            logger.info("IDs Loaded");
            TimerHandler.startTimerHandler();
            logger.info("Timer Handler Started");

            LevelDetailsPanel.setPanel(null);

            Platform.setImplicitExit(false);

            logger.info("All Panels Created");

            UpdateChecker.checkForUpdates();
            logger.info("Started Update Checker");

            Defaults.initializeThemeInfo();
            logger.info("Theme Info Initialized");
            Themes.refreshUI();
            logger.info("UI Refreshed");
            if(Defaults.isAprilFools) {
                AprilFools.create();
                AprilFools.loadLevels();
            }
            starting.setVisible(false);

            logger.info("Launch Finished in " + (System.currentTimeMillis() - time) + "ms");

            //If first time launch, the user has to go through onboarding
            //Show it and wait until finished


            if (!SettingsHandler.getSettings("onboarding").exists()) {
                Onboarding.createPanel();
                Window.setVisible(true);
                logger.info("Window Visible");

                Onboarding.refreshUI();
                Onboarding.isLoading = true;
                while (Onboarding.isLoading) {
                    Utilities.sleep(100);
                }
                TwitchAccount.setInfo();
                YouTubeAccount.setInfo();

                if(SettingsHandler.getSettings("youtubeEnabled").asBoolean()) Account.refreshYouTube(YouTubeAccount.name);
                new Thread(ChannelPoints::refresh).start();
            }
            else {
                if(!SettingsHandler.getSettings("runAtStartup").asBoolean() || reopen) Window.setVisible(true);
                logger.info("Window Visible");
            }

            logger.info("Command Variables Loaded");

            ServerBot.connect();
            System.out.println( System.currentTimeMillis() - time);


            if(SettingsHandler.getSettings("youtubeEnabled").asBoolean()){
                new Thread(YouTubeChatListener::startChatListener).start();
                logger.info("YouTube Chat Listener Started");
            }

            if(SettingsHandler.getSettings("twitchEnabled").asBoolean()) {
                new NamedThread("TwitchChatListener", () -> {
                    chatReader = new TwitchChatListener(TwitchAccount.login);
                    chatReader.connect(SettingsHandler.getSettings("oauth").asString());
                    logger.info("Twitch Chat Listener Started");
                }).start();
                new NamedThread("TwitchChannelPointListener", () -> {
                    try {
                        channelPointListener = new TwitchListener(new URI("wss://pubsub-edge.twitch.tv"));
                        channelPointListener.connect();
                        logger.info("Channel Point Listener Started");
                    } catch (URISyntaxException e) {
                        Main.logger.error(e.getLocalizedMessage(), e);
                    }
                }).start();
            }


            Window.setOnTop(SettingsHandler.getSettings("onTop").asBoolean());
            try {
                Outputs.setOutputStringFile(RequestsUtils.parseInfoString(SettingsHandler.getSettings("outputString").asString()));
            }
            catch (Exception e){
                SettingsHandler.writeSettings("outputFileLocation", Paths.get(Defaults.saveDirectory + "\\loquibot").toString());
            }

            Path file = Paths.get(Defaults.saveDirectory + "\\loquibot\\saved.json");
            if (Files.exists(file)) {
                String levelsJson = Files.readString(file, StandardCharsets.UTF_8);
                try {
                    JSONObject object = new JSONObject(levelsJson);
                    Requests.loadLevels(object);
                }
                catch (Exception ignored){
                }
            }
            logger.info("Saved IDs Loaded");
            allowRequests = true;
            RequestFunctions.saveFunction();
            RequestsTab.getLevelsPanel().setSelect(0);

            sendMessages = true;
            if(SettingsHandler.getSettings("twitchEnabled").asBoolean()) {
                boolean loquiIsMod = TwitchAPI.isLoquiMod();
                SettingsHandler.writeSettings("isMod", String.valueOf(loquiIsMod));
            }
            programLoaded = true;

            Window.setVisible(true);

        } catch (Exception e) {
            Main.logger.error(e.getLocalizedMessage(), e);
            DialogBox.showDialogBox("Error!", e + ": " + e.getStackTrace()[0], "Please report to @Alphalaneous on Discord.", new String[]{"Close"});
            close();
        }
    }

    public static void sendMessageConnectedService(String message){
        if(streamDeckSocket != null) {
            streamDeckSocket.sendMessage(message);
        }
    }

    public static void restart(){
        try {
            Runtime.getRuntime().exec(SettingsHandler.getSettings("installPath").asString());
            Main.forceClose();
        }
        catch (Exception e){
            logger.fatal("Failed restart, closing...");
            Main.logger.error(e.getLocalizedMessage(), e);
            System.exit(0);
        }
    }

    public static ArrayList<Image> getIconImages() {
        return iconImages;
    }

    public static void sendMainMessage(String message) {
        TwitchChatListener.getCurrentListener().sendMessage(message);
    }

    public static void sendToServer(String message) {
        try {
            ServerBot.sendMessage(message);
        } catch (Exception ignored) {
        }
    }

    public static void sendMessageWithoutCooldown(String message){
        if(SettingsHandler.getSettings("twitchEnabled").asBoolean()) {
            JSONObject messageObj = new JSONObject();
            messageObj.put("request_type", "send_message");
            messageObj.put("message", message);
            ServerBot.sendMessage(messageObj.toString());
        }
    }

    public static void sendMessage(String messageA, String messageID) {

        if(SettingsHandler.getSettings("twitchEnabled").asBoolean()) {

            String[] messages = messageA.split("¦");
            for (String message : messages) {

                if (!SettingsHandler.getSettings("silentMode").asBoolean() || message.equalsIgnoreCase(" ")) {
                    if (!message.equalsIgnoreCase("")) {

                        JSONObject messageObj = new JSONObject();
                        messageObj.put("request_type", "send_message");
                        if (SettingsHandler.getSettings("antiDox").asBoolean()) {
                            message = Language.modify(message.replaceAll(System.getProperty("user.name"), "*****"));
                        }
                        messageObj.put("message", message);
                        if(messageID != null && !messageID.trim().equalsIgnoreCase("")){
                            messageObj.put("reply-id", messageID);
                        }
                        ServerBot.sendMessage(messageObj.toString());
                    }
                }
            }
        }
    }

    public static void sendYTMessage(String messageA, String username){
        if(SettingsHandler.getSettings("youtubeEnabled").asBoolean()){
            String[] messages = messageA.split("¦");
            for(String message : messages) {

                if (!SettingsHandler.getSettings("silentMode").asBoolean() || message.equalsIgnoreCase(" ")) {
                    if (!message.equalsIgnoreCase("")) {

                        JSONObject messageObj = new JSONObject();
                        messageObj.put("request_type", "send_yt_message");
                        messageObj.put("liveChatId", YouTubeAccount.liveChatId);

                        if (SettingsHandler.getSettings("antiDox").asBoolean()) {
                            message = Language.modify(message.replaceAll(System.getProperty("user.name"), "*****"));
                        }

                        String pingStart = "";

                        if(username != null && !username.equalsIgnoreCase("")){
                            pingStart = com.alphalaneous.Utils.Utilities.format("@%s, ", username);
                        }
                        messageObj.put("username", YouTubeAccount.name);
                        messageObj.put("message", pingStart + message);

                        if(YouTubeAccount.liveChatId != null) {
                            try {
                                ServerBot.sendMessage(messageObj.toString());
                            } catch (Exception e) {
                                Main.logger.error(e.getLocalizedMessage(), e);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void sendMessage(String message) {
        sendMessage(message, "");
    }
    public static void sendMessage(String message, boolean doAnnounce) {
        if(SettingsHandler.getSettings("twitchEnabled").asBoolean()) {
            SettingsHandler.writeSettings("isMod", String.valueOf(TwitchAPI.isLoquiMod()));
            if (doAnnounce && SettingsHandler.getSettings("isMod").asBoolean() && !message.startsWith("/")) {
                sendMessage("/announce " + message);
            }
            else sendMessage(message);
        }
    }

    private static void runKeyboardHook() {
        //if (!Defaults.isMac()) {
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
            } catch(Exception e){
                try {
                    GlobalScreen.unregisterNativeHook();
                } catch (NativeHookException e1) {
                    Main.logger.error(e1.getLocalizedMessage(), e1);
                }
                failed = true;
            }
            keyboardHookThread = new Thread(() -> {
                while (runHook.get()) {
                    if (failed) {
                        runKeyboardHook();
                    }
                    Utilities.sleep(100);
                }
            });
            keyboardHookThread.start();

    }

    public static void close() {

        ServerBot.disconnect();
        ServerBot.reconnect = false;

        if(!SettingsHandler.getSettings("runAtStartup").asBoolean()) {
            com.alphalaneous.Utils.Utilities.disposeTray();
            if (!SettingsHandler.getSettings("onboarding").asBoolean()) {
                forceClose();
            }
            System.exit(0);
        }
        else {
            Window.setVisible(false);
        }
    }

    public static void forceClose(){

        com.alphalaneous.Utils.Utilities.disposeTray();
        Window.setVisible(false);
        try {
            if(TwitchListener.getCurrentTwitchListener() != null) {
                TwitchListener.getCurrentTwitchListener().disconnectBot();
            }
            if(TwitchChatListener.getCurrentListener() != null) {
                TwitchChatListener.getCurrentListener().disconnect();
            }
            if(ServerBot.clientSocket != null) {
                ServerBot.disconnect();
            }
            if(!Defaults.isMac()) GlobalScreen.unregisterNativeHook();
        } catch (Exception e) {
            logger.fatal("Failed closing properly, forcing close");
            Main.logger.error(e.getLocalizedMessage(), e);
        }
        System.exit(0);
    }


    public static void setUI() {
        HashMap<Object, Object> defaults = new HashMap<>();
        for (Map.Entry<Object, Object> entry : UIManager.getDefaults().entrySet()) {
            if (entry.getKey().getClass() == String.class && ((String) entry.getKey()).startsWith("ProgressBar")) {
                defaults.put(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<Object, Object> entry : UIManager.getDefaults().entrySet()) {
            if (entry.getKey().getClass() == String.class && ((String) entry.getKey()).startsWith("ToolTip")) {
                defaults.put(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<Object, Object> entry : UIManager.getDefaults().entrySet()) {
            if (entry.getKey().getClass() == String.class && ((String) entry.getKey()).startsWith("MenuItem")) {
                defaults.put(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<Object, Object> entry : UIManager.getDefaults().entrySet()) {
            if (entry.getKey().getClass() == String.class && ((String) entry.getKey()).startsWith("ScrollBar")) {
                defaults.put(entry.getKey(), entry.getValue());
            }
        }

        for (Map.Entry<Object, Object> entry : defaults.entrySet()) {
            UIManager.getDefaults().put(entry.getKey(), entry.getValue());
        }


        System.setProperty("sun.awt.noerasebackground", "true");
        UIManager.put("Menu.selectionBackground", Color.RED);
        UIManager.put("Menu.selectionForeground", Color.WHITE);
        UIManager.put("Menu.background", Color.WHITE);
        UIManager.put("Menu.foreground", Color.BLACK);
        UIManager.put("Menu.opaque", false);
        UIManager.put("ToolTipManager.enableToolTipMode", "allWindows");

    }

    public static JFrame getStartingFrame(){
        return starting;
    }

}
