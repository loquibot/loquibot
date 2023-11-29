package com.alphalaneous.Interactive.TwitchExclusive.ChannelPoints;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ChannelPointReward {

	private final long cost;
	private final String title;
	private final String id;

	private final Color bgColor;
	private final String prompt;
	private final URL imgURL;
	private final Icon icon;
	private final boolean defaultIcon;

	public ChannelPointReward(String id, String title, String prompt, long cost, Color bgColor, URL imgURL, Icon icon, boolean defaultIcon) {
		this.id = id;
		this.title = title;
		this.prompt = prompt;
		this.cost = cost;
		this.bgColor = bgColor;
		this.imgURL = imgURL;
		this.icon = icon;
		this.defaultIcon = defaultIcon;
	}

	public long getCost() {
		return cost;
	}

	public String getTitle() {
		return title;
	}

	public String getId(){
		return id;
	}

	public Color getBgColor() {
		return bgColor;
	}

	public String getPrompt() {
		return prompt;
	}

	public URL getImgURL() {
		return imgURL;
	}

	public Icon getIcon() {
		return icon;
	}

	public boolean isDefaultIcon() {
		return defaultIcon;
	}
}
