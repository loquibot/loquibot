package com.alphalaneous.Services.GeometryDash;

import com.alphalaneous.*;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Settings.Logs.LoggedID;
import com.alphalaneous.Swing.BrowserWindow;
import com.alphalaneous.Swing.Components.*;
import com.alphalaneous.Settings.BlockedIDs;
import com.alphalaneous.Settings.Outputs;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Utils.Utilities;
import com.alphalaneous.Windows.AprilFools;
import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Tabs.RequestsTab;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class RequestFunctions {

    //todo make spamming the next button only show the last request in chat rather than the first. (Check if spamming, wait until stop and send current selected level)

    private static final LinkedHashMap<LevelButton, Integer> undoQueue = new LinkedHashMap<>(15) {
        protected boolean removeEldestEntry(Map.Entry<LevelButton, Integer> eldest) {
            return size() > 15;
        }
    };
    private static boolean didUndo = false;

    public static void openGDBrowser(int pos) {
        BrowserWindow browserWindow = new BrowserWindow("https://www.gdbrowser.com/" + RequestsTab.getRequest(pos).getLevelData().getGDLevel().getLevel().id());
    }

    public static void skipFunction() {
        skipFunction(LevelButton.selectedID);
    }

    public static void skipFunction(boolean setPos) {
        skipFunction(LevelButton.selectedID, setPos);

    }

    public static void skipFunction(int pos) {
        skipFunction(pos, true);
    }
    public static void skipFunction(int pos, boolean setPos) {

        boolean wasSelected;

        if (Main.programLoaded) {
            if (RequestsTab.getQueueSize() != 0) {
                if (didUndo) {
                    undoQueue.clear();
                    didUndo = false;
                }

                undoQueue.put(RequestsTab.getRequest(pos), pos);
                wasSelected = RequestsTab.getRequest(pos).selected;

                new LoggedID((int) RequestsTab.getRequest(pos).getID(), RequestsTab.getRequest(pos).getLevelData().getGDLevel().getLevel().levelVersion());
                RequestsTab.removeRequest(pos);

                if(setPos || wasSelected) {
                    if (RequestsTab.getQueueSize() <= pos) RequestsTab.setRequestSelect(RequestsTab.getQueueSize() - 1);
                    else RequestsTab.setRequestSelect(pos);
                }

                if (RequestsTab.getQueueSize() > 0) {
                    StringSelection selection = new StringSelection(
                            String.valueOf(RequestsTab.getRequest(0).getLevelData().getGDLevel().getLevel().id()));
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(selection, selection);
                }


                if (pos == 0 && RequestsTab.getQueueSize() > 0) {
                    if (!SettingsHandler.getSettings("disableNP").asBoolean() && !SettingsHandler.getSettings("inGameNowPlaying").asBoolean()) {
                        Main.sendYTMessage(Utilities.format("🎮 | $NOW_PLAYING_MESSAGE$",
                                RequestsTab.getRequest(0).getLevelData().getGDLevel().getLevel().name(),
                                RequestsTab.getRequest(0).getLevelData().getGDLevel().getLevel().id(),
                                RequestsTab.getRequest(0).getLevelData().getDisplayName()), null);
                        Main.sendMessage(Utilities.format("🎮 | $NOW_PLAYING_MESSAGE$",
                                RequestsTab.getRequest(0).getLevelData().getGDLevel().getLevel().name(),
                                RequestsTab.getRequest(0).getLevelData().getGDLevel().getLevel().id(),
                                RequestsTab.getRequest(0).getLevelData().getDisplayName()), SettingsHandler.getSettings("announceNP").asBoolean());
                    }
                }
            }
        }

        Outputs.setOutputStringFile(RequestsUtils.parseInfoString(SettingsHandler.getSettings("outputString").asString()));

        RequestsTab.getLevelsPanel().setWindowName(RequestsTab.getQueueSize());
        if(RequestsTab.getQueueSize() == 0) LevelDetailsPanel.setPanel(null);
        else LevelDetailsPanel.setPanel(RequestsTab.getRequest(RequestsUtils.getSelection()).getLevelData());

        RequestFunctions.saveFunction();
    }

    public static void undoFunction() {
        if (!undoQueue.isEmpty()) {
            didUndo = true;
            int selectPosition = LevelButton.selectedID;
            LevelButton levelButton = (LevelButton) undoQueue.keySet().toArray()[undoQueue.size() - 1];
            int position = (int) undoQueue.values().toArray()[undoQueue.size() - 1];
            if (position >= RequestsTab.getQueueSize()) {
                position = RequestsTab.getQueueSize();
            }
            RequestsTab.addRequest(levelButton, position);
            if (RequestsTab.getLevelPosition(levelButton) > selectPosition) {
                RequestsTab.getLevelsPanel().setSelect(selectPosition);
            } else if (RequestsTab.getQueueSize() == 1) {
                RequestsTab.getLevelsPanel().setSelect(selectPosition);
                LevelDetailsPanel.setPanel(RequestsTab.getRequest(selectPosition).getLevelData());

            } else {
                RequestsTab.getLevelsPanel().setSelect(selectPosition + 1);
            }
            undoQueue.remove(levelButton);
        }

        Outputs.setOutputStringFile(RequestsUtils.parseInfoString(SettingsHandler.getSettings("outputString").asString()));

        RequestFunctions.saveFunction();
    }

    public static int undoFunctionGetPos() {
        if (undoQueue.size() != 0) {
            didUndo = true;
            int selectPosition = LevelButton.selectedID;
            LevelButton levelButton = (LevelButton) undoQueue.keySet().toArray()[undoQueue.size() - 1];
            int position = (int) undoQueue.values().toArray()[undoQueue.size() - 1];
            if (position >= RequestsTab.getQueueSize()) {
                position = RequestsTab.getQueueSize();
            }
            RequestsTab.addRequest(levelButton, position);
            undoQueue.remove(levelButton);
            RequestFunctions.saveFunction();
            Outputs.setOutputStringFile(RequestsUtils.parseInfoString(SettingsHandler.getSettings("outputString").asString()));

            return position;
        }

        Outputs.setOutputStringFile(RequestsUtils.parseInfoString(SettingsHandler.getSettings("outputString").asString()));

        RequestFunctions.saveFunction();
        return LevelButton.selectedID;
    }


    public static void randomFunction() {
        if (Main.programLoaded) {
            Random random = new Random();
            int num;
            if (RequestsTab.getQueueSize() != 0) {
                if (didUndo) {
                    undoQueue.clear();
                    didUndo = false;
                }

                undoQueue.put(RequestsTab.getRequest(LevelButton.selectedID), LevelButton.selectedID);
                new LoggedID((int) RequestsTab.getRequest(LevelButton.selectedID).getID(), RequestsTab.getRequest(LevelButton.selectedID).getLevelData().getGDLevel().getLevel().levelVersion());
                RequestsTab.removeRequest(LevelButton.selectedID);

                RequestFunctions.saveFunction();


                if (RequestsTab.getQueueSize() != 0) {
                    while (true) {
                        try {
                            num = random.nextInt(RequestsTab.getQueueSize());
                            break;
                        } catch (Exception ignored) {
                        }
                    }

                    RequestsTab.getLevelsPanel().setSelect(num);


                    StringSelection selection = new StringSelection(
                            String.valueOf(RequestsTab.getRequest(num).getLevelData().getGDLevel().getLevel().id()));
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(selection, selection);

                    if (!SettingsHandler.getSettings("disableNP").asBoolean() && !SettingsHandler.getSettings("inGameNowPlaying").asBoolean()) {
                        Main.sendYTMessage(Utilities.format("🎮 | $NOW_PLAYING_MESSAGE$",
                                RequestsTab.getRequest(num).getLevelData().getGDLevel().getLevel().name(),
                                RequestsTab.getRequest(num).getLevelData().getGDLevel().getLevel().id(),
                                RequestsTab.getRequest(num).getLevelData().getDisplayName()), null);
                        Main.sendMessage(Utilities.format("🎮 | $NOW_PLAYING_MESSAGE$",
                                RequestsTab.getRequest(num).getLevelData().getGDLevel().getLevel().name(),
                                RequestsTab.getRequest(num).getLevelData().getGDLevel().getLevel().id(),
                                RequestsTab.getRequest(num).getLevelData().getDisplayName()), SettingsHandler.getSettings("announceNP").asBoolean());

                    }

                    Outputs.setOutputStringFile(RequestsUtils.parseInfoString(SettingsHandler.getSettings("outputString").asString()));
                    LevelDetailsPanel.setPanel(RequestsTab.getRequest(num).getLevelData());
                }
            }
            RequestFunctions.saveFunction();
            RequestsTab.getLevelsPanel().setWindowName(RequestsTab.getQueueSize());
        }
    }

    public static void copyFunction() {
        copyFunction(LevelButton.selectedID);
    }

    public static void copyFunction(int pos) {
        if (RequestsTab.getQueueSize() != 0) {
            StringSelection selection = new StringSelection(
                    String.valueOf(RequestsTab.getRequest(pos).getLevelData().getGDLevel().getLevel().id()));
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        }
    }

    public static void reportFunction(int pos){
        if (RequestsTab.getQueueSize() != 0) {
            //NSFW
            //CRASH
            //IDENTITY
            //HARASSMENT
            JPanel reportPanel = new JPanel();
            reportPanel.setLayout(null);
            reportPanel.setBounds(0,0,300,200);

            LangLabel reportLabel = new LangLabel("$REPORT$ " + RequestsTab.getRequest(pos).getLevelData().getGDLevel().getLevel().name());
            reportLabel.setBounds(10,0,200,40);
            reportLabel.setFont(Defaults.MAIN_FONT.deriveFont(20f));
            reportPanel.add(reportLabel);

            RadioPanel radioPanel = new RadioPanel("NSFW", "Crash", "Harassment");
            radioPanel.setBounds(10,40,200,150);

            reportPanel.add(radioPanel);
            DialogBox.showDialogBox(reportPanel);

        }
    }


    public static void saveFunction() {
        AprilFools.loadLevels();
        new Thread(() -> {
            try {
                Path file = Paths.get(Defaults.saveDirectory + "\\loquibot\\saved.json");
                if (!Files.exists(file)) {
                    Files.createFile(file);
                }

                JSONObject levels = new JSONObject();
                JSONArray levelsArray = new JSONArray();

                for (int i = 0; i < RequestsUtils.getSize(); i++) {

                    String creatorName = "Unknown";
                    String songTitle = "Unknown";
                    String songArtist = "Unknown";
                    long originalID = 0;
                    long songID = 0;

                    if (RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().creatorName().isPresent()) {
                        creatorName = RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().creatorName().get();
                    }
                    if (RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().song().isPresent()) {
                        songTitle = RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().song().get().title();
                        songArtist = RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().song().get().artist();
                        songID = RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().song().get().id();
                    }
                    if (RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().originalLevelId().isPresent()) {
                        originalID = RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().originalLevelId().get();
                    }

                    JSONObject level = new JSONObject();

                    level.put("creator_name", creatorName);
                    level.put("song_title", songTitle);
                    level.put("song_artist", songArtist);
                    level.put("original_id", originalID);
                    level.put("song_id", songID);
                    level.put("id", RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().id());
                    level.put("name", RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().name());
                    level.put("difficulty", RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().difficulty());
                    level.put("demon_difficulty", RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().demonDifficulty());
                    level.put("is_auto", RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().isAuto());
                    level.put("is_demon", RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().isDemon());
                    level.put("is_epic", RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().isEpic());
                    level.put("featured_score", RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().featuredScore());
                    level.put("stars", RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().stars());
                    level.put("requested_stars", RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().requestedStars());
                    level.put("requester", RequestsTab.getRequest(i).getRequester());
                    level.put("game_version", RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().gameVersion());
                    level.put("description", RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().description());
                    level.put("coin_count", RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().coinCount());
                    level.put("likes", RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().likes());
                    level.put("downloads", RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().downloads());
                    level.put("length", RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().length().toString());
                    level.put("level_version", RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().levelVersion());
                    level.put("object_count", RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().objectCount());
                    level.put("has_verified_coins", RequestsTab.getRequest(i).getLevelData().getGDLevel().getLevel().hasCoinsVerified());
                    level.put("is_youtube", RequestsTab.getRequest(i).getLevelData().isYouTube());
                    level.put("display_name", RequestsTab.getRequest(i).getLevelData().getDisplayName());
                    level.put("account_id", RequestsTab.getRequest(i).getLevelData().getGDLevel().getAccountID());
                    level.put("is_platformer", RequestsTab.getRequest(i).getLevelData().getGDLevel().getLength() == 5);

                    levelsArray.put(level);
                }

                levels.put("levels", levelsArray);

                Files.writeString(file, levels.toString(3), StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);

            } catch (IOException e) {
                Main.logger.error(e.getLocalizedMessage(), e);
            }
        }).start();
    }

    public static void blockFunction() {
        blockFunction(LevelButton.selectedID, false);
    }
    public static void blockFunction(int pos) {
        blockFunction(pos, false);
    }
    public static void blockFunction(boolean skip) {
        blockFunction(LevelButton.selectedID, skip);
    }

    public static void blockFunction(int pos, boolean skip) {
        if (Main.programLoaded) {
            if (pos == 0 && RequestsTab.getQueueSize() > 1) {
                StringSelection selection = new StringSelection(
                        String.valueOf(RequestsTab.getRequest(1).getLevelData().getGDLevel().getLevel().id()));
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
            }
            if (RequestsTab.getQueueSize() != 0) {

                new Thread(() -> {
                    String option;
                    if(skip) option = "YES";
                    else option = DialogBox.showDialogBox("$BLOCK_ID_TITLE$", "$BLOCK_ID_INFO$", "$BLOCK_ID_SUBINFO$", new String[]{"$YES$", "$NO$"}, new Object[]{RequestsTab.getRequest(pos).getLevelData().getGDLevel().getLevel().name(), RequestsTab.getRequest(pos).getLevelData().getGDLevel().getLevel().id()});

                    if (option.equalsIgnoreCase("YES")) {
                        BlockedIDs.addBlockedLevel(String.valueOf(RequestsTab.getRequest(pos).getLevelData().getGDLevel().getLevel().id()));
                        RequestsTab.removeRequest(pos);
                        RequestFunctions.saveFunction();
                        RequestsTab.getLevelsPanel().setSelect(0);
                        RequestsTab.getLevelsPanel().setWindowName(RequestsTab.getQueueSize());

                    }
                    LevelDetailsPanel.setPanel(RequestsTab.getRequest(pos).getLevelData());
                }).start();
            }
        }
    }

    public static void clearFunction() {
        clearFunction(false);
    }

    public static void clearFunction(boolean skip) {
        if (Main.programLoaded) {
            new Thread(() -> {
                String option;
                if(skip) option = "CLEAR_ALL";
                else option = DialogBox.showDialogBox("$CLEAR_QUEUE_TITLE$", "$CLEAR_QUEUE_INFO$", "$CLEAR_QUEUE_SUBINFO$", new String[]{"$CLEAR_ALL$", "$CANCEL$"});

                if (option.equalsIgnoreCase("CLEAR_ALL")) {
                    if (RequestsTab.getQueueSize() != 0) {
                        RequestsTab.clearRequests();
                        undoQueue.clear();
                        RequestFunctions.saveFunction();
                    }
                    RequestsTab.getLevelsPanel().setSelect(0);
                    LevelDetailsPanel.setPanel(null);
                }
            }).start();
        }
    }

    public static void requestsToggleFunction() {
        RequestsTab.toggle();
    }
}
