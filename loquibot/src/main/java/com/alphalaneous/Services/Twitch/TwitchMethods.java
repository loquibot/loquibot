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
		TwitchAPI.setAllViewers();
		Random random = new Random();
		int num = random.nextInt(TwitchAPI.allViewers.size() - 1);
		return TwitchAPI.allViewers.get(num);
	}

	@SuppressWarnings("unchecked")
	public static String[] getViewers() {
		ArrayList<String> allViewers = (ArrayList<String>) TwitchAPI.allViewers.clone();
		String[] array = new String[allViewers.size()];
		for (int j = 0; j < allViewers.size(); j++) {
			array[j] = allViewers.get(j);
		}
		return array;
	}

	public static void reloadViewers() {
		TwitchAPI.setAllViewers();
	}

	public static void checkloquibot() {

	}
}
