package com.alphalaneous.Services.YouTube;

import com.alphalaneous.Components.DialogBox;
import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.Utilities;
import com.alphalaneous.Window;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.LiveBroadcast;
import com.google.api.services.youtube.model.LiveBroadcastListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

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
            if (Window.getFrame().isVisible() && !shownError) {
                new Thread(() -> {
                    String option = DialogBox.showDialogBox("Cannot connect to YouTube Chat!", "Your account isn't enabled for livestreaming! :(", "Your account needs to be verified.", new String[]{"Okay", "Help"});
                    if(option.equalsIgnoreCase("help")){
                        try {
                            Utilities.openURL(new URI("https://support.google.com/youtube/answer/171664?hl=en"));
                        } catch (URISyntaxException ex) {
                            Logging.getLogger().error(ex.getMessage(), ex);
                        }
                    }
                }).start();
                shownError = true;
            }
        }
        return null;
    }

    public static String getVideoID(YouTube youtube) throws IOException {
        try {
            YouTube.LiveBroadcasts.List broadcastList = youtube
                    .liveBroadcasts()
                    .list(Collections.singletonList("id"))
                    .setFields("items/id")
                    .setBroadcastType("all")
                    .setBroadcastStatus("active");

            return (String) broadcastList.execute().getItems().get(0).get("id");
        }
        catch (GoogleJsonResponseException e) {
            e.printStackTrace();
            if (Window.getFrame().isVisible() && !shownError) {
                new Thread(() -> {
                    String option = DialogBox.showDialogBox("Cannot connect to YouTube Chat!", "Your account isn't enabled for livestreaming! :(", "Your account needs to be verified.", new String[]{"Okay", "Help"});
                    if(option.equalsIgnoreCase("help")){
                        try {
                            Utilities.openURL(new URI("https://support.google.com/youtube/answer/171664?hl=en"));
                        } catch (URISyntaxException ex) {
                            Logging.getLogger().error(ex.getMessage(), ex);
                        }
                    }
                }).start();
                shownError = true;
            }
        }
        return null;
    }

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