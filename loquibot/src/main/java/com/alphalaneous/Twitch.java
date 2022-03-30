package com.alphalaneous;

import java.util.ArrayList;
import java.util.Random;

public class Twitch {

	public static String getFollowDate(String user) {
		return null;
	}

	public static String getFollowAge(String user) {
		return null;
	}

	public static long getFollowers() {
		return APIs.getFollowerCount();
	}

	public static String getRandomViewer() {
		APIs.setAllViewers();
		Random random = new Random();
		int num = random.nextInt(APIs.allViewers.size() - 1);
		return APIs.allViewers.get(num);
	}

	@SuppressWarnings("unchecked")
	public static String[] getViewers() {
		ArrayList<String> allViewers = (ArrayList<String>) APIs.allViewers.clone();
		String[] array = new String[allViewers.size()];
		for (int j = 0; j < allViewers.size(); j++) {
			array[j] = allViewers.get(j);
		}
		return array;
	}

	public static void reloadViewers() {
		APIs.setAllViewers();
	}

	public static void checkloquibot() {

	}
}
