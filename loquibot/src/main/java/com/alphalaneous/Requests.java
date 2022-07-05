package com.alphalaneous;

import com.alphalaneous.Moderation.Moderation;
import com.alphalaneous.Panels.LevelButton;
import com.alphalaneous.SettingsPanels.OutputSettings;
import com.alphalaneous.SettingsPanels.FiltersSettings;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.TwitchBot.ChatMessage;
import com.vdurmont.emoji.EmojiManager;
import jdash.client.exception.ActionFailedException;
import jdash.client.exception.GDClientException;
import jdash.client.exception.HttpResponseException;
import jdash.client.exception.ResponseDeserializationException;
import jdash.common.entity.GDLevel;
import jdash.common.entity.GDSong;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class Requests {

    public static volatile boolean requestsEnabled = true;
    public static HashMap<Long, String> globallyBlockedIDs = new HashMap<>();
    private static final HashMap<Long, Integer> addedLevels = new HashMap<>();
    private static final HashMap<String, Integer> userStreamLimitMap = new HashMap<>();
    private static final Path logged = Paths.get(Defaults.saveDirectory + "\\loquibot\\requestsLog.txt");
    private static final ArrayList<LevelButton> removedForOffline = new ArrayList<>();


    public static ArrayList<LevelButton> getRemovedForOffline(){
        return removedForOffline;
    }

    public static void addRemovedForOffline(LevelButton button){
        button.resetGonePoints();
        removedForOffline.add(button);
    }


    public static void addRequest(long IDa, String user, boolean isMod, boolean isSub, String message, String messageID, long userID, boolean isCommand, ChatMessage chatMessage) {


        if(!com.alphalaneous.Windows.Window.getWindow().isVisible()) return;
        if(chatMessage == null) {
            chatMessage = new ChatMessage(new String[]{}, user, user, message, new String[]{}, isMod, isSub, false, 0, false);
        }


        if (globallyBlockedIDs.containsKey(IDa)) {
            sendUnallowed(Utilities.format("$GLOBALLY_BLOCKED_LEVEL_MESSAGE$", user, globallyBlockedIDs.get(IDa)), chatMessage.isYouTube());
            return;
        }
        if(chatMessage.isYouTube()){
            user = chatMessage.getDisplayName();
        }
        if(IDa != 0) System.out.println("> Adding Request: "+ IDa);
        else System.out.println("> Adding Request: "+ message);
        ChatMessage finalChatMessage = chatMessage;
        String finalUser = user;
        new Thread(() -> {
            try {
                if(!finalChatMessage.isYouTube() && Moderation.checkIfLink(message)){
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
                if (!requestsEnabled) {
                    sendUnallowed(Utilities.format("$REQUESTS_OFF_MESSAGE$", finalUser), finalChatMessage.isYouTube());
                    return;
                }

                GDLevel level;

                ArrayList<String> arguments = new ArrayList<>();
                arguments.add(""); //Accidentally started array at one due to value I thought existed, easier to add dummy value than change everything.
                Matcher argMatcher = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(message);
                while (argMatcher.find()) {
                    arguments.add(argMatcher.group(1).toLowerCase().trim());
                }
                String levelNameS; //Starting level name for search
                String usernameS; //starting username for search
                if (isCommand) {
                    Matcher IDMatcher = Pattern.compile("(\\d+)").matcher(arguments.get(1));
                    if (IDMatcher.matches() && arguments.size() <= 2) {
                        long ID = Long.parseLong(IDMatcher.group(1));
                        level = checkLevelIDAndGetLevel(ID);
                        if (level == null) return;
                    } else if (arguments.size() > 2) {
                        boolean inQuotes = false;
                        if (arguments.get(2).equalsIgnoreCase("by") || message.contains(" by ")) {
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
                                usernameS = argumentsS[1].trim().replace("\"", "");
                            } else {
                                sendUnallowed(Utilities.format("$LEVEL_COMMAND_FORMAT_MESSAGE$", finalUser), finalChatMessage.isYouTube());
                                return;
                            }

                            level = GDAPI.getLevelByNameByUser(levelNameS, usernameS, inQuotes);

                            if (level == null) {
                                sendUnallowed(Utilities.format("$LEVEL_USER_DOESNT_EXIST_MESSAGE$", finalUser), finalChatMessage.isYouTube());
                                return;
                            }
                        } else {
                            level = getID(message, finalUser, finalChatMessage.isYouTube());
                            if (level == null) return;
                        }
                    } else {
                        level = getID(message, finalUser, finalChatMessage.isYouTube());
                        if (level == null) return;
                    }
                } else {
                    level = checkLevelIDAndGetLevel(IDa);
                    if (level == null) return;
                }

                for (int k = 0; k < RequestsTab.getQueueSize(); k++) {

                    if (level.id() == RequestsTab.getRequest(k).getLevelData().getGDLevel().id()) {
                        int j = k + 1;
                        if (!Settings.getSettings("disableShowPosition").asBoolean()) {
                            sendUnallowed(Utilities.format("$ALREADY_IN_QUEUE_MESSAGE$", finalUser, j), finalChatMessage.isYouTube());
                        } else {
                            sendUnallowed(Utilities.format("$ALREADY_IN_QUEUE_MESSAGE_ALT$", finalUser), finalChatMessage.isYouTube());
                        }
                        return;
                    }
                }
                boolean bypass = false;
                if(Settings.getSettings("twitchEnabled").asBoolean()){
                    bypass = (Settings.getSettings("modsBypass").asBoolean() && isMod) || (finalUser.equalsIgnoreCase(TwitchAccount.login) && Settings.getSettings("streamerBypass").asBoolean());
                }
                if (!bypass) {
                    if (checkList(level.id(), "\\loquibot\\blocked.txt")) {
                        sendUnallowed(Utilities.format("$BLOCKED_LEVEL_MESSAGE$", finalUser), finalChatMessage.isYouTube());
                        return;
                    }
                    if (Files.exists(logged) && (Settings.getSettings("repeatedRequestsAll").asBoolean() && !Settings.getSettings("updatedRepeated").asBoolean()) && Main.programLoaded) {
                        Scanner sc = new Scanner(logged.toFile());
                        while (sc.hasNextLine()) {
                            if (String.valueOf(level.id()).equals(sc.nextLine().split(",")[0])) {
                                sc.close();
                                sendUnallowed(Utilities.format("$REQUESTED_BEFORE_MESSAGE$", finalUser), finalChatMessage.isYouTube());
                                return;
                            }
                        }
                        sc.close();
                    }
                    if (globallyBlockedIDs.containsKey(level.id())) {
                        sendUnallowed(Utilities.format("$GLOBALLY_BLOCKED_LEVEL_MESSAGE$", finalUser, globallyBlockedIDs.get(level.id())), finalChatMessage.isYouTube());
                        return;
                    }
                    if (Settings.getSettings("subscribers").asBoolean()) {
                        if(!finalChatMessage.isYouTube()) {
                            if (!(isSub || isMod)) {
                                sendUnallowed(Utilities.format("$REQUESTS_SUBSCRIBE_MESSAGE$", finalUser), finalChatMessage.isYouTube());
                                return;
                            }
                        }
                    }
                    if (Settings.getSettings("followers").asBoolean()) {
                        if(!finalChatMessage.isYouTube()) {
                            if (APIs.isNotFollowing(finalUser, userID)) {
                                sendUnallowed(Utilities.format("$FOLLOW_MESSAGE$", finalUser), finalChatMessage.isYouTube());
                                return;
                            }
                        }
                    }
                    if (level.id() < Settings.getSettings("minID").asInteger() && Settings.getSettings("minIDOption").asBoolean()) {
                        sendUnallowed(Utilities.format("$MIN_ID_MESSAGE$", finalUser, Settings.getSettings("minID").asInteger()), finalChatMessage.isYouTube());
                        return;
                    }
                    if (level.id() > Settings.getSettings("maxID").asInteger() && Settings.getSettings("maxIDOption").asBoolean()) {
                        sendUnallowed(Utilities.format("$MAX_ID_MESSAGE$", finalUser, Settings.getSettings("maxID").asInteger()), finalChatMessage.isYouTube());
                        return;
                    }
                    if (Settings.getSettings("queueLimitEnabled").asBoolean() && (RequestsTab.getQueueSize() >= Settings.getSettings("queueLimit").asInteger())) {
                        if (!Settings.getSettings("disableQF").asBoolean()) {
                            sendUnallowed(Utilities.format("$QUEUE_FULL_MESSAGE$", finalUser), finalChatMessage.isYouTube());
                        }
                        return;
                    }
                    if (Settings.getSettings("userLimitEnabled").asBoolean()) {
                        int size = 0;
                        for (int i = 0; i < RequestsTab.getQueueSize(); i++) {
                            if (RequestsTab.getRequest(i).getLevelData().getRequester().equalsIgnoreCase(finalChatMessage.getSender())) {
                                size++;
                            }
                        }
                        if (size >= Settings.getSettings("userLimit").asInteger()) {
                            sendUnallowed(Utilities.format("$MAXIMUM_LEVELS_MESSAGE$", finalUser), finalChatMessage.isYouTube());
                            return;
                        }
                    }
                    if (Settings.getSettings("userLimitStreamEnabled").asBoolean()) {
                        if (userStreamLimitMap.containsKey(finalChatMessage.getSender())) {
                            if (userStreamLimitMap.get(finalChatMessage.getSender()) >= Settings.getSettings("userLimitStream").asInteger()) {
                                sendUnallowed(Utilities.format("$MAXIMUM_LEVELS_STREAM_MESSAGE$", finalUser), finalChatMessage.isYouTube());
                                return;
                            }
                        }
                    }
                    if (addedLevels.containsKey(level.id()) && (Settings.getSettings("repeatedRequests").asBoolean() && !Settings.getSettings("updatedRepeated").asBoolean())) {
                        sendUnallowed(Utilities.format("$REQUESTED_BEFORE_MESSAGE$", finalUser), finalChatMessage.isYouTube());
                        return;
                    }
                }
                if (userStreamLimitMap.containsKey(finalChatMessage.getSender())) {
                    userStreamLimitMap.put(finalChatMessage.getSender(), userStreamLimitMap.get(finalChatMessage.getSender()) + 1);
                } else {
                    userStreamLimitMap.put(finalChatMessage.getSender(), 1);
                }

                LevelData levelData = new LevelData();
                levelData.setYouTube(finalChatMessage.isYouTube());
                if (levelData.isYouTube()) levelData.setDisplayName(finalChatMessage.getDisplayName());
                if (!bypass) {
                    if (checkList(level.creatorName(), "\\loquibot\\blockedGDUsers.txt")) {
                        sendUnallowed(Utilities.format("$BLOCKED_CREATOR_MESSAGE$", finalUser), levelData.isYouTube());
                        return;
                    }
                    if (Settings.getSettings("rated").asBoolean() && !(level.stars() > 0)) {
                        sendUnallowed(Utilities.format("$STAR_RATED_MESSAGE$", finalUser), levelData.isYouTube());
                        return;
                    }
                    if (Settings.getSettings("unrated").asBoolean() && level.stars() > 0) {
                        sendUnallowed(Utilities.format("$UNRATED_MESSAGE$", finalUser),levelData.isYouTube());
                        return;
                    }
                    if (Settings.getSettings("minObjectsOption").asBoolean() && level.objectCount() < Settings.getSettings("minObjects").asInteger()) {
                        sendUnallowed(Utilities.format("$FEW_OBJECTS_MESSAGE$", finalUser),levelData.isYouTube());
                        return;
                    }
                    if (Settings.getSettings("maxObjectsOption").asBoolean() && level.objectCount() > Settings.getSettings("maxObjects").asInteger()) {
                        sendUnallowed(Utilities.format("$MANY_OBJECTS_MESSAGE$", finalUser),levelData.isYouTube());
                        return;
                    }
                    if (level.objectCount() != 0) {
                        if (Settings.getSettings("minLikesOption").asBoolean() && level.objectCount() < Settings.getSettings("minLikes").asInteger()) {
                            sendUnallowed(Utilities.format("$FEW_LIKES_MESSAGE$", finalUser),levelData.isYouTube());
                            return;
                        }
                        if (Settings.getSettings("maxObjectsOption").asBoolean() && level.objectCount() > Settings.getSettings("maxLikes").asInteger()) {
                            sendUnallowed(Utilities.format("$MANY_LIKES_MESSAGE$", finalUser),levelData.isYouTube());
                            return;
                        }
                    }
                }
                if (messageID != null) {
                    levelData.setMessageID(messageID);
                }

                if (level.featuredScore() > 0) {
                    levelData.setFeatured();
                }
                levelData.setLevelData(level);
                if (!bypass) {
                    if (Files.exists(logged) && Settings.getSettings("updatedRepeated").asBoolean()) {
                        Scanner sc = new Scanner(logged.toFile());
                        while (sc.hasNextLine()) {
                            String levelLine = sc.nextLine();
                            if (String.valueOf(level.id()).equals(levelLine.split(",")[0])) {
                                int version;
                                if (levelLine.split(",").length == 1) {
                                    version = 1;
                                } else {
                                    version = Integer.parseInt(levelLine.split(",")[1]);
                                }
                                if (version >= levelData.getGDLevel().levelVersion()) {
                                    sc.close();
                                    sendUnallowed(Utilities.format("$REQUESTED_BEFORE_MESSAGE$", finalUser),levelData.isYouTube());
                                    return;
                                }
                            }
                        }
                        sc.close();
                    }
                    if (addedLevels.containsKey(level.id()) && (Settings.getSettings("updatedRepeated").asBoolean())) {
                        if (addedLevels.get(level.id()) >= levelData.getGDLevel().levelVersion()) {
                            sendUnallowed(Utilities.format("$REQUESTED_BEFORE_MESSAGE$", finalUser),levelData.isYouTube());
                            return;
                        }
                    }
                    if (FiltersSettings.excludedDifficulties.contains(levelData.getSimpleDifficulty().toLowerCase()) && Settings.getSettings("disableDifficulties").asBoolean()) {
                        sendUnallowed(Utilities.format("$DIFFICULTY_MESSAGE$", finalUser),levelData.isYouTube());
                        return;
                    }
                    if (FiltersSettings.excludedRequestedDifficulties.contains(starToDifficulty(levelData.getGDLevel().requestedStars())) && Settings.getSettings("disableReqDifficulties").asBoolean()) {
                        sendUnallowed(Utilities.format("$REQ_DIFFICULTY_MESSAGE$", finalUser),levelData.isYouTube());
                        return;
                    }
                    if (FiltersSettings.excludedLengths.contains(level.length().name().toLowerCase()) && Settings.getSettings("disableLengths").asBoolean()) {
                        sendUnallowed(Utilities.format("$LENGTH_MESSAGE$", finalUser),levelData.isYouTube());
                        return;
                    }
                }

                if (Settings.getSettings("autoDL").asBoolean()) {
                    new Thread(() -> {
                        Optional<GDSong> song = levelData.getGDLevel().song();
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

                levelData.setRequester(finalUser);
                levelData.setMessage(message);
                levelData.setMessageID(messageID);

                Optional<String> creatorName = levelData.getGDLevel().creatorName();

                try {
                    creatorName.ifPresent(s -> levelData.setPlayerIcon(GDAPI.getIcon(GDAPI.getGDUserProfile(s), 100)));
                }
                catch (Exception ignored){
                    //if green user, don't fail
                }
                LevelButton levelButton = new LevelButton(levelData);

                RequestsTab.addRequest(levelButton);


                try {
                    RequestsTab.getLevelsPanel().setSelect(LevelButton.selectedID, RequestsTab.getQueueSize() == 1);
                } catch (Exception ignored) {
                }
                RequestsTab.getLevelsPanel().setWindowName(RequestsTab.getQueueSize());
                RequestFunctions.saveFunction();
                if (Main.sendMessages && !Settings.getSettings("disableConfirm").asBoolean() && RequestsTab.getQueueSize() != 1) {
                    if (!Settings.getSettings("disableShowPosition").asBoolean()) {
                        sendSuccess(Utilities.format("$CONFIRMATION_MESSAGE$",
                                levelData.getRequester(),
                                level.name(),
                                level.id(),
                                RequestsTab.getQueueSize()), Settings.getSettings("whisperConfirm").asBoolean(), finalUser,levelData.isYouTube());
                    } else {
                        sendSuccess(Utilities.format("$CONFIRMATION_MESSAGE_ALT$",
                                levelData.getRequester(),
                                level.name(),
                                level.id()), Settings.getSettings("whisperConfirm").asBoolean(), finalUser,levelData.isYouTube());
                    }
                }

                if (Main.sendMessages && !Settings.getSettings("disableConfirm").asBoolean() && RequestsTab.getQueueSize() == 1) {
                    StringSelection selection = new StringSelection(String.valueOf(RequestsTab.getRequest(0).getLevelData().getGDLevel().id()));
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(selection, selection);

                    if (Main.sendMessages && !Settings.getSettings("disableNP").asBoolean()) {
                        Main.sendMessage(Utilities.format("🎮 | $NOW_PLAYING_TOP_MESSAGE$",
                                RequestsTab.getRequest(0).getLevelData().getRequester(),
                                RequestsTab.getRequest(0).getLevelData().getGDLevel().name(),
                                RequestsTab.getRequest(0).getLevelData().getGDLevel().id()), Settings.getSettings("announceNP").asBoolean());
                        Main.sendYTMessage(Utilities.format("🎮 | $NOW_PLAYING_TOP_MESSAGE$",
                                RequestsTab.getRequest(0).getLevelData().getRequester(),
                                RequestsTab.getRequest(0).getLevelData().getGDLevel().name(),
                                RequestsTab.getRequest(0).getLevelData().getGDLevel().id()));
                    }

                }

            } catch (GDClientException e) {
                boolean isYT = finalChatMessage.isYouTube();

                if (Utilities.isCausedBy(e, ActionFailedException.class)) {
                    sendUnallowed(Utilities.format("$LEVEL_DOESNT_EXIST_MESSAGE$", finalUser), isYT);
                } else if (Utilities.isCausedBy(e, HttpResponseException.class)) {
                    sendError(Utilities.format("$SEARCH_FAILED$", finalUser), isYT);
                } else if (Utilities.isCausedBy(e, ResponseDeserializationException.class)) {
                    sendError(Utilities.format("$REQUEST_FAILED$", finalUser), isYT);
                } else {
                    sendError(Utilities.format("$REQUEST_ERROR$", finalUser), isYT);
                    System.out.println(IDa);
                    System.out.println(message);
                    e.printStackTrace();
                }
            } catch (Exception e) {
                boolean isYT = finalChatMessage.isYouTube();
                e.printStackTrace();
                sendError(Utilities.format("$REQUEST_ERROR$", finalUser), isYT);
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
            addedLevels.put(levelData.getGDLevel().id(), levelData.getGDLevel().levelVersion());
            OutputSettings.setOutputStringFile(RequestsUtils.parseInfoString(Settings.getSettings("outputString").asString()));
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
                    if (String.valueOf(levelData.getGDLevel().id()).equals(value.split(",")[0])) {
                        sc.close();
                        exists = true;
                        break;
                    }
                    Utilities.sleep(5);
                }
                sc.close();
            }
            if (!exists) {
                Files.write(file, (levelData.getGDLevel().id() + "," + levelData.getGDLevel().levelVersion() + "\n").getBytes(), StandardOpenOption.APPEND);

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
                fileOut.write(inputBuffer.toString().replace(value, levelData.getGDLevel().id() + "," + levelData.getGDLevel().levelVersion()).getBytes());
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
                    level.optString("display_name", "")

            );
        }
    }

    private static GDLevel getID(String message, String user, boolean isYT) {
        String messageS = message.split(" ", 2)[1].replace("\"", "");
        GDLevel level;
        if(EmojiManager.containsEmoji(messageS)){
            sendUnallowed(Utilities.format("$LEVEL_DOESNT_EXIST_MESSAGE$", user), isYT);
            return null;
        }
        level = GDAPI.getTopLevelByName(messageS);

        if (level == null) {
            sendUnallowed(Utilities.format("$LEVEL_DOESNT_EXIST_MESSAGE$", user), isYT);
            return null;
        }
        return level;
    }

    private static GDLevel checkLevelIDAndGetLevel(long ID) {
        GDLevel level;
        if (ID > 999999999 || ID < 1) {
            return null;
        }
        level = GDAPI.getLevel(ID);
        return level;
    }

    private static void sendError(String message, boolean isYT) {
        if(isYT) Main.sendYTMessage("🔴 | " + message);
        else Main.sendMessage("🔴 | " + message);
    }

    private static void sendUnallowed(String message, boolean isYT) {
        if(isYT) Main.sendYTMessage("🟡 | " + message);
        else Main.sendMessage("🟡 | " + message);
    }

    private static void sendSuccess(String message, boolean whisper, String user, boolean isYT) {
        if(isYT) Main.sendYTMessage("🟢 | " + message);
        else Main.sendMessage("🟢 | " + message, whisper, user);
    }

    private static boolean checkList(Object object, String path) {
        String value = String.valueOf(object);
        if (object instanceof String) {
            value = (String) object;
        }
        Path pathA = Paths.get(Defaults.saveDirectory + path);
        if (Files.exists(pathA)) {
            Scanner sc;
            try {
                sc = new Scanner(pathA.toFile());
                while (sc.hasNextLine()) {
                    if (value.equals(sc.nextLine())) {
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

    private static void parse(byte[] level, long levelID) {
        boolean image = false;
        all:
        for (int k = 0; k < RequestsTab.getQueueSize(); k++) {

            if (RequestsTab.getRequest(k).getLevelData().getGDLevel().id() == levelID) {
                StringBuilder decompressed = null;
                try {
                    decompressed = decompress(level);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int imageIDCount = 0;
                String color = "";
                assert decompressed != null;
                String[] values = decompressed.toString().split(";");
                if ((values.length < Settings.getSettings("minObjects").asInteger()) && Settings.getSettings("minObjectsOption").asBoolean()) {
                    Main.sendMessage(Utilities.format("🟡 | $TOO_FEW_OBJECTS_MESSAGE$", RequestsTab.getRequest(k).getLevelData().getRequester()));
                    //LevelsPanel.removeButton(k);
                    RequestsTab.removeRequest(k);
                    return;
                }
                if ((values.length > Settings.getSettings("maxObjects").asInteger()) && Settings.getSettings("maxObjectsOption").asBoolean()) {
                    Main.sendMessage(Utilities.format("🟡 | $TOO_MANY_OBJECTS_MESSAGE$", RequestsTab.getRequest(k).getLevelData().getRequester()));
                    //LevelsPanel.removeButton(k);
                    RequestsTab.removeRequest(k);
                    return;
                }
                for (String value1 : values) {
                    if (Settings.getSettings("lowCPUMode").asBoolean()) {
                        Utilities.sleep(50);
                    }
                    if (value1.startsWith("1,1110") || value1.startsWith("1,211") || value1.startsWith("1,914")) {
                        String value = value1.replaceAll("(,[^,]*),", "$1;");
                        String[] attributes = value.split(";");
                        double scale = 0;
                        boolean hsv = false;
                        boolean zOrder = false;
                        String tempColor = "";
                        String text = "";
                        for (String attribute : attributes) {

                            if (attribute.startsWith("32")) {
                                if (Double.parseDouble(attribute.split(",")[1]) < 1) {
                                    scale = Double.parseDouble(attribute.split(",")[1]);
                                }
                            }
                            if (attribute.startsWith("41")) {
                                hsv = true;
                            }
                            if (attribute.startsWith("21")) {
                                tempColor = attribute.split(",")[1];
                            }
                            if (attribute.startsWith("25")) {
                                zOrder = true;
                            }
                            if (attribute.startsWith("31")) {
                                String formatted = attribute.split(",")[1].replace("_", "/").replace("-", "+");
                                text = new String(Base64.getDecoder().decode(formatted));
                            }
                        }
                        InputStream is = Main.class.getClassLoader()
                                .getResourceAsStream("Resources/blockedWords.txt");
                        assert is != null;
                        InputStreamReader isr = new InputStreamReader(is);
                        BufferedReader br = new BufferedReader(isr);
                        String line;
                        try {
                            out:
                            while ((line = br.readLine()) != null) {
                                String[] text1 = text.toUpperCase().split(" ");
                                for (String s : text1) {
                                    if (s.equalsIgnoreCase(line)) {
                                        RequestsTab.getRequest(k).getLevelData().setContainsVulgar();
                                        break out;
                                    }
                                }
                            }
                            if (scale != 0.0 && hsv) {
                                if (tempColor.equalsIgnoreCase(color) && !zOrder) {
                                    imageIDCount++;
                                }
                            }
                            if (imageIDCount >= 1000) {
                                image = true;
                            }
                            color = tempColor;
                            is.close();
                            isr.close();
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            break all;
                        }
                    }
                    Utilities.sleep(0);
                }
                try {
                    URL ids = new URL("https://raw.githubusercontent.com/Alphatism/loquibot/Master/GD%20Request%20Bot/External/false%20positives.txt");
                    Scanner s = new Scanner(ids.openStream());
                    while (s.hasNextLine()) {
                        String lineA = s.nextLine();
                        if (lineA.equalsIgnoreCase(String.valueOf(levelID))) {
                            image = false;
                            break;
                        }
                    }
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (image) {
                    RequestsTab.getRequest(k).getLevelData().setContainsImage();
                }
                Utilities.sleep(250);
                try {
                    RequestsTab.getRequest(k).getLevelData().setAnalyzed();
                    RequestsTab.getLevelsPanel().updateUI(RequestsTab.getRequest(k).getLevelData().getGDLevel().id());
                } catch (IndexOutOfBoundsException ignored) {
                }
                if (k == 0) {
                    RequestFunctions.containsBadStuffCheck();
                }
                break;
            }
        }
    }

    private static StringBuilder decompress(byte[] compressed) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(bis);
        BufferedReader br = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        bis.close();
        br.close();
        gis.close();
        bis.close();
        return sb;
    }

    public static void request(String user, boolean isMod, boolean isSub, String message, String messageID, long userID, ChatMessage chatMessage) {
        addRequest(0, user, isMod, isSub, message, messageID, userID, true, chatMessage);
    }

    public static int getPosFromIDBasic(long ID) {
        for (int i = 0; i < RequestsTab.getQueueSize(); i++) {
            if (RequestsTab.getLevelsPanel().getButtonBasic(i).getID() == ID) {
                return i;
            }
        }
        return -1;
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
