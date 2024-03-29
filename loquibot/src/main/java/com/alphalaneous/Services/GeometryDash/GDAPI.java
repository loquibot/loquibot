package com.alphalaneous.Services.GeometryDash;

import com.alphalaneous.Main;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Utils.AlreadyInQueueException;
import com.alphalaneous.Utils.NoLevelException;
import com.alphalaneous.Utils.Utilities;
import jdash.client.GDClient;
import jdash.client.exception.GDClientException;
import jdash.client.request.GDRequest;
import jdash.client.request.GDRouter;
import jdash.client.response.GDResponse;
import jdash.common.*;
import jdash.common.entity.*;
import jdash.graphics.SpriteFactory;
import org.imgscalr.Scalr;
import org.json.JSONObject;
import reactor.core.publisher.Flux;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static jdash.client.request.GDRequests.GET_GJ_LEVELS_21;
import static jdash.client.request.GDRequests.commonParams;
import static jdash.client.response.GDResponseDeserializers.levelSearchResponse;

public class GDAPI {

    private static final HashSet<Long> batchQueue = new HashSet<>();
    private static final HashSet<Long> justBatched = new HashSet<>();


    private static final HashSet<BatchedLevel> batchLevels = new HashSet<>();

    private static GDClient client = GDClient.create();
    private static final SpriteFactory spriteFactory = SpriteFactory.create();


    public static GDClient getClient(){
        return client;
    }

    public static ImageIcon getIcon(GDUserProfile user){
        return getIcon(user, 30);
    }
	public static ImageIcon getIcon(GDUserProfile user, int scale){
	    try {

	        boolean hasGlowOutline = user.hasGlowOutline();
	        if(user.color1Id() == 15){
	            hasGlowOutline = true;
            }
            BufferedImage icon = spriteFactory.makeSprite(IconType.CUBE, user.cubeIconId(), user.color1Id(), user.color2Id(), hasGlowOutline);
            Image imgScaled = icon.getScaledInstance(scale, scale, Image.SCALE_SMOOTH);

            return new ImageIcon(imgScaled);
	    } catch (Exception e) {
            BufferedImage icon = spriteFactory.makeSprite(IconType.CUBE, 1, 1, 1, false);
            Image imgScaled = icon.getScaledInstance(scale, scale, Image.SCALE_SMOOTH);
            icon.flush();
            return new ImageIcon(imgScaled);
	    }
	}
    public static ImageIcon getIcon(IconType type, int id, int color1Id, int color2Id, boolean withGlowOutline, int scale){

        try {
            BufferedImage icon = Scalr.resize(spriteFactory.makeSprite(type, id, color1Id, color2Id, withGlowOutline), Scalr.Method.BALANCED,scale, Scalr.OP_ANTIALIAS);
            //Image imgScaled = icon.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            icon.flush();
            return new ImageIcon(icon);
        } catch (Exception e) {
            BufferedImage icon = Scalr.resize(spriteFactory.makeSprite(IconType.CUBE, 1, 1, 1, false),Scalr.Method.BALANCED,scale,Scalr.OP_ANTIALIAS);
            //Image imgScaled = icon.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            icon.flush();
            return new ImageIcon(icon);
        }
    }

    public static void changeServer(String server){
        GDRouter router = GDRouter.builder()
                .setBaseUrl(server).build();
        client = GDClient.create()
                .withRouter(router);
    }

    public static boolean login(String username, String password){

        try {
            client = client.login(username, password).block();
            return true;
        }
        catch (GDClientException e){
            return false;
        }
    }

    public static GDSong getSong(long songID){
        return client.getSongInfo(songID).block();
    }

    public static GDLevelExtra getLevel(long ID){

        batchQueue.add(ID);

        while (!notifyBool.get()){
            Utilities.sleep(2);
        }

        for(BatchedLevel level : batchLevels){

            if(justBatched.contains(ID)) throw new AlreadyInQueueException(level.getLevelExtra());

            if(level.getID() == ID){
                if(!level.isExists()){
                    throw new NoLevelException();
                }
                justBatched.add(ID);
                return level.getLevelExtra();
            }
        }
        return null;
    }

    static AtomicBoolean notifyBool = new AtomicBoolean(false);

    public static void startBatchListener(){
        new Thread(() -> {

            while(true){

                if(!batchQueue.isEmpty()){
                    justBatched.clear();
                    Main.logger.info("Adding batch: " + Arrays.toString(batchQueue.toArray()));

                    ArrayList<GDLevelExtra> levelExtras = getLevels(batchQueue);

                    for(GDLevelExtra levelExtra : levelExtras){
                        batchLevels.add(new BatchedLevel(levelExtra.getLevel().id(), true, levelExtra));
                    }

                    ArrayList<Long> found = new ArrayList<>();

                    for(long ID : batchQueue){
                        for(BatchedLevel level : batchLevels){
                            if(level.getID() == ID){
                                found.add(ID);
                            }
                        }
                    }

                    found.forEach(batchQueue::remove);

                    for(long ID : batchQueue){
                        batchLevels.add(new BatchedLevel(ID, false, null));
                    }

                    batchQueue.clear();
                    notifyBool.set(true);
                    Utilities.sleep(100);
                    notifyBool.set(false);
                }
                Utilities.sleep(10000);
            }
        }).start();
    }


    static int wait = 0;
    public static GDLevelExtra browseLevels(LevelBrowseMode mode, String query, LevelSearchFilter filter,
                                            int page) {

        if(SettingsHandler.getSettings("waitForRequests").asBoolean()){
            wait += 1000;
            Utilities.sleep(wait);
            wait -= 1000;
        }

        var request = GDRequest.of(GET_GJ_LEVELS_21)
                .addParameters(commonParams())
                .addParameters(Objects.requireNonNullElse(filter, LevelSearchFilter.create()).toMap())
                .addParameter("page", page)
                .addParameter("type", mode.getType())
                .addParameter("str", Objects.requireNonNullElse(query, ""));
        if (mode == LevelBrowseMode.FOLLOWED) {
            request.addParameter("followed", client.getFollowedAccountIds().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(",")));
        }
        GDResponse searchResponse = request.execute(client.getCache(), client.getRouter());
        String searchString = searchResponse.deserialize(e -> e).block();
        GDLevel level = Objects.requireNonNull(searchResponse.deserialize(levelSearchResponse()).flatMapMany(Flux::fromIterable).collectList().block()).get(0);

        long accountID = 0;
        int length = 0;

        if(searchString != null){


            String infoString = searchString.split("#")[0];

            String[] splitByColon = infoString.split(":");

            for(int i = 0; i < splitByColon.length; i+=2){

                if ((i + 1) >= splitByColon.length) {
                    break;
                }

                String partA = splitByColon[i];
                String partB = splitByColon[i+1];

                if(partA.equals("15")){
                    length = Integer.parseInt(partB);
                }
            }

            accountID = getAccountID(searchString, level, accountID);
        }

        return new GDLevelExtra(level, accountID, length);
    }

    private static long getAccountID(String searchString, GDLevel level, long accountID) {
        String accountsString;
        accountsString = searchString.split("#")[1];
        String[] accounts = accountsString.split("\\|");

        for(String str : accounts){
            if(str.equalsIgnoreCase("")){
                accountID = -1;
            }
            else {
                long userID = Long.parseLong(str.split(":")[0]);
                if (userID == level.creatorPlayerId()) {
                    accountID = Long.parseLong(str.split(":")[2]);
                    break;
                }
            }
        }
        return accountID;
    }

    public static ArrayList<GDLevelExtra> browseLevelsByUser(long playerId, int page) {

        var request = GDRequest.of(GET_GJ_LEVELS_21)
                .addParameters(commonParams())
                .addParameter("page", page)
                .addParameter("type", LevelBrowseMode.BY_USER.getType())
                .addParameter("str", "" + playerId);

        GDResponse searchResponse = request.execute(client.getCache(), client.getRouter());
        String searchString = searchResponse.deserialize(e -> e).block();
        List<GDLevel> levels = searchResponse.deserialize(levelSearchResponse()).flatMapMany(Flux::fromIterable).collectList().block();

        ArrayList<GDLevelExtra> returnedLevels = new ArrayList<>();

        if(levels != null) {
            for (GDLevel level : levels) {

                long accountID = 0;
                int length = -1;

                if (searchString != null) {

                    String infoString = searchString.split("#")[0];

                    String[] levelsString = infoString.split("\\|");

                    out: for(String levelString : levelsString) {

                        String[] splitByColon = levelString.split(":");

                        for (int i = 0; i < splitByColon.length; i += 2) {

                            if ((i + 1) >= splitByColon.length) {
                                break;
                            }

                            String partA = splitByColon[i];
                            String partB = splitByColon[i + 1];

                            if(partA.equals("1") && !partB.equals("" + level.id())){
                                continue out;
                            }
                            if (partA.equals("15")) {
                                length = Integer.parseInt(partB);
                            }
                        }
                    }

                    accountID = getAccountID(searchString, level, accountID);
                }
                returnedLevels.add(new GDLevelExtra(level, accountID, length));
            }
        }
        return returnedLevels;
    }


    public static ArrayList<GDLevelExtra> getLevels(HashSet<Long> IDs) {

        ArrayList<GDLevelExtra> returnedLevels = new ArrayList<>();
        
        var request = GDRequest.of(GET_GJ_LEVELS_21)
                .addParameters(commonParams())
                .addParameter("page", 0)
                .addParameter("type", 26)
                .addParameter("total", IDs.size())
                .addParameter("str", IDs.stream().map(Object::toString)
                        .collect(Collectors.joining(",")));


        GDResponse searchResponse = request.execute(client.getCache(), client.getRouter());
        String searchString = searchResponse.deserialize(e -> e).block();
        List<GDLevel> theLevels;

        try {
            theLevels = searchResponse.deserialize(levelSearchResponse()).flatMapMany(Flux::fromIterable).collectList().block();
        }
        catch (Exception e){
            Main.logger.error(e.getMessage(), e);
            return returnedLevels;
        }


        if(theLevels != null) {

            for (GDLevel level : theLevels) {
                returnedLevels.add(new GDLevelExtra(level));
            }

            if (searchString != null) {

                int length = 0;

                int id = 0;

                String infoString = searchString.split("#")[0];

                String[] splitByColon = infoString.split(":");

                for(int i = 0; i < splitByColon.length; i+=2){

                    if ((i + 1) >= splitByColon.length) {
                        break;
                    }

                    String partA = splitByColon[i];
                    String partB = splitByColon[i+1];

                    if(partA.equals("1")){
                        id = Integer.parseInt(partB);
                    }

                    if(partA.equals("15")){
                        length = Integer.parseInt(partB);
                    }

                }

                for (GDLevelExtra level : returnedLevels) {
                    if (level.getLevel().id() == id) {
                        level.setLength(length);
                    }
                }

                String accountsString = searchString.split("#")[1];
                String[] accounts = accountsString.split("\\|");

                for (String str : accounts) {
                    if (!str.equalsIgnoreCase("")) {
                        long userID = Long.parseLong(str.split(":")[0]);

                        for (GDLevelExtra level : returnedLevels) {
                            if (level.getLevel().creatorPlayerId() == userID) {
                                level.setAccountID(Long.parseLong(str.split(":")[2]));
                            }
                        }
                    }
                }
            }
        }
        return returnedLevels;
    }


    public static GDLevelExtra getTopLevelByName(String name) {


        if(SettingsHandler.getSettings("isWhitelisted").asBoolean()) {

            HashMap<String, String> params = new HashMap<>();
            params.put("name", name);

            JSONObject levelData = com.alphalaneous.Utils.Utilities.getFromLoquiServers("getTopLevelByName", params);

            return com.alphalaneous.Utils.Utilities.jsonToGDLevelExtra(levelData);
        }
        else {
            return Objects.requireNonNull(browseLevels(LevelBrowseMode.SEARCH, name, LevelSearchFilter.create(), 0));
        }
    }
    public static GDLevelExtra getLevelByNameByUser(String name, String username, boolean isEqual){

        if(SettingsHandler.getSettings("isWhitelisted").asBoolean()) {

            HashMap<String, String> params = new HashMap<>();
            params.put("name", name);
            params.put("username", username);
            params.put("isEqual", String.valueOf(isEqual));

            JSONObject levelData = com.alphalaneous.Utils.Utilities.getFromLoquiServers("getLevelByNameByUser", params);

            return com.alphalaneous.Utils.Utilities.jsonToGDLevelExtra(levelData);
        }
        else {
            GDLevelExtra level;
            for(int j = 0; j < 10; j++) {
                GDUserStats stats = getGDUserStats(username);
                ArrayList<GDLevelExtra> levels = browseLevelsByUser(stats.playerId(), j);
                try {
                    for (int i = 0; i < 10; i++) {
                        level = Objects.requireNonNull(levels).get(i);

                        if (isEqual && level.getLevel().name().equalsIgnoreCase(name)) {
                            return level;
                        } else if (!isEqual && level.getLevel().name().toLowerCase().startsWith(name.toLowerCase())) {
                            return level;
                        }
                    }
                }
                catch (IndexOutOfBoundsException e){
                    return null;
                }
            }
            return null;
        }
    }


    public static List<GDComment> getGDComments(long ID, boolean mostLiked, int page) {
        try {
            CommentSortMode commentSortMode = CommentSortMode.RECENT;
            if (mostLiked) commentSortMode = CommentSortMode.MOST_LIKED;

            return client.getCommentsForLevel(ID, commentSortMode, page, 20).collectList().block(Duration.ofSeconds(2));
        }
        catch (Exception e){
            return null;
        }
    }

    public static GDUserProfile getGDUserProfile(long accountID){
        return client.getUserProfile(accountID).block();
    }

    public static GDUserProfile getGDUserProfile(String username){
        return client.getUserProfile(getGDUserStats(username).accountId()).block();

    }
    public static GDUserStats getGDUserStats(String username){
        return Objects.requireNonNull(client.searchUsers(username, 0).collectList().block()).get(0);
    }

    public static GDLevelDownload getGDLevel(long ID) {
        return client.downloadLevel(ID).block();
    }
}
