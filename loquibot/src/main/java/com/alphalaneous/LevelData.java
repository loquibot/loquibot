package com.alphalaneous;

import com.alphalaneous.Panels.LevelButton;
import com.alphalaneous.Panels.LevelsPanel;
import jdash.common.entity.GDLevel;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;

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
	private boolean viewership = true;
	private boolean featured;
	private boolean epic;
	private boolean containsVulgar;
	private boolean containsImage;
	private boolean analyzed = false;
	private boolean persist;
	private ImageIcon playerIcon;
	private LevelButton levelButton;

	public String getSimpleDifficulty(){
		if(getLevelData().isAuto()){
			return "AUTO";
		}
		if(getLevelData().isDemon()){
			return getLevelData().demonDifficulty().name().toUpperCase() + " DEMON";
		}
		return getLevelData().difficulty().name().toUpperCase();
	}

	public int getPosition(){
		int pos = 0;
		for(Component component : levelButton.getParent().getComponents()){
			if(component.equals(levelButton)){
				return pos;
			}
			pos++;
		}
		return -1;
	}

	public void remove(){

		int selected = LevelButton.selectedID;
		if(selected > getPosition() || selected == Requests.levels.size()-1){
			selected = selected - 1;
		}
		new Thread(() -> {
			while(savingLogs){
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			savingLogs = true;
			Requests.saveLogs(this);
			savingLogs = false;
		}).start();
		//LevelsPanel.removeButton(levelButton);
		Requests.levels.remove(this);
		LevelsPanel.refreshButtons();
		LevelsPanel.setSelect(selected);
	}

	public void setLevelButton(LevelButton levelButton){
		this.levelButton = levelButton;
	}

	public LevelButton getLevelButton(){
		return levelButton;
	}

	public GDLevel getLevelData(){
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
