package com.alphalaneous.Services.YouTube;

import com.alphalaneous.Moderation.Moderation;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;

public class YouTubeScrape {

    public static YouTubeVideo getDirectVideo(String query) throws IOException {


        String link = "https://www.youtube.com/watch?v=" + query;
        String thumbnailURL = "https://i1.ytimg.com/vi/" + query + "/hqdefault.jpg";

        Document doc = Jsoup.connect(link).get();
        Elements elements = doc.getElementsByTag("script");
        String data = "";
        Elements metaElements = doc.getElementsByTag("meta");

        String time = "";
        for(Element element : metaElements){
            if(element.attributes().get("itemprop").equals("duration")){
                time = element.attributes().get("content");
                break;
            }
        }
        Duration dur = Duration.parse(time);
        long seconds = dur.getSeconds();

        for (Element element : elements) {
            if(element.data().startsWith("var ytInitialData")){
                data = element.data().substring("var ytInitialData = ".length(), element.data().length()-1);
                break;
            }
        }

        String otherData = "";
        for (Element element : elements) {
            if(element.data().startsWith("var ytInitialPlayerResponse")){
                otherData = element.data().substring("var ytInitialPlayerResponse = ".length(), element.data().length()-1);
                break;
            }
        }

        JSONObject jsonData = new JSONObject(data);

        JSONObject playerOverlays = jsonData.getJSONObject("playerOverlays");
        JSONObject playerOverlayRenderer = playerOverlays.getJSONObject("playerOverlayRenderer");

        JSONObject videoDetails = playerOverlayRenderer.getJSONObject("videoDetails");
        JSONObject playerOverlayVideoDetailsRenderer = videoDetails.getJSONObject("playerOverlayVideoDetailsRenderer");

        JSONObject titleObj = playerOverlayVideoDetailsRenderer.getJSONObject("title");
        String title = titleObj.getString("simpleText");

        //JSONArray keywords = videoDetails.getJSONArray("keywords");
        //System.out.println(keywords);

        //String shortDescription = videoDetails.getString("shortDescription");
        //System.out.println(shortDescription);

        //simpleText (string) -> description (object) -> playerMicroformatRenderer (object) -> microformat (object) -> playerConfig (object)

        JSONObject subtitle = playerOverlayVideoDetailsRenderer.getJSONObject("subtitle");

        JSONArray runs = subtitle.getJSONArray("runs");
        String username = runs.getJSONObject(0).getString("text");

        JSONObject otherJsonData = new JSONObject(otherData);
        //System.out.println(otherJsonData.toString(4));

        JSONObject microformat = otherJsonData.getJSONObject("microformat");
        JSONObject playerMicroformatRenderer = microformat.getJSONObject("playerMicroformatRenderer");
        JSONObject description = playerMicroformatRenderer.getJSONObject("description");
        String descriptionText = description.getString("simpleText");

        System.out.println(descriptionText.replace("\n", " ").trim());

        return new YouTubeVideo(title, username, query,"", thumbnailURL, 0, (int) seconds);
    }

    public static ArrayList<YouTubeVideo> searchYouTube(String query) throws IOException {

        String link = "https://www.youtube.com/results?search_query=" + URLEncoder.encode(query, StandardCharsets.UTF_8);

        Document doc = Jsoup.connect(link).get();
        Elements elements = doc.getElementsByTag("script");

        String data = "";

        for (Element element : elements) {
                if(element.data().startsWith("var ytInitialData")){
                    data = element.data().substring("var ytInitialData = ".length(), element.data().length()-1);
                    break;
                }
        }
        JSONObject jsonObject = new JSONObject(data);
        JSONObject contents = jsonObject.getJSONObject("contents");
        JSONObject twoColumnSearchResultsRenderer = contents.getJSONObject("twoColumnSearchResultsRenderer");
        JSONObject primaryContents = twoColumnSearchResultsRenderer.getJSONObject("primaryContents");
        JSONObject sectionListRenderer = primaryContents.getJSONObject("sectionListRenderer");
        JSONArray contents2 = sectionListRenderer.getJSONArray("contents");
        JSONObject video1 = contents2.getJSONObject(0);
        JSONObject itemSectionRenderer = video1.getJSONObject("itemSectionRenderer");
        JSONArray contents3 = itemSectionRenderer.getJSONArray("contents");

        ArrayList<YouTubeVideo> youTubeVideos = new ArrayList<>();
        for(Object object : contents3){

            JSONObject mainObject = (JSONObject)object;
            if(mainObject.has("videoRenderer")) {
                JSONObject videoRenderer = mainObject.getJSONObject("videoRenderer");
                JSONObject thumbnail = videoRenderer.getJSONObject("thumbnail");
                JSONObject lengthText = videoRenderer.getJSONObject("lengthText");
                String timeText = lengthText.getString("simpleText");

                int seconds = 0;
                for (var x: timeText.split(":")) {
                    seconds = seconds * 60 + Byte.parseByte(x);
                }
                JSONArray thumbnails = thumbnail.getJSONArray("thumbnails");
                JSONObject highResThumbnail = thumbnails.getJSONObject(thumbnails.length()-1);
                String thumbnailURL = highResThumbnail.getString("url").split("\\?")[0];
                JSONObject titleObj = videoRenderer.getJSONObject("title");
                JSONArray titleRuns = titleObj.getJSONArray("runs");
                JSONObject titleRunsArr0 = titleRuns.getJSONObject(0);
                String title = titleRunsArr0.getString("text");
                String videoID = videoRenderer.getString("videoId");
                JSONObject shortBylineText = videoRenderer.getJSONObject("shortBylineText");
                JSONObject viewCountText = videoRenderer.getJSONObject("viewCountText");
                long viewCount = 0;
                try {
                    viewCount = Long.parseLong(viewCountText.getString("simpleText").split(" ")[0].replace(",", ""));
                }
                catch (NumberFormatException ignored){
                }
                JSONArray runs = shortBylineText.getJSONArray("runs");
                JSONObject runsArr0 = runs.getJSONObject(0);
                String username = runsArr0.getString("text");
                youTubeVideos.add(new YouTubeVideo(title, username, videoID, "", thumbnailURL, viewCount, seconds));

            }
        }

        /*for(YouTubeVideo video : youTubeVideos){
            System.out.println(video.getTitle() + " | Uploaded by " + video.getUsername() + " | https://www.youtube.com/watch?v=" + video.getVideoID() + " | " + video.getThumbnailURL());

        }*/

        return youTubeVideos;
    }
}
