package com.alphalaneous;

import jdash.client.GDClient;
import jdash.client.exception.GDClientException;
import jdash.client.request.GDRouter;
import jdash.common.CommentSortMode;
import jdash.common.IconType;
import jdash.common.LevelBrowseMode;
import jdash.common.LevelSearchFilter;
import jdash.common.entity.*;
import jdash.graphics.SpriteFactory;
import org.imgscalr.Scalr;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class GDAPI {

    private static GDClient client = GDClient.create();
    private static final SpriteFactory spriteFactory = SpriteFactory.create();

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

    public static GDLevel getLevel(long ID){
        return client.findLevelById(ID).block();
    }

    public static GDLevel getTopLevelByName(String name) {
        return Objects.requireNonNull(client.browseLevels(LevelBrowseMode.SEARCH, name, LevelSearchFilter.create(), 0).collectList().block()).get(0);
    }
    public static GDLevel getLevelByNameByUser(String name, String username, boolean isEqual){
        GDLevel level;
        for(int j = 0; j < 10; j++) {
            List<GDLevel> levels = client.browseLevelsByUser(getGDUserStats(username).playerId(), j).collectList().block();
            try {
                for (int i = 0; i < 10; i++) {
                    level = Objects.requireNonNull(levels).get(i);
                    if (isEqual && level.name().equalsIgnoreCase(name)) {
                        return level;
                    } else if (!isEqual && level.name().toLowerCase().startsWith(name.toLowerCase())) {
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


    public static List<GDComment> getGDComments(long ID, boolean mostLiked, int page) {
        try {
            CommentSortMode commentSortMode = CommentSortMode.RECENT;
            if (mostLiked) commentSortMode = CommentSortMode.MOST_LIKED;

            return client.getCommentsForLevel(ID, commentSortMode, page, 20).collectList().block(Duration.ofMillis(2500));
        }
        catch (GDClientException e){
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


}
