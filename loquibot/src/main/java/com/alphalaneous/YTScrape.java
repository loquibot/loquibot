package com.alphalaneous;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class YTScrape {

    public static ArrayList<YouTubeVideo> searchYouTube(String query) throws IOException {
        Document doc = Jsoup.connect("https://www.youtube.com/results?search_query=" + URLEncoder.encode(query, StandardCharsets.UTF_8)).get();
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
            //System.out.println(mainObject.toString(2));
            if(mainObject.has("videoRenderer")) {
                JSONObject videoRenderer = mainObject.getJSONObject("videoRenderer");
                JSONObject thumbnail = videoRenderer.getJSONObject("thumbnail");
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
                youTubeVideos.add(new YouTubeVideo(title, username, videoID, thumbnailURL, viewCount));

            }
        }

        /*for(YouTubeVideo video : youTubeVideos){
            System.out.println(video.getTitle() + " | Uploaded by " + video.getUsername() + " | https://www.youtube.com/watch?v=" + video.getVideoID() + " | " + video.getThumbnailURL());

        }*/

        return youTubeVideos;
    }
}
