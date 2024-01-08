package com.alphalaneous.Services.YouTube;

import com.alphalaneous.Pages.SettingsSubPages.AccountsPage;
import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.SettingsHandler;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.common.collect.Lists;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class YouTubeAccount {

    public static String name;
    public static String ID;
    public static BufferedImage profileImage;
    public static YouTube youtube;
    public static Credential credential;
    private static final List<String> scopes = Lists.newArrayList(YouTubeScopes.YOUTUBE, YouTubeScopes.YOUTUBE_READONLY, YouTubeScopes.YOUTUBE_FORCE_SSL);
    public static String liveChatId;

    public static YouTube getYouTube(){
        return youtube;
    }

    public static void setCredential(boolean refresh, boolean prompt) throws IOException {

        if(prompt){
            //todo show pop up asking for login
        }

        credential = YouTubeAuth.authorize(scopes, "YouTubeCredentials", refresh);
        if(credential != null) {
            SettingsHandler.writeSettings("isYouTubeLoggedIn", String.valueOf(true));
            setInfo();
            AccountsPage.setYouTubeAccountInfo();
        }
        else{
            System.out.println("failed");
            //todo prompt that the login failed
        }
    }

    public static void setInfo(){

        try {
            youtube = new YouTube.Builder(YouTubeAuth.HTTP_TRANSPORT, YouTubeAuth.JSON_FACTORY, credential)
                    .setApplicationName("loquibot-chat").build();

            YouTube.Channels.List request = youtube.channels()
                    .list(List.of("snippet"));
            ChannelListResponse response = request.setMine(true).execute();

            String liveChatId = GetLiveChatID.getLiveChatId(YouTubeAccount.getYouTube());
            if (liveChatId != null) YouTubeAccount.liveChatId = liveChatId;

            JSONObject youtubeObject = new JSONObject(response);

            JSONArray items = youtubeObject.getJSONArray("items");
            JSONObject item1 = items.getJSONObject(0);
            JSONObject snippet = item1.getJSONObject("snippet");
            ID = item1.getString("id");

            String title = snippet.getString("title");
            JSONObject thumbnails = snippet.getJSONObject("thumbnails");
            JSONObject medium = thumbnails.getJSONObject("medium");
            String mediumURL = medium.getString("url");

            name = title;

            profileImage = ImageIO.read(new URL(mediumURL));

        } catch (Exception e) {
            Logging.getLogger().error(e.getMessage(), e);

        }
    }
}
