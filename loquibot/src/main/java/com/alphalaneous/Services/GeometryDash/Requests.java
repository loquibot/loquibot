package com.alphalaneous.Services.GeometryDash;

import com.alphalaneous.*;
import com.alphalaneous.Moderation.Moderation;
import com.alphalaneous.Services.Twitch.TwitchAPI;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Swing.Components.LevelButton;
import com.alphalaneous.Settings.Outputs;
import com.alphalaneous.Settings.Filters;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.Services.Twitch.TwitchAccount;
import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Utils.Utilities;
import com.vdurmont.emoji.EmojiManager;
import jdash.client.exception.ActionFailedException;
import jdash.client.exception.GDClientException;
import jdash.client.exception.HttpResponseException;
import jdash.client.exception.ResponseDeserializationException;
import jdash.common.entity.GDSong;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Requests {

    public static volatile boolean requestsEnabled = true;
    public static HashMap<Long, String> globallyBlockedIDs = new HashMap<>();
    public static HashMap<Long, String> globallyBlockedUsers = new HashMap<>();

    public static List<JSONObject> reportedIDs = Collections.synchronizedList(new ArrayList<>());

    private static final HashMap<Long, Integer> addedLevels = new HashMap<>();
    private static final HashMap<String, Integer> userStreamLimitMap = new HashMap<>();
    private static final Path logged = Paths.get(Defaults.saveDirectory + "\\loquibot\\requestsLog.txt");

    private static int totalLevelsSent = 0;

    public static void incrementSent(){
        totalLevelsSent++;
    }

    public static void decrementSent(){
        totalLevelsSent--;
    }

    public static void addRequest(long IDa, String user, boolean isMod, boolean isSub, String message, String messageID, long userID, boolean isCommand, ChatMessage chatMessage){
        addRequest(IDa, user, isMod, isSub, message, messageID, userID, isCommand, chatMessage, -1);
    }
    public static void addRequest(long IDa, String user, boolean isMod, boolean isSub, String message, String messageID, long userID, boolean isCommand, ChatMessage chatMessage, int pos) {


        if(!com.alphalaneous.Windows.Window.getWindow().isVisible()) return;
        if(chatMessage == null) {
            chatMessage = new ChatMessage(new String[]{}, user, user, message, new String[]{}, isMod, isSub, false, 0, false, false);
        }
        if (chatMessage.getTag("user-id") != null && !chatMessage.isYouTube() && !chatMessage.isKick() && globallyBlockedUsers.containsKey(Long.parseLong(chatMessage.getTag("user-id")))) {
            return;
        }
        if (globallyBlockedIDs.containsKey(IDa)) {
            sendUnallowed(Utilities.format("$GLOBALLY_BLOCKED_LEVEL_MESSAGE$", globallyBlockedIDs.get(IDa)), messageID, chatMessage.isYouTube(), chatMessage.isKick(), chatMessage.getSenderElseDisplay());
            return;
        }
        if(chatMessage.isYouTube() || chatMessage.isKick()){
            user = chatMessage.getDisplayName();
        }
        if(IDa != 0) System.out.println("> Adding Request: "+ IDa);
        else System.out.println("> Adding Request: "+ message);
        ChatMessage finalChatMessage = chatMessage;
        String finalUser = user;
        new Thread(() -> {
            try {
                if(!finalChatMessage.isYouTube() && !finalChatMessage.isKick() && Moderation.checkIfLink(message)){
                    return;
                }
                if (checkList(finalChatMessage.getSender(), "\\loquibot\\blockedUsers.txt")) {
                    return;
                }
                if (!Main.allowRequests) {
                    return;
                }
                if (!Main.programLoaded) {
                    return;
                }
                if (!requestsEnabled && pos == -1) {
                    sendUnallowed(Utilities.format("$REQUESTS_OFF_MESSAGE$"), messageID, finalChatMessage.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                    return;
                }

                GDLevelExtra level;

                ArrayList<String> arguments = new ArrayList<>();
                arguments.add(""); //Accidentally started array at one due to value I thought existed, easier to add dummy value than change everything.
                Matcher argMatcher = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(message);
                while (argMatcher.find()) {
                    arguments.add(argMatcher.group(1).toLowerCase().trim());
                }
                String levelNameS; //Starting level name for search
                String usernameS = ""; //starting username for search
                if (isCommand) {
                    Matcher IDMatcher = Pattern.compile("(\\d+)").matcher(arguments.get(1));
                    if (IDMatcher.matches() && arguments.size() <= 2) {
                        long ID = Long.parseLong(IDMatcher.group(1));
                        level = checkLevelIDAndGetLevel(ID);
                        if (level == null) return;
                    } else if (arguments.size() > 2) {
                        boolean inQuotes = false;
                        if (arguments.get(2).equalsIgnoreCase("by") || message.toLowerCase().contains(" by ")) {
                            if (arguments.get(2).equalsIgnoreCase("by")) {
                                levelNameS = arguments.get(1).trim();
                                if (levelNameS.startsWith("\"") && levelNameS.endsWith("\"")) {
                                    inQuotes = true;
                                }
                                levelNameS = levelNameS.replace("\"", "");
                                usernameS = arguments.get(3).trim().replace("\"", "");
                            } else if (message.toLowerCase().contains(" by ")) {
                                String messageS = message.split(" ", 2)[1];
                                String[] argumentsS = messageS.split(" by ", 2);

                                levelNameS = argumentsS[0];
                                if (levelNameS.startsWith("\"") && levelNameS.endsWith("\"")) {
                                    inQuotes = true;
                                }
                                levelNameS = levelNameS.replace("\"", "");

                                if(argumentsS.length >= 2) {
                                    usernameS = argumentsS[1].trim().replace("\"", "");
                                }
                            } else {
                                sendUnallowed(Utilities.format("$LEVEL_COMMAND_FORMAT_MESSAGE$"), messageID, finalChatMessage.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                                return;
                            }

                            level = GDAPI.getLevelByNameByUser(levelNameS, usernameS, inQuotes);

                            if (level == null) {
                                sendUnallowed(Utilities.format("$LEVEL_USER_DOESNT_EXIST_MESSAGE$"), messageID, finalChatMessage.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                                return;
                            }
                        } else {
                            level = getID(message, finalChatMessage.isYouTube(), finalChatMessage.isKick(), messageID, finalChatMessage.getSenderElseDisplay());
                            if (level == null) return;
                        }
                    } else {
                        level = getID(message, finalChatMessage.isYouTube(), finalChatMessage.isKick(), messageID, finalChatMessage.getSenderElseDisplay());
                        if (level == null) return;
                    }
                } else {
                    level = checkLevelIDAndGetLevel(IDa);
                    if (level == null) return;
                }

                for (int k = 0; k < RequestsTab.getQueueSize(); k++) {

                    if (level.getLevel().id() == RequestsTab.getRequest(k).getLevelData().getGDLevel().getLevel().id()) {
                        int j = k + 1;

                        if(SettingsHandler.getSettings("autoDeleteRepeatSend").asBoolean()){
                            Main.sendMessage("/delete " + finalChatMessage.getTag("id"));
                        }
                        if(!SettingsHandler.getSettings("disableInQueueMessage").asBoolean()) {
                            if (!SettingsHandler.getSettings("disableShowPosition").asBoolean()) {
                                sendUnallowed(Utilities.format("$ALREADY_IN_QUEUE_MESSAGE$", j), messageID, finalChatMessage.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                            } else {
                                sendUnallowed(Utilities.format("$ALREADY_IN_QUEUE_MESSAGE_ALT$"), messageID, finalChatMessage.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                            }
                        }
                        return;
                    }
                }
                boolean bypass = false;
                if(SettingsHandler.getSettings("twitchEnabled").asBoolean()){
                    bypass = (SettingsHandler.getSettings("modsBypass").asBoolean() && isMod)
                            || (finalUser.equalsIgnoreCase(TwitchAccount.login) && SettingsHandler.getSettings("streamerBypass").asBoolean()
                            || finalChatMessage.getUserLevel().equals("admin"));
                }
                if (!bypass) {
                    if (checkList(level.getLevel().id(), "\\loquibot\\blocked.txt")) {
                        sendUnallowed(Utilities.format("$BLOCKED_LEVEL_MESSAGE$"), messageID, finalChatMessage.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                        return;
                    }
                    if (Files.exists(logged) && (SettingsHandler.getSettings("repeatedRequestsAll").asBoolean() && !SettingsHandler.getSettings("updatedRepeated").asBoolean()) && Main.programLoaded) {
                        Scanner sc = new Scanner(logged.toFile());
                        while (sc.hasNextLine()) {
                            if (String.valueOf(level.getLevel().id()).equals(sc.nextLine().split(",")[0])) {
                                sc.close();
                                sendUnallowed(Utilities.format("$REQUESTED_BEFORE_MESSAGE$"), messageID, finalChatMessage.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                                return;
                            }
                        }
                        sc.close();
                    }

                    if (globallyBlockedIDs.containsKey(level.getLevel().id())) {
                        sendUnallowed(Utilities.format("$GLOBALLY_BLOCKED_LEVEL_MESSAGE$", globallyBlockedIDs.get(level.getLevel().id())), messageID, finalChatMessage.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                        return;
                    }
                    if (SettingsHandler.getSettings("subscribers").asBoolean()) {
                        if(!finalChatMessage.isYouTube() && !finalChatMessage.isKick()) {
                            if (!(isSub || isMod)) {
                                sendUnallowed(Utilities.format("$REQUESTS_SUBSCRIBE_MESSAGE$"), messageID, finalChatMessage.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                                return;
                            }
                        }
                    }
                    if (SettingsHandler.getSettings("followers").asBoolean()) {
                        if(!finalChatMessage.isYouTube() && !finalChatMessage.isKick()) {
                            if (TwitchAPI.isNotFollowing(finalUser, userID)) {
                                sendUnallowed(Utilities.format("$FOLLOW_MESSAGE$"), messageID, finalChatMessage.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                                return;
                            }
                        }
                    }
                    if (level.getLevel().id() < SettingsHandler.getSettings("minID").asInteger() && SettingsHandler.getSettings("minIDOption").asBoolean()) {
                        sendUnallowed(Utilities.format("$MIN_ID_MESSAGE$", SettingsHandler.getSettings("minID").asInteger()), messageID, finalChatMessage.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                        return;
                    }
                    if (level.getLevel().id() > SettingsHandler.getSettings("maxID").asInteger() && SettingsHandler.getSettings("maxIDOption").asBoolean()) {
                        sendUnallowed(Utilities.format("$MAX_ID_MESSAGE$", SettingsHandler.getSettings("maxID").asInteger()), messageID, finalChatMessage.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                        return;
                    }
                    if (SettingsHandler.getSettings("queueLimitEnabled").asBoolean() && (RequestsTab.getQueueSize() >= SettingsHandler.getSettings("queueLimit").asInteger())) {
                        if (!SettingsHandler.getSettings("disableQF").asBoolean()) {
                            sendUnallowed(Utilities.format("$QUEUE_FULL_MESSAGE$"), messageID, finalChatMessage.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                        }
                        return;
                    }
                    if (SettingsHandler.getSettings("levelLimitEnabled").asBoolean() && (totalLevelsSent >= SettingsHandler.getSettings("levelLimit").asInteger())) {

                        sendUnallowed(Utilities.format("$MAXIMUM_LEVELS_TOTAL_MESSAGE$"), messageID, finalChatMessage.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                        return;
                    }

                    if (SettingsHandler.getSettings("userLimitEnabled").asBoolean()) {
                        int size = 0;
                        for (int i = 0; i < RequestsTab.getQueueSize(); i++) {
                            if (RequestsTab.getRequest(i).getLevelData().getRequester().equalsIgnoreCase(finalChatMessage.getSender())) {
                                size++;
                            }
                        }
                        if (size >= SettingsHandler.getSettings("userLimit").asInteger()) {
                            sendUnallowed(Utilities.format("$MAXIMUM_LEVELS_MESSAGE$"), messageID, finalChatMessage.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                            return;
                        }
                    }
                    if (SettingsHandler.getSettings("userLimitStreamEnabled").asBoolean()) {
                        if (userStreamLimitMap.containsKey(finalChatMessage.getSender())) {
                            if (userStreamLimitMap.get(finalChatMessage.getSender()) >= SettingsHandler.getSettings("userLimitStream").asInteger()) {
                                sendUnallowed(Utilities.format("$MAXIMUM_LEVELS_STREAM_MESSAGE$"), messageID, finalChatMessage.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                                return;
                            }
                        }
                    }
                    if (addedLevels.containsKey(level.getLevel().id()) && (SettingsHandler.getSettings("repeatedRequests").asBoolean() && !SettingsHandler.getSettings("updatedRepeated").asBoolean())) {
                        sendUnallowed(Utilities.format("$REQUESTED_BEFORE_MESSAGE$"), messageID, finalChatMessage.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                        return;
                    }
                }


                LevelData levelData = new LevelData();
                levelData.setYouTube(finalChatMessage.isYouTube());
                levelData.setKick(finalChatMessage.isKick());
                if (levelData.isYouTube() || levelData.isKick()) levelData.setDisplayName(finalChatMessage.getDisplayName());
                else levelData.setDisplayName(finalChatMessage.getSender());

                if (!bypass) {
                    if(level.getLevel().creatorName().isPresent()) {
                        if (checkList(level.getLevel().creatorName().get(), "\\loquibot\\blockedGDUsers.txt")) {
                            sendUnallowed(Utilities.format("$BLOCKED_CREATOR_MESSAGE$"), messageID, levelData.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                            return;
                        }
                    }
                    /*if (SettingsHandler.getSettings("twoPlayerOnly").asBoolean() && level. ) {
                        sendUnallowed(Utilities.format("$STAR_RATED_MESSAGE$", finalUser), levelData.isYouTube());
                        return;
                    }*/
                    if (SettingsHandler.getSettings("rated").asBoolean() && !(level.getLevel().stars() > 0)) {
                        sendUnallowed(Utilities.format("$STAR_RATED_MESSAGE$"), messageID, levelData.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                        return;
                    }
                    if (SettingsHandler.getSettings("unrated").asBoolean() && level.getLevel().stars() > 0) {
                        sendUnallowed(Utilities.format("$UNRATED_MESSAGE$"), messageID, levelData.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                        return;
                    }
                    if (SettingsHandler.getSettings("minObjectsOption").asBoolean() && level.getLevel().objectCount() < SettingsHandler.getSettings("minObjects").asInteger()) {
                        sendUnallowed(Utilities.format("$FEW_OBJECTS_MESSAGE$"), messageID,levelData.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                        return;
                    }
                    if (SettingsHandler.getSettings("maxObjectsOption").asBoolean() && level.getLevel().objectCount() > SettingsHandler.getSettings("maxObjects").asInteger()) {
                        sendUnallowed(Utilities.format("$MANY_OBJECTS_MESSAGE$"), messageID,levelData.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                        return;
                    }
                    if (level.getLevel().objectCount() != 0) {
                        if (SettingsHandler.getSettings("minLikesOption").asBoolean() && level.getLevel().objectCount() < SettingsHandler.getSettings("minLikes").asInteger()) {
                            sendUnallowed(Utilities.format("$FEW_LIKES_MESSAGE$"), messageID,levelData.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                            return;
                        }
                        if (SettingsHandler.getSettings("maxObjectsOption").asBoolean() && level.getLevel().objectCount() > SettingsHandler.getSettings("maxLikes").asInteger()) {
                            sendUnallowed(Utilities.format("$MANY_LIKES_MESSAGE$"), messageID,levelData.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                            return;
                        }
                    }
                }
                if (messageID != null) {
                    levelData.setMessageID(messageID);
                }

                if (level.getLevel().featuredScore() > 0) {
                    levelData.setFeatured();
                }

                if(Files.exists(logged) && SettingsHandler.getSettings("showRepeatedRequestsAll").asBoolean() && Main.programLoaded){
                    Scanner sc = new Scanner(logged.toFile());
                    while (sc.hasNextLine()) {
                        if (String.valueOf(level.getLevel().id()).equals(sc.nextLine().split(",")[0])) {
                            levelData.setRepeated(true);
                            sc.close();
                            break;
                        }
                    }
                    sc.close();
                }

                levelData.setLevelData(level);
                if (!bypass) {
                    if (Files.exists(logged) && SettingsHandler.getSettings("updatedRepeated").asBoolean()) {
                        Scanner sc = new Scanner(logged.toFile());
                        while (sc.hasNextLine()) {
                            String levelLine = sc.nextLine();
                            if (String.valueOf(level.getLevel().id()).equals(levelLine.split(",")[0])) {
                                int version;
                                if (levelLine.split(",").length == 1) {
                                    version = 1;
                                } else {
                                    version = Integer.parseInt(levelLine.split(",")[1]);
                                }
                                if (version >= levelData.getGDLevel().getLevel().levelVersion()) {
                                    sc.close();
                                    sendUnallowed(Utilities.format("$REQUESTED_BEFORE_MESSAGE$"), messageID,levelData.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                                    return;
                                }
                            }
                        }
                        sc.close();
                    }
                    if (addedLevels.containsKey(level.getLevel().id()) && (SettingsHandler.getSettings("updatedRepeated").asBoolean())) {
                        if (addedLevels.get(level.getLevel().id()) >= levelData.getGDLevel().getLevel().levelVersion()) {
                            sendUnallowed(Utilities.format("$REQUESTED_BEFORE_MESSAGE$"), messageID,levelData.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                            return;
                        }
                    }
                    if (Filters.excludedDifficulties.contains(levelData.getSimpleDifficulty().toLowerCase()) && SettingsHandler.getSettings("disableDifficulties").asBoolean()) {
                        sendUnallowed(Utilities.format("$DIFFICULTY_MESSAGE$"), messageID,levelData.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                        return;
                    }
                    if (Filters.excludedRequestedDifficulties.contains(starToDifficulty(levelData.getGDLevel().getLevel().requestedStars())) && SettingsHandler.getSettings("disableReqDifficulties").asBoolean()) {
                        sendUnallowed(Utilities.format("$REQ_DIFFICULTY_MESSAGE$"), messageID,levelData.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                        return;
                    }
                    if (Filters.excludedLengths.contains(level.getLevel().length().name().toLowerCase()) && SettingsHandler.getSettings("disableLengths").asBoolean()) {
                        sendUnallowed(Utilities.format("$LENGTH_MESSAGE$"), messageID,levelData.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                        return;
                    }
                }

                if (SettingsHandler.getSettings("autoDL").asBoolean()) {
                    new Thread(() -> {
                        Optional<GDSong> song = levelData.getGDLevel().getLevel().song();
                        if (song.isPresent()) {
                            Path songFile = Paths.get(System.getenv("LOCALAPPDATA") + "\\GeometryDash\\" + song.get().id() + ".mp3");
                            if (!Files.exists(songFile)) {
                                try {
                                    Optional<String> songDL = song.get().downloadUrl();
                                    if (songDL.isPresent()) {
                                        FileUtils.copyURLToFile(new URL(songDL.get()), songFile.toFile());
                                    }
                                } catch (IOException ignored) {
                                }
                            }
                        }
                    }).start();
                }
                if (userStreamLimitMap.containsKey(finalChatMessage.getSender())) {
                    userStreamLimitMap.put(finalChatMessage.getSender(), userStreamLimitMap.get(finalChatMessage.getSender()) + 1);
                } else {
                    userStreamLimitMap.put(finalChatMessage.getSender(), 1);
                }
                if(levelData.isYouTube()){
                    levelData.setRequester(finalChatMessage.getSender());
                }
                else{
                    levelData.setRequester(finalUser);
                }

                levelData.setMessage(message);
                levelData.setMessageID(messageID);

                Optional<String> creatorName = levelData.getGDLevel().getLevel().creatorName();

                try {
                    creatorName.ifPresent(s -> levelData.setPlayerIcon(GDAPI.getIcon(GDAPI.getGDUserProfile(s), 100)));
                }
                catch (Exception ignored){
                    //if green user, don't fail
                }
                LevelButton levelButton = new LevelButton(levelData);

                if(pos != -1 && RequestsTab.getQueueSize() != 0) {
                    RequestsTab.addRequest(levelButton, pos);
                }
                else {
                    RequestsTab.addRequest(levelButton);
                }

                try {
                    RequestsTab.getLevelsPanel().setSelect(LevelButton.selectedID, RequestsTab.getQueueSize() == 1);
                } catch (Exception ignored) {
                }

                incrementSent();

                RequestsTab.getLevelsPanel().setWindowName(RequestsTab.getQueueSize());
                RequestFunctions.saveFunction();

                try {
                    Outputs.setOutputStringFile(RequestsUtils.parseInfoString(SettingsHandler.getSettings("outputString").asString()));
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                if (Main.sendMessages && !SettingsHandler.getSettings("disableConfirm").asBoolean() && RequestsTab.getQueueSize() != 1) {
                    if(pos == -1) {
                        if (!SettingsHandler.getSettings("disableShowPosition").asBoolean()) {
                            sendSuccess(Utilities.format("$CONFIRMATION_MESSAGE$",
                                    level.getLevel().name(),
                                    level.getLevel().id(),
                                    RequestsTab.getQueueSize()), messageID, levelData.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                        } else {
                            sendSuccess(Utilities.format("$CONFIRMATION_MESSAGE_ALT$",
                                    level.getLevel().name(),
                                    level.getLevel().id()), messageID, levelData.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                        }
                    }
                    else{
                        sendSuccess(Utilities.format("$CONFIRMATION_MESSAGE_INSTANT$",
                                level.getLevel().name(),
                                level.getLevel().id(),
                                RequestsTab.getQueueSize()), messageID, levelData.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());

                    }
                }
                if (Main.sendMessages && !SettingsHandler.getSettings("disableConfirm").asBoolean() && RequestsTab.getQueueSize() == 1) {
                    StringSelection selection = new StringSelection(String.valueOf(RequestsTab.getRequest(0).getLevelData().getGDLevel().getLevel().id()));
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(selection, selection);

                    if (Main.sendMessages && !SettingsHandler.getSettings("disableNP").asBoolean() && !SettingsHandler.getSettings("inGameNowPlaying").asBoolean()) {
                        Main.sendMessage(Utilities.format("🎮 | $NOW_PLAYING_TOP_MESSAGE$",
                                RequestsTab.getRequest(0).getLevelData().getGDLevel().getLevel().name(),
                                RequestsTab.getRequest(0).getLevelData().getGDLevel().getLevel().id()), SettingsHandler.getSettings("announceNP").asBoolean());
                        Main.sendKickMessage(Utilities.format("🎮 | $NOW_PLAYING_TOP_MESSAGE$",
                                RequestsTab.getRequest(0).getLevelData().getGDLevel().getLevel().name(),
                                RequestsTab.getRequest(0).getLevelData().getGDLevel().getLevel().id()), RequestsTab.getRequest(0).getLevelData().getDisplayName());
                        Main.sendYTMessage(Utilities.format("🎮 | $NOW_PLAYING_TOP_MESSAGE$",
                                RequestsTab.getRequest(0).getLevelData().getGDLevel().getLevel().name(),
                                RequestsTab.getRequest(0).getLevelData().getGDLevel().getLevel().id()), RequestsTab.getRequest(0).getLevelData().getDisplayName());
                    }
                    else if(SettingsHandler.getSettings("inGameNowPlaying").asBoolean() && !SettingsHandler.getSettings("disableConfirm").asBoolean()){
                        if(pos == -1) {
                            if (!SettingsHandler.getSettings("disableShowPosition").asBoolean()) {
                                sendSuccess(Utilities.format("$CONFIRMATION_MESSAGE$",
                                        level.getLevel().name(),
                                        level.getLevel().id(),
                                        RequestsTab.getQueueSize()), messageID, levelData.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                            } else {
                                sendSuccess(Utilities.format("$CONFIRMATION_MESSAGE_ALT$",
                                        level.getLevel().name(),
                                        level.getLevel().id()), messageID, levelData.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                            }
                        }
                        else {
                            sendSuccess(Utilities.format("$CONFIRMATION_MESSAGE_INSTANT$",
                                    level.getLevel().name(),
                                    level.getLevel().id(),
                                    RequestsTab.getQueueSize()), messageID, levelData.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                        }
                    }

                }

            } catch (GDClientException e) {
                if (Utilities.isCausedBy(e, ActionFailedException.class)) {
                    sendUnallowed(Utilities.format("$LEVEL_DOESNT_EXIST_MESSAGE$"), messageID, finalChatMessage.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                } else if (Utilities.isCausedBy(e, HttpResponseException.class)) {
                    sendError(Utilities.format("$SEARCH_FAILED$"), messageID, finalChatMessage.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                } else if (Utilities.isCausedBy(e, ResponseDeserializationException.class)) {
                    sendError(Utilities.format("$REQUEST_FAILED$"), messageID, finalChatMessage.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                } else {


                    sendError(Utilities.format("$REQUEST_ERROR$", e.getClass(), "(" + e.getStackTrace()[0].getFileName() + ":" + e.getStackTrace()[0].getLineNumber() + ")"), messageID, finalChatMessage.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
                    System.out.println(IDa);
                    System.out.println(message);
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendError(Utilities.format("$REQUEST_ERROR$", e.getClass(), "(" + e.getStackTrace()[0].getFileName() + ":" + e.getStackTrace()[0].getLineNumber() + ")"), messageID, finalChatMessage.isYouTube(), finalChatMessage.isKick(), finalChatMessage.getSenderElseDisplay());
            }
        }).start();
    }

    public static String starToDifficulty(int star){
        switch (star){
            case 1: return "auto";
            case 2: return "easy";
            case 3: return "normal";
            case 4:
            case 5:
                return "hard";
            case 6:
            case 7:
                return "harder";
            case 8:
            case 9:
                return "insane";
            case 10: return "hard demon";
        }
        return "NA";
    }

    public static void saveLogs(LevelData levelData){
        try {
            addedLevels.put(levelData.getGDLevel().getLevel().id(), levelData.getGDLevel().getLevel().levelVersion());
            try {
                Outputs.setOutputStringFile(RequestsUtils.parseInfoString(SettingsHandler.getSettings("outputString").asString()));
            }
            catch (Exception e){
                e.printStackTrace();
            }
            Path file = Paths.get(Defaults.saveDirectory + "\\loquibot\\requestsLog.txt");

            boolean exists = false;
            if (!Files.exists(file)) {
                Files.createFile(file);
            }
            String value = null;
            if (Files.exists(logged)) {
                Scanner sc = new Scanner(logged.toFile());
                while (sc.hasNextLine()) {
                    value = sc.nextLine();
                    if (String.valueOf(levelData.getGDLevel().getLevel().id()).equals(value.split(",")[0])) {
                        sc.close();
                        exists = true;
                        break;
                    }
                    Utilities.sleep(5);
                }
                sc.close();
            }
            if (!exists) {
                Files.write(file, (levelData.getGDLevel().getLevel().id() + "," + levelData.getGDLevel().getLevel().levelVersion() + "\n").getBytes(), StandardOpenOption.APPEND);

            } else {
                BufferedReader fileA = new BufferedReader(new FileReader(Defaults.saveDirectory + "\\loquibot\\requestsLog.txt"));
                StringBuilder inputBuffer = new StringBuilder();
                String line;
                while ((line = fileA.readLine()) != null) {
                    inputBuffer.append(line);
                    inputBuffer.append('\n');
                }
                fileA.close();

                FileOutputStream fileOut = new FileOutputStream(Defaults.saveDirectory + "\\loquibot\\requestsLog.txt");
                fileOut.write(inputBuffer.toString().replace(value, levelData.getGDLevel().getLevel().id() + "," + levelData.getGDLevel().getLevel().levelVersion()).getBytes());
                fileOut.close();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void loadLevels(JSONObject levels) {
        JSONArray levelsArray = levels.getJSONArray("levels");
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
                    level.optString("requester", level.optString("display_name", "Unknown")),
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
                    level.getBoolean("has_verified_coins"),
                    level.optBoolean("is_youtube", false),
                    level.optString("display_name", ""),
                    level.optLong("account_id", 0)

            );
        }
    }

    private static GDLevelExtra getID(String message, boolean isYT, boolean isKick, String messageID, String username) {
        String messageS = message.split(" ", 2)[1].replace("\"", "");
        GDLevelExtra level;
        if(EmojiManager.containsEmoji(messageS)){
            sendUnallowed(Utilities.format("$LEVEL_DOESNT_EXIST_MESSAGE$"), messageID, isYT, isKick, username);
            return null;
        }
        level = GDAPI.getTopLevelByName(messageS);

        if (level == null) {
            sendUnallowed(Utilities.format("$LEVEL_DOESNT_EXIST_MESSAGE$"), messageID, isYT, isKick, username);
            return null;
        }
        return level;
    }

    private static GDLevelExtra checkLevelIDAndGetLevel(long ID) {
        GDLevelExtra level;
        if (ID > 999999999 || ID < 1) {
            return null;
        }
        level = GDAPI.getLevel(ID);
        return level;
    }
    private static void sendError(String message, String messageID, boolean isYT, boolean isKick, String username) {
        if(isYT) Main.sendYTMessage("🔴 | " + message, username);
        else if(isKick) Main.sendKickMessage("🔴 | " + message, username);
        else Main.sendMessage("🔴 | " + message, messageID);
    }
    private static void sendUnallowed(String message, String messageID, boolean isYT, boolean isKick, String username) {
        if(isYT) Main.sendYTMessage("🟡 | " + message, username);
        else if(isKick) Main.sendKickMessage("🟡 | " + message, username);
        else Main.sendMessage("🟡 | " + message, messageID);
    }
    public static void sendSuccess(String message, String messageID, boolean isYT, boolean isKick, String username) {
        if(isYT) Main.sendYTMessage("🟢 | " + message, username);
        else if(isKick) Main.sendKickMessage("🟢 | " + message, username);
        else Main.sendMessage("🟢 | " + message, messageID);
    }

    private static boolean checkList(Object object, String path) {
        String value = null;
        if (object instanceof String) {
            value = (String) object;
        }
        else{
            value = String.valueOf(object);
        }

        Path pathA = Paths.get(Defaults.saveDirectory + path);
        if (Files.exists(pathA)) {
            Scanner sc;
            try {
                sc = new Scanner(pathA.toFile());
                while (sc.hasNextLine()) {
                    String line = sc.nextLine().trim();
                    if (value.equalsIgnoreCase(line)) {
                        sc.close();
                        return true;
                    }
                }
                sc.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static void request(String user, boolean isMod, boolean isSub, String message, String messageID, long userID, ChatMessage chatMessage) {
        addRequest(0, user, isMod, isSub, message, messageID, userID, true, chatMessage);
    }

    public static int getPosFromID(long ID) {
        for (int i = 0; i < RequestsTab.getQueueSize(); i++) {
            if (RequestsTab.getLevelsPanel().getButton(i).getID() == ID) {
                return i;
            }
        }
        return -1;
    }
}
