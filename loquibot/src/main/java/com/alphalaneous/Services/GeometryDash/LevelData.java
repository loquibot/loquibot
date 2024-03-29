package com.alphalaneous.Services.GeometryDash;

import jdash.common.entity.GDLevel;

import javax.swing.*;

/**
 * @noinspection WeakerAccess
 */
public class LevelData {

	private String requester;

	public boolean isRepeated() {
		return isRepeated;
	}

	public void setRepeated(boolean repeated) {
		isRepeated = repeated;
	}

	private boolean isRepeated = false;
	private boolean viewership = true;
	private GDLevelExtra levelData;
	private String message;
	private String messageID;
	private String youtubeURL;
	private boolean featured;
	private boolean epic;
	private boolean containsVulgar;
	private boolean containsImage;
	private ImageIcon playerIcon;
	private boolean isYouTube;
	private boolean isKick;



	private String videoID;
	private String videoTitle;
	private String videoCreator;
	private String videoViews;

	private String displayName;

	public String getVideoID() {
		return videoID;
	}

	public void setVideoID(String videoID) {
		this.videoID = videoID;
	}

	public String getVideoTitle() {
		return videoTitle;
	}

	public void setVideoTitle(String videoTitle) {
		this.videoTitle = videoTitle;
	}

	public String getVideoCreator() {
		return videoCreator;
	}

	public void setVideoCreator(String videoCreator) {
		this.videoCreator = videoCreator;
	}

	public String getVideoViews() {
		return videoViews;
	}

	public void setVideoViews(String videoViews) {
		this.videoViews = videoViews;
	}
	public long getPlayerID() {
		return playerID;
	}

	public void setPlayerID(long playerID) {
		this.playerID = playerID;
	}

	private long playerID;

	public void setYouTube(boolean isYouTube){
		this.isYouTube = isYouTube;
	}
	public void setKick(boolean isKick){
		this.isKick = isKick;
	}

	public boolean isYouTube() {
		return isYouTube;
	}

	public boolean isKick() {
		return isKick;
	}


	public void setDisplayName(String displayName){
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getSimpleDifficulty(){
		if(getGDLevel().getLevel().isAuto()){
			return "AUTO";
		}
		if(getGDLevel().getLevel().isDemon()){
			return getGDLevel().getLevel().demonDifficulty().name().toUpperCase() + " DEMON";
		}
		return getGDLevel().getLevel().difficulty().name().toUpperCase();
	}

	public void setYoutubeURL(String url){
		this.youtubeURL = url;
	}

	public GDLevelExtra getGDLevel(){
		return levelData;
	}

	public void setLevelData(GDLevelExtra level){
		this.levelData = level;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessageID() {
		return messageID;
	}

	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}

	public String getRequester() {
		return requester;
	}

	public void setRequester(String requester) {
		this.requester = requester;
	}

	public boolean getFeatured() {
		return featured;
	}

	public boolean getEpic() {
		return epic;
	}

	public void setEpic(boolean epic) {
		this.epic = epic;
	}

	public boolean getContainsVulgar() {
		return containsVulgar;
	}

	public boolean getContainsImage() {
		return containsImage;
	}

	public String getYoutubeURL(){
		return youtubeURL;
	}

	public ImageIcon getPlayerIcon() {
		return playerIcon;
	}

	public void setPlayerIcon(ImageIcon playerIcon) {
		this.playerIcon = playerIcon;
	}

	public void setContainsVulgar() {
		this.containsVulgar = true;
	}

	public void setContainsImage() {
		this.containsImage = true;
	}

	public void setFeatured() {
		this.featured = true;
	}

	public boolean getViewership() {
		return viewership;
	}

	public void setViewership(boolean viewership) {
		this.viewership = viewership;
	}
}
