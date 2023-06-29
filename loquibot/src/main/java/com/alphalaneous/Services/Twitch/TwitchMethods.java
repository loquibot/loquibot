package com.alphalaneous.Services.Twitch;

import java.util.ArrayList;
import java.util.Random;

public class TwitchMethods {

	public static String getFollowDate(String user) {
		return null;
	}

	public static String getFollowAge(String user) {
		return null;
	}

	public static long getFollowers() {
		return TwitchAPI.getFollowerCount();
	}

	public static String getRandomViewer() {

		Random random = new Random();
		int num = random.nextInt(TwitchAPI.viewerList.size() - 1);
		return TwitchAPI.viewerList.get(num);
	}

	@SuppressWarnings("unchecked")
	public static String[] getViewers() {
		return TwitchAPI.viewerList.toArray(String[]::new);
	}
}
