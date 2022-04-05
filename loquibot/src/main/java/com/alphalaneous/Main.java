package com.alphalaneous;

import com.alphalaneous.Panels.LevelDetailsPanel;
import com.alphalaneous.SettingsPanels.*;
import com.alphalaneous.Tabs.*;
import com.alphalaneous.Windows.*;
import com.alphalaneous.Windows.Window;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    static {
        BackwardsCompatibilityLayer.setNewLocation();
    }

    public static boolean programLoaded = false;
    public static boolean sendMessages = false;
    public static boolean allowRequests = false;
    public static Thread keyboardHookThread;

    private static TwitchListener channelPointListener;
    private static ChatListener chatReader;
    private static ServerBot serverBot = null;
    private static boolean keepConnecting = true;
    private static boolean failed = false;
    private static final ArrayList<Image> iconImages = new ArrayList<>();
    private static final ImageIcon icon = Assets.loquibot;
    private static final Image newIcon16 = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
    private static final Image newIcon32 = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
    private static final JFrame starting = new JFrame("loquibot");
    private static StreamDeckSocket streamDeckSocket;

    public static void main(String[] args) {

        iconImages.add(newIcon16);
        iconImages.add(newIcon32);

        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);
        LogWindow.createWindow();
        new Thread(Main::runKeyboardHook).start();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

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

        System.out.println("> Start");

        //Saves defaults of UI Elements before switching to Nimbus
        //Sets to Nimbus, then sets defaults back
        setUI();
        Settings.loadSettings();
        LoadGD.load();
        Themes.loadTheme();

        System.out.println("> Settings Loaded");

        if (Settings.getSettings("onboarding").exists()) {
            try {
                TwitchAccount.setInfo();
                new Thread(ChannelPointSettings::refresh).start();
            } catch (Exception e) {
                e.printStackTrace();
                APIs.setOauth();
                TwitchAccount.setInfo();
                new Thread(ChannelPointSettings::refresh).start();
            }
        }
        System.out.println("> Twitch Loaded");
        try {
            try {
                Language.loadLanguage();
                Language.startFileChangeListener();
            }
            catch (IllegalArgumentException e){
                System.out.println("> Language Change Listener Failed");
            }

            new Thread(Assets::loadAssets).start();
            new Thread(Defaults::initializeThemeInfo).start();
            new Thread(Defaults::startMainThread).start();
            new Thread(streamDeckSocket = new StreamDeckSocket()).start();

            System.out.println("> Main Threads Started");

            Window.initFrame();
            CommandEditor.createPanel();
            RequestsTab.createPanel();
            ChatbotTab.createPanel();
            SettingsTab.createPanel();
            OfficerWindow.create();
            Window.loadTopComponent();
            LoadCommands.loadCommands();
            LoadTimers.loadTimers();
            LoggedID.loadLoggedIDs();
            TimerHandler.startTimerHandler();

            LevelDetailsPanel.setPanel(null);


            System.out.println("> Panels Created");

            Window.loadSettings();
            starting.setVisible(false);

            //If first time launch, the user has to go through onboarding
            //Show it and wait until finished

            Themes.refreshUI();

            if (!Settings.getSettings("onboarding").exists()) {
                Onboarding.createPanel();
                Window.setVisible(true);
                System.out.println("> Window Visible");

                Onboarding.refreshUI();
                Onboarding.isLoading = true;
                while (Onboarding.isLoading) {
                    Utilities.sleep(100);
                }
                TwitchAccount.setInfo();
                new Thread(ChannelPointSettings::refresh).start();
            }
            else {
                Window.setVisible(true);
                System.out.println("> Window Visible");
            }

            new Thread(Variables::loadVars).start();
            System.out.println("> Command Variables Loaded");

            new Thread(() -> {
                while (keepConnecting) {
                    try {
                        if (chatReader != null) {
                            try {
                                chatReader.disconnect();
                            } catch (WebsocketNotConnectedException ignored) {
                            }
                        }
                        chatReader = new ChatListener(TwitchAccount.login);
                        chatReader.connect(Settings.getSettings("oauth").asString(), TwitchAccount.login);
                        while (!chatReader.isClosed()) {
                            Utilities.sleep(100);
                        }
                    } catch (Exception ignored) {
                    }
                    Utilities.sleep(1000);
                }
            }).start();
            new Thread(() -> {
                while (keepConnecting) {
                    serverBot = new ServerBot();
                    serverBot.connect();
                    Utilities.sleep(1000);
                }
            }).start();


            //Reads channel point redemptions for channel point triggers

            new Thread(() -> {
                try {
                    while (true) {
                        channelPointListener = new TwitchListener(new URI("wss://pubsub-edge.twitch.tv"));
                        channelPointListener.connectBlocking();
                        while (channelPointListener.isOpen()) {
                            Utilities.sleep(10);
                        }
                        Utilities.sleep(2000);
                    }
                } catch (URISyntaxException | InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();



            Window.setOnTop(Settings.getSettings("onTop").asBoolean());
            try {
                OutputSettings.setOutputStringFile(RequestsUtils.parseInfoString(Settings.getSettings("outputString").asString(), 0));
            }
            catch (Exception e){
                Settings.writeSettings("outputFileLocation", Paths.get(Defaults.saveDirectory + "\\loquibot").toString());
                //OutputSettings.setOutputStringFile(RequestsUtils.parseInfoString(Settings.getSettings("outputString").asString(), 0));
            }
            Path initialJS = Paths.get(Defaults.saveDirectory + "\\loquibot\\initial.js");
            if (Files.exists(initialJS)) {
                new Thread(() -> {
                    try {
                        if (!Files.readString(initialJS, StandardCharsets.UTF_8).equalsIgnoreCase("")) {
                            Command.run(TwitchAccount.display_name, true, true, new String[]{"dummy"}, Files.readString(initialJS, StandardCharsets.UTF_8), 0, null, -1);
                        }
                    } catch (Exception ignored) {
                    }
                }).start();
            } else {
                Files.createFile(initialJS);
            }

            Path file = Paths.get(Defaults.saveDirectory + "\\loquibot\\saved.json");
            if (Files.exists(file)) {
                String levelsJson = Files.readString(file, StandardCharsets.UTF_8);
                JSONObject object = new JSONObject(levelsJson);
                JSONArray levelsArray = object.getJSONArray("levels");
                for(Object object1 : levelsArray){
                    JSONObject level = (JSONObject) object1;
                    RequestsUtils.forceAdd(
                            level.getString("name"),
                            level.getString("creator_name"),
                            level.getLong("id"),
                            level.getString("difficulty"),
                            level.getString("demon_difficulty"),
                            level.getBoolean("is_demon"),
                            level.getBoolean("is_auto"),
                            level.getBoolean("is_epic"),
                            level.getInt("featured_score"),
                            level.getInt("stars"),
                            level.getInt("requested_stars"),
                            level.getString("requester"),
                            level.getInt("game_version"),
                            level.getInt("coin_count"),
                            level.getString("description"),
                            level.getInt("likes"),
                            level.getInt("downloads"),
                            level.getString("length"),
                            level.getInt("level_version"),
                            level.getLong("song_id"),
                            level.getString("song_title"),
                            level.getString("song_artist"),
                            level.getInt("object_count"),
                            level.getLong("original_id"),
                            false, false,
                            level.getBoolean("has_verified_coins")
                    );
                }
            }
            allowRequests = true;
            RequestFunctions.saveFunction();
            RequestsTab.getLevelsPanel().setSelect(0);
            new Thread(APIs::checkViewers).start();

            sendMessages = true;
            APIs.setAllViewers();

            Settings.writeSettings("isMod", String.valueOf(APIs.isLoquiMod()));

            if(!Settings.getSettings("isHigher").exists()) {
                if (APIs.allMods.contains("loquibot") || APIs.allVIPs.contains("loquibot")) Settings.writeSettings("isHigher", "true");
                else Settings.writeSettings("isHigher", "false");
            }
            System.gc();
            programLoaded = true;

        } catch (Exception e) {
            e.printStackTrace();
            DialogBox.showDialogBox("Error!", e + ": " + e.getStackTrace()[0], "Please report to Alphalaneous#9687 on Discord.", new String[]{"Close"});
            close(true, false);
        }
    }

    public static void sendMessageToStreamDeck(String message){
        if(streamDeckSocket != null) {
            streamDeckSocket.sendMessage(message);
        }
    }

    private static void updateTree(){
        ComponentTree.updateTree(Window.getWindow().getRootPane());
    }

    public static ArrayList<Image> getIconImages() {
        return iconImages;
    }

    static void refreshBot() {
        try {
            serverBot.connect();
            if (channelPointListener != null) {
                channelPointListener.disconnectBot();
            }
            channelPointListener = new TwitchListener(new URI("wss://pubsub-edge.twitch.tv"));
            channelPointListener.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static void sendMainMessage(String message) {

        chatReader.sendMessage(message);
    }

    public static void sendToServer(String message) {
        try {
            serverBot.sendMessage(message);
        } catch (Exception ignored) {
        }
    }

    public static void sendMessageWithoutCooldown(String message){
        JSONObject messageObj = new JSONObject();
        messageObj.put("request_type", "send_message");
        messageObj.put("message", message);
        serverBot.sendMessage(messageObj.toString());
    }

    static void sendMessage(String message, boolean whisper, String user) {
        if (!Settings.getSettings("silentMode").asBoolean() || message.equalsIgnoreCase(" ")) {
            if (!message.equalsIgnoreCase("")) {

                JSONObject messageObj = new JSONObject();
                messageObj.put("request_type", "send_message");
                if (Settings.getSettings("antiDox").asBoolean()) {
                    message = Language.uwuify(message.replaceAll(System.getProperty("user.name"), "*****"));
                }
                if (whisper) {
                    messageObj.put("message", "/w " + user + " " + message);
                } else {
                    messageObj.put("message", message);
                }
                try {
                    serverBot.sendMessage(messageObj.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (whisper) {
            if (!message.equalsIgnoreCase("")) {
                JSONObject messageObj = new JSONObject();
                messageObj.put("request_type", "send_message");
                if (Settings.getSettings("antiDox").asBoolean()) {
                    message = message.replaceAll(System.getProperty("user.name"), "*****");
                }
                messageObj.put("message", "/w " + user + " " + message);
                try {
                    serverBot.sendMessage(messageObj.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void sendMessage(String message) {
        sendMessage(message, false, null);
    }
    public static void sendMessage(String message, boolean doAnnounce) {
        Settings.writeSettings("isMod", String.valueOf(APIs.isLoquiMod()));

        if(doAnnounce && Settings.getSettings("isMod").asBoolean() && !message.startsWith("/")) sendMessage("/announce " + message, false, null);
        else sendMessage(message, false, null);
    }

    private static void runKeyboardHook() {
        var runHookRef = new Object() {
            boolean runHook = true;
        };
        if (keyboardHookThread != null) {
            if (keyboardHookThread.isAlive()) {
                runHookRef.runHook = false;
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
        } catch (Exception e) {
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException e1) {
                e1.printStackTrace();
            }
            failed = true;
        }
        keyboardHookThread = new Thread(() -> {
            while (runHookRef.runHook) {
                if (failed) {
                    runKeyboardHook();
                }
                Utilities.sleep(100);
            }
        });
        keyboardHookThread.start();
    }

    public static void close(boolean forceLoaded, boolean load) {
        boolean loaded = Main.programLoaded;

        if (forceLoaded) {
            loaded = load;
        }
        new Thread(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Variables.saveVars();
            Settings.saveSettings();
            Themes.saveTheme();
            CommandData.saveCustomCommands();
            CommandData.saveDefaultCommands();
            CommandData.saveGeometryDashCommands();
            TimerData.saveCustomTimers();
            LoggedID.saveLoggedIDs();
            System.exit(0);
        }).start();
        Utilities.disposeTray();
        if (!Settings.getSettings("onboarding").asBoolean() && loaded) {
            Window.setVisible(false);
            Window.setSettings();
            keepConnecting = false;
            try {
                channelPointListener.disconnectBot();
                chatReader.disconnect();
                serverBot.disconnect();
                GlobalScreen.unregisterNativeHook();
            } catch (Exception ignored) {
            }
            Variables.saveVars();
            Themes.saveTheme();
            Settings.saveSettings();
            CommandData.saveCustomCommands();
            CommandData.saveDefaultCommands();
            CommandData.saveGeometryDashCommands();
            TimerData.saveCustomTimers();
            LoggedID.saveLoggedIDs();
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
