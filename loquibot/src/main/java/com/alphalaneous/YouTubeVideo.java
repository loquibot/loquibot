package com.alphalaneous;

public class YouTubeVideo {

    private final String title;
    private final String username;
    private final String videoID;
    private final String thumbnailURL;
    private final long viewCount;

    YouTubeVideo(String title, String username, String videoID, String thumbnailURL, long viewCount){
        this.title = title;
        this.thumbnailURL = thumbnailURL;
        this.videoID = videoID;
        this.username = username;
        this.viewCount = viewCount;
    }

    public String getTitle() {
        return title;
    }

    public String getUsername() {
        return username;
    }

    public String getVideoID() {
        return videoID;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public long getViewCount(){
        return viewCount;
    }

}
