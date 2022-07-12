package com.alphalaneous.Services.YouTube;

import javax.swing.*;

public class YouTubeVideo {

    private final String title;
    private final String username;
    private final String videoID;
    private final String thumbnailURL;
    private final int time;
    private ImageIcon image;
    private String requester;
    private final long viewCount;

    YouTubeVideo(String title, String username, String videoID, String thumbnailURL, long viewCount, int time){
        this.title = title;
        this.thumbnailURL = thumbnailURL;
        this.videoID = videoID;
        this.username = username;
        this.viewCount = viewCount;
        this.time = time;
    }

    public void setRequester(String requester){
        this.requester = requester;
    }

    public void setImage(ImageIcon image){
        this.image = image;
    }

    public ImageIcon getImage(){
        return image;
    }

    public String getRequester() {
        return requester;
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

    public int getDuration() {
        return time;
    }
}
