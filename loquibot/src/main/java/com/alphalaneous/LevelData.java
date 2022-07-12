package com.alphalaneous;

import com.alphalaneous.Swing.Components.LevelButton;
import jdash.common.entity.GDLevel;

import javax.swing.*;

/**
 * @noinspection WeakerAccess
 */
public class LevelData {

	private static boolean savingLogs = false;

	private String requester;
	private GDLevel levelData;
	private int password;
	private String message;
	private String messageID;
	private String youtubeURL;
	private boolean viewership = true;
	private boolean featured;
	private boolean epic;
	private boolean containsVulgar;
	private boolean containsImage;
	private boolean analyzed = false;
	private boolean persist;
	private ImageIcon playerIcon;
	private LevelButton levelButton;
	private boolean isYouTube;
	private String displayName;

	public void setYouTube(boolean isYouTube){
		this.isYouTube = isYouTube;
	}

	public boolean isYouTube() {
		return isYouTube;
	}

	public void setDisplayName(String displayName){
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getSimpleDifficulty(){
		if(getGDLevel().isAuto()){
			return "AUTO";
		}
		if(getGDLevel().isDemon()){
			return getGDLevel().demonDifficulty().name().toUpperCase() + " DEMON";
		}
		return getGDLevel().difficulty().name().toUpperCase();
	}

	public void setYoutubeURL(String url){
		this.youtubeURL = url;
	}

	public void setLevelButton(LevelButton levelButton){
		this.levelButton = levelButton;
	}

	public LevelButton getLevelButton(){
		return levelButton;
	}

	public GDLevel getGDLevel(){
		return levelData;
	}

	public void setLevelData(GDLevel level){
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

	public int getPassword() {
		return password;
	}

	public void setPassword(int password) {
		this.password = password;
	}

	public boolean getViewership() {
		return viewership;
	}

	public void setViewership(boolean viewership) {
		this.viewership = viewership;
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

	public boolean getAnalyzed() {
		return analyzed;
	}

	public boolean getNotPersist() {
		return !persist;
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

	public void setAnalyzed() {
		this.analyzed = true;
	}

	public void setContainsVulgar() {
		this.containsVulgar = true;
	}

	public void setContainsImage() {
		this.containsImage = true;
	}

	public void setPersist(boolean persist) {
		this.persist = persist;
	}


	public void setFeatured() {
		this.featured = true;
	}
}
