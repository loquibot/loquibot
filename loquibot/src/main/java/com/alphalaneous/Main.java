package com.alphalaneous;

import com.alphalaneous.ChatBot.KickBot;
import com.alphalaneous.ChatBot.ServerBot;
import com.alphalaneous.Interactive.CheerActions.CheerActionData;
import com.alphalaneous.Interactive.CheerActions.LoadCheerActions;
import com.alphalaneous.KickAPI.KickClient;
import com.alphalaneous.Memory.Global;
import com.alphalaneous.Memory.MemoryHelper;
import com.alphalaneous.Services.GeometryDash.LoadGD;
import com.alphalaneous.Services.GeometryDash.RequestFunctions;
import com.alphalaneous.Services.GeometryDash.Requests;
import com.alphalaneous.Services.GeometryDash.RequestsUtils;
import com.alphalaneous.Images.Assets;
import com.alphalaneous.Interactive.ChannelPoints.ChannelPointData;
import com.alphalaneous.Interactive.ChannelPoints.LoadPoints;
import com.alphalaneous.Interactive.Commands.CommandData;
import com.alphalaneous.Interactive.Commands.LoadCommands;
import com.alphalaneous.Interactive.Keywords.KeywordData;
import com.alphalaneous.Interactive.Keywords.LoadKeywords;
import com.alphalaneous.Interactive.Timers.LoadTimers;
import com.alphalaneous.Interactive.Timers.TimerData;
import com.alphalaneous.Interactive.Timers.TimerHandler;
import com.alphalaneous.Interactive.Variables;
import com.alphalaneous.Running.CheckIfRunning;
import com.alphalaneous.Running.LoquibotSocket;
import com.alphalaneous.Services.Kick.KickAccount;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    static {
        System.setProperty("sun.java2d.noddraw", "true");
        BackwardsCompatibilityLayer.setNewLocation();
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
    private static ImageIcon icon;
    private static ImageIcon iconLarge;

    private static Image newIcon16;
    private static Image newIcon32;
    private static Image newIcon512;

    private static final JFrame starting = new JFrame("loquibot");
    private static ConnectorSocket streamDeckSocket;

    public static void main(String[] args) throws IOException, InterruptedException {



        long time = System.currentTimeMillis();
        new LoquibotSocket();
        try {
            URI originalURI = new URI("ws://127.0.0.1:18562");
            CheckIfRunning checkIfRunning = new CheckIfRunning(originalURI);
            checkIfRunning.connectBlocking();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Assets.load();
            Assets.loadAssets();

            icon = Assets.loquibot;
            iconLarge = Assets.loquibotLarge;
            newIcon16 = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            newIcon32 = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            newIcon512 = iconLarge.getImage().getScaledInstance(512, 512, Image.SCALE_SMOOTH);

            iconImages.add(newIcon16);
            iconImages.add(newIcon32);
            iconImages.add(newIcon512);

        }
        catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("> Loaded Icons");

        if(Defaults.isMac()) {
            Taskbar taskbar = Taskbar.getTaskbar();
            try {
                taskbar.setIconImage(Main.getIconImages().get(2));
            } catch (UnsupportedOperationException | SecurityException e) {
                e.printStackTrace();
            }
        }

        //Initialize JavaFX Graphics Toolkit (Hacky Solution)
        if(!Defaults.isMac()) new Thread(JFXPanel::new).start();


        setUI();

        System.out.println("> UI Set");

        new Thread(() -> {
            Utilities.sleep(21600000);
            if(SettingsHandler.getSettings("runAtStartup").asBoolean() && !Window.getWindow().isVisible()) {
                restart();
            }
        }).start();

        SettingsHandler.loadSettings();
        //MacKeyListener.loadKeybinds();

        System.out.println("> Settings Loaded");

        boolean reopen = SettingsHandler.getSettings("hasUpdated").asBoolean();
        SettingsHandler.writeSettings("hasUpdated", "false");

        if(SettingsHandler.getSettings("channel").exists() && !SettingsHandler.getSettings("twitchEnabled").exists()){
            SettingsHandler.writeSettings("twitchEnabled", "true");
        }


        if(!Defaults.isMac()) {
            new Thread(() -> {
                Find.setup();
                Find.findPath();
            }).start();
        }

        Defaults.setSystem(false);

        System.out.println("> System Theme Set");

        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);

        System.out.println("> Logger Settings Set");


        new Thread(Main::runKeyboardHook).start();

        System.out.println("> Windows Keyboard Hook Initialized");


        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        System.out.println("> Look and Feel Set");


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


        if(!SettingsHandler.getSettings("runAtStartup").asBoolean() || reopen) starting.setVisible(true);

        System.out.println("> Start");

        LoadGD.load();
        Themes.loadTheme();

        System.out.println("> Themes Loaded");

        if (SettingsHandler.getSettings("onboarding").exists()) {

            if(!SettingsHandler.getSettings("oauth").exists()){
                if (SettingsHandler.getSettings("twitchEnabled").asBoolean()) TwitchAPI.setOauth();
            }

            try {
                if (SettingsHandler.getSettings("youtubeEnabled").asBoolean()) YouTubeAccount.setCredential(false);
            }

            catch (Exception f){
                f.printStackTrace();
                try {
                    YouTubeAccount.setCredential(true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            try {
                if (SettingsHandler.getSettings("kickEnabled").asBoolean())
                    if (SettingsHandler.getSettings("kickUsername").exists() &&
                            !SettingsHandler.getSettings("kickUsername").asString().trim().equals("")) {
                        new KickBot(SettingsHandler.getSettings("kickUsername").asString()).connect();
                }
            }
            catch (Exception e){
                e.printStackTrace();
                SettingsHandler.writeSettings("kickEnabled", "false");
            }
        }

        TwitchAccount.setInfo();
        ChannelPoints.refresh();
        YouTubeAccount.setInfo();
        Account.refreshYouTube(YouTubeAccount.name);
        Account.refreshKick(KickAccount.username, false);

        System.out.println("> Twitch Loaded");
        try {
            try {
                Language.loadLanguage();
                Language.startFileChangeListener();
            }
            catch (IllegalArgumentException e){
                System.out.println("> Language Change Listener Failed");
            }

            new Thread(Defaults::startMainThread).start();
            new Thread(streamDeckSocket = new ConnectorSocket()).start();

            System.out.println("> Main Threads Started");

            Window.loadSettings();


            System.out.println("> Window Settings Loaded");
            try {
                Window.initFrame();
            }
            catch (Exception e){
                e.printStackTrace();
            }

            System.out.println("> Window initialized");
            RequestsTab.createPanel();
            System.out.println("> Requests Tab created");
            ChatbotTab.createPanel();
            System.out.println("> Chatbot Tab created");
            SettingsTab.createPanel();
            System.out.println("> Settings Tab created");
            OfficerWindow.create();
            System.out.println("> Officers Window created");
            Window.loadTopComponent();
            System.out.println("> Top Tab shown");
            LoadCommands.loadCommands();
            System.out.println("> Commands Loaded");
            LoadTimers.loadTimers();
            System.out.println("> Timers Loaded");
            LoadPoints.loadPoints();
            System.out.println("> Channel Points Loaded");
            LoadKeywords.loadKeywords();
            System.out.println("> Keywords Loaded");
            LoadCheerActions.loadCheerActions();
            System.out.println("> Cheer Actions Loaded");
            LoggedID.loadLoggedIDs();
            System.out.println("> IDs Loaded");
            TimerHandler.startTimerHandler();
            System.out.println("> Timer Handler Started");

            LevelDetailsPanel.setPanel(null);

            Platform.setImplicitExit(false);

            System.out.println("> All Panels Created");

            UpdateChecker.checkForUpdates();
            System.out.println("> Started Update Checker");

            Defaults.initializeThemeInfo();
            System.out.println("> Theme Info Initialized");
            Themes.refreshUI();
            System.out.println("> UI Refreshed");
            if(Defaults.isAprilFools) {
                AprilFools.create();
                AprilFools.loadLevels();
            }
            starting.setVisible(false);

            System.out.println("> Launch Finished in " + (System.currentTimeMillis() - time) + "ms");

            //If first time launch, the user has to go through onboarding
            //Show it and wait until finished

            if (!SettingsHandler.getSettings("onboarding").exists()) {
                Onboarding.createPanel();
                Window.setVisible(true);
                System.out.println("> Window Visible");

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
                System.out.println("> Window Visible");
            }



            Variables.loadVars();
            System.out.println("> Command Variables Loaded");

            ServerBot.connect();


            if(SettingsHandler.getSettings("youtubeEnabled").asBoolean()){
                new Thread(YouTubeChatListener::startChatListener).start();
                System.out.println("> YouTube Chat Listener Started");
            }

            if(SettingsHandler.getSettings("twitchEnabled").asBoolean()) {
                new Thread(() -> {
                    chatReader = new TwitchChatListener(TwitchAccount.login);
                    chatReader.connect(SettingsHandler.getSettings("oauth").asString());
                    System.out.println("> Twitch Chat Listener Started");
                }).start();
                new Thread(() -> {
                    try {
                        channelPointListener = new TwitchListener(new URI("wss://pubsub-edge.twitch.tv"));
                        channelPointListener.connect();
                        System.out.println("> Channel Point Listener Started");
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }).start();
            }


            Window.setOnTop(SettingsHandler.getSettings("onTop").asBoolean());
            try {
                Outputs.setOutputStringFile(RequestsUtils.parseInfoString(SettingsHandler.getSettings("outputString").asString()));
            }
            catch (Exception e){
                SettingsHandler.writeSettings("outputFileLocation", Paths.get(Defaults.saveDirectory + "\\loquibot").toString());
                //OutputSettings.setOutputStringFile(RequestsUtils.parseInfoString(Settings.getSettings("outputString").asString(), 0));
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
            System.out.println("> Saved IDs Loaded");
            allowRequests = true;
            RequestFunctions.saveFunction();
            RequestsTab.getLevelsPanel().setSelect(0);
            //new Thread(TwitchAPI::checkViewers).start();

            sendMessages = true;
            if(SettingsHandler.getSettings("twitchEnabled").asBoolean()) {
                boolean loquiIsMod = TwitchAPI.isLoquiMod();
                SettingsHandler.writeSettings("isMod", String.valueOf(loquiIsMod));
            }
            programLoaded = true;

            startSaveLoop();
            System.out.println("> Save loop Started");

            /*if(!SettingsHandler.getSettings("dontShowDonate").asBoolean()) {
                new Thread(() -> {
                    String choice = DialogBox.showDialogBox("Help Out Alpha!", "Hosting loquibot costs me some money.", "Any donation is appreciated!", new String[]{"Donate", "No", "Don't Show"});
                    if (choice.equalsIgnoreCase("Donate")) {
                        try {
                            com.alphalaneous.Utils.Utilities.openURL(new URI("https://streamlabs.com/alphalaneous/tip"));
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (choice.equalsIgnoreCase("Don't Show")) {
                        SettingsHandler.writeSettings("dontShowDonate", "true");
                    }
                }).start();
            }*/
            if(!Defaults.isMac()) {
                Global.onEnterLevel(() -> {
                    if (SettingsHandler.getSettings("inGameNowPlaying").asBoolean()) {
                        if (MemoryHelper.isInFocus()) {
                            String levelName = com.alphalaneous.Memory.Level.getLevelName();
                            long levelID = com.alphalaneous.Memory.Level.getID();

                            if (lastID == levelID) {
                                return;
                            }

                            lastID = levelID;

                            String username = null;
                            int pos = Requests.getPosFromID(levelID);
                            if (pos != -1) {
                                username = RequestsTab.getRequest(pos).getLevelData().getDisplayName();
                            }

                            if (username != null) {
                                Main.sendYTMessage(com.alphalaneous.Utils.Utilities.format("🎮 | $NOW_PLAYING_MESSAGE$",
                                        levelName,
                                        levelID,
                                        username), null);
                                Main.sendKickMessage(com.alphalaneous.Utils.Utilities.format("🎮 | $NOW_PLAYING_MESSAGE$",
                                        levelName,
                                        levelID,
                                        username), null);
                                Main.sendMessage(com.alphalaneous.Utils.Utilities.format("🎮 | $NOW_PLAYING_MESSAGE$",
                                        levelName,
                                        levelID,
                                        username), SettingsHandler.getSettings("announceNP").asBoolean());
                            } else {
                                Main.sendYTMessage(com.alphalaneous.Utils.Utilities.format("🎮 | $NOW_PLAYING_MESSAGE_NO_USER$",
                                        levelName,
                                        levelID), null);
                                Main.sendKickMessage(com.alphalaneous.Utils.Utilities.format("🎮 | $NOW_PLAYING_MESSAGE_NO_USER$",
                                        levelName,
                                        levelID), null);
                                Main.sendMessage(com.alphalaneous.Utils.Utilities.format("🎮 | $NOW_PLAYING_MESSAGE_NO_USER$",
                                        levelName,
                                        levelID), SettingsHandler.getSettings("announceNP").asBoolean());
                            }
                        }
                    }
                });
            }
            Window.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            DialogBox.showDialogBox("Error!", e + ": " + e.getStackTrace()[0], "Please report to Alphalaneous#9687 on Discord.", new String[]{"Close"});
            close(true, false);
        }
    }

    static long lastID = 0;


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
            System.out.println("> Failed restart, closing...");
            e.printStackTrace();
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
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    public static void sendKickMessage(String messageA, String username){
        if(SettingsHandler.getSettings("kickEnabled").asBoolean()){
            String[] messages = messageA.split("¦");
            for(String message : messages) {

                if (!SettingsHandler.getSettings("silentMode").asBoolean() || message.equalsIgnoreCase(" ")) {
                    if (!message.equalsIgnoreCase("") && SettingsHandler.getSettings("kick_chat_token").exists()) {

                        JSONObject messageObj = new JSONObject();
                        messageObj.put("request_type", "kick_send_message");
                        messageObj.put("token", SettingsHandler.getSettings("kick_chat_token").asString());

                        if (SettingsHandler.getSettings("antiDox").asBoolean()) {
                            message = Language.modify(message.replaceAll(System.getProperty("user.name"), "*****"));
                        }

                        String pingStart = "";

                        if(username != null && !username.equalsIgnoreCase("")){
                            pingStart = com.alphalaneous.Utils.Utilities.format("@%s, ", username);
                        }

                        messageObj.put("message", pingStart + message);

                        if(KickAccount.chatroomID != 0) {
                            try {
                                ServerBot.sendMessage(messageObj.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
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
                    e1.printStackTrace();
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

    public static void save(){
        try {
            Window.setSettings();
            Variables.saveVars();
            SettingsHandler.saveSettings();
            Themes.saveTheme();
            CommandData.saveCustomCommands();
            CommandData.saveDefaultCommands();
            CommandData.saveGeometryDashCommands();
            //CommandData.saveMediaShareCommands();
            TimerData.saveCustomTimers();
            KeywordData.saveCustomKeywords();
            CheerActionData.saveCustomCheerActions();
            ChannelPointData.saveCustomPoints();
            LoggedID.saveLoggedIDs();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void startSaveLoop(){
        new Thread(() -> {
            while(true){
                save();
                Utilities.sleep(10000);
            }
        }).start();
    }

    public static void close(boolean forceLoaded, boolean load) {
        boolean loaded = Main.programLoaded;

        if (forceLoaded) {
            loaded = load;
        }
        Main.save();
        ServerBot.disconnect();
        ServerBot.reconnect = false;

        if(!SettingsHandler.getSettings("runAtStartup").asBoolean()) {
            com.alphalaneous.Utils.Utilities.disposeTray();
            if (!SettingsHandler.getSettings("onboarding").asBoolean() && loaded) {
                forceClose();
            }
            System.exit(0);
        }
        else {
            Window.setVisible(false);
        }
    }

    public static void forceClose(){
        Main.save();
        com.alphalaneous.Utils.Utilities.disposeTray();
        Window.setVisible(false);
        Window.setSettings();
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
            System.out.println("Failed closing properly, forcing close");
            e.printStackTrace();
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

    public static void close() {
        close(false, false);
    }
}
