package com.alphalaneous.Services.YouTube;

import com.alphalaneous.Swing.BrowserWindow;
import com.alphalaneous.Utils.Utilities;
import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Windows.Window;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.LiveBroadcast;
import com.google.api.services.youtube.model.LiveBroadcastListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

/**
 * Gets a live chat id from a video id or current signed in user.
 *
 * The videoId is often included in the video's url, e.g.:
 * https://www.youtube.com/watch?v=L5Xc93_ZL60
 *                                 ^ videoId
 * The video URL may be found in the browser address bar, or by right-clicking a video and selecting
 * Copy video URL from the context menu.
 *
 * @author Jim Rogers
 */
public class GetLiveChatID {

    static boolean shownError = false;

    public static String getLiveChatId(YouTube youtube) throws IOException {
        try {
            YouTube.LiveBroadcasts.List broadcastList = youtube
                    .liveBroadcasts()
                    .list(Collections.singletonList("snippet"))
                    .setFields("items/snippet/liveChatId")
                    .setBroadcastType("all")
                    .setBroadcastStatus("active");
            LiveBroadcastListResponse broadcastListResponse = broadcastList.execute();
            for (LiveBroadcast b : broadcastListResponse.getItems()) {
                String liveChatId = b.getSnippet().getLiveChatId();
                if (liveChatId != null && !liveChatId.isEmpty()) {
                    return liveChatId;
                }
            }
        }
        catch (GoogleJsonResponseException e) {
            if (Window.getWindow().isVisible() && !shownError) {
                new Thread(() -> {
                    String option = DialogBox.showDialogBox("Cannot connect to YouTube Chat!", "Your account isn't enabled for livestreaming! :(", "Your account needs to be verified.", new String[]{"Okay", "Help"});
                    if(option.equalsIgnoreCase("help")){
                        try {
                            Utilities.openURL(new URI("https://support.google.com/youtube/answer/171664?hl=en"));
                        } catch (URISyntaxException ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();
                shownError = true;
            }
        }
        return null;
    }

    /**
     * Retrieves the liveChatId from the broadcast associated with a videoId.
     *
     * @param youtube The object is used to make YouTube Data API requests.
     * @param videoId The videoId associated with the live broadcast.
     * @return A liveChatId, or null if not found.
     */
    public static String getLiveChatId(YouTube youtube, String videoId) throws IOException {
        // Get liveChatId from the video
        YouTube.Videos.List videoList = youtube.videos()
                .list(Collections.singletonList("liveStreamingDetails"))
                .setFields("items/liveStreamingDetails/activeLiveChatId")
                .setId(Collections.singletonList(videoId));
        VideoListResponse response = videoList.execute();
        for (Video v : response.getItems()) {
            String liveChatId = v.getLiveStreamingDetails().getActiveLiveChatId();
            if (liveChatId != null && !liveChatId.isEmpty()) {
                return liveChatId;
            }
        }

        return null;
    }
}