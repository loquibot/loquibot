package com.alphalaneous;

import com.alphalaneous.Panels.LevelsPanel;
import com.alphalaneous.SettingsPanels.AccountSettings;
import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.auth.Scopes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.netty.buffer.Unpooled.wrappedBuffer;

public class APIs {

	static ArrayList<String> allViewers = new ArrayList<>();
	static ArrayList<String> allMods = new ArrayList<>();
	static ArrayList<String> allVIPs = new ArrayList<>();
	static AtomicBoolean success = new AtomicBoolean(false);

	static void setAllViewers() {
		try {
			URL url = new URL("https://tmi.twitch.tv/group/user/" + Settings.getSettings("channel").asString().toLowerCase() + "/chatters");
			URLConnection conn = url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String x;
			while ((x = br.readLine()) != null) {
				builder.append(x).append("\n");
			}
			JSONObject viewers = new JSONObject(builder.toString());
			String[] types = {"broadcaster", "vips", "staff", "moderators", "admins", "global_mods", "viewers"};
			allViewers.clear();
			for (String type : types) {
				JSONArray viewerList = viewers.getJSONObject("chatters").getJSONArray(type);
				for (int i = 0; i < viewerList.length(); i++) {
					String viewer = viewerList.get(i).toString().replaceAll("\"", "");
					allViewers.add(viewer);
				}
			}
			allVIPs.clear();
			allMods.clear();
			JSONArray viewerListMods = viewers.getJSONObject("chatters").getJSONArray("moderators");
			for (int i = 0; i < viewerListMods.length(); i++) {
				String viewer = viewerListMods.get(i).toString().replaceAll("\"", "");
				allMods.add(viewer);
			}
			JSONArray viewerListVIPs = viewers.getJSONObject("chatters").getJSONArray("vips");
			for (int i = 0; i < viewerListVIPs.length(); i++) {
				String viewer = viewerListVIPs.get(i).toString().replaceAll("\"", "");
				allVIPs.add(viewer);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<ChannelPointReward> getChannelPoints() {
		JSONArray awards = Objects.requireNonNull(twitchAPI("https://api.twitch.tv/helix/channel_points/custom_rewards?broadcaster_id=" + getUserID())).getJSONArray("data");
		ArrayList<ChannelPointReward> rewards = new ArrayList<>();
		for (int i = 0; i < awards.length(); i++) {
			String title = awards.getJSONObject(i).getString("title");
			String prompt = awards.getJSONObject(i).getString("prompt");
			long cost = awards.getJSONObject(i).getLong("cost");
			Color bgColor = Color.decode(awards.getJSONObject(i).getString("background_color"));
			String imgURL;
			ImageIcon image;
			boolean defaultIcon = false;
			try {
				imgURL = awards.getJSONObject(i).getJSONObject("image").getString("url_2x");
				URL url = new URL(imgURL);
				BufferedImage c = ImageIO.read(url);
				image = new ImageIcon(new ImageIcon(c).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));

			} catch (JSONException | IOException e) {
				imgURL = "https://static-cdn.jtvnw.net/custom-reward-images/default-2.png";
				image = Assets.channelPoints;
				defaultIcon = true;
			}

			ChannelPointReward reward = null;
			try {
				reward = new ChannelPointReward(title, prompt, cost, bgColor, new URL(imgURL), image, defaultIcon);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			if (reward != null) {
				rewards.add(reward);
			}
		}
		return rewards;
	}

	static void checkViewers() {
			while (true) {
				try {
					URL url = new URL("https://tmi.twitch.tv/group/user/" + Settings.getSettings("channel").asString().toLowerCase() + "/chatters");
					URLConnection conn = url.openConnection();
					BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					StringBuilder builder = new StringBuilder();
					String x;
					while ((x = br.readLine()) != null) {
						builder.append(x).append("\n");
					}
					JSONObject viewers = new JSONObject(builder.toString());
					String[] types = {"broadcaster", "vips", "staff", "moderators", "admins", "global_mods", "viewers"};
					for (int i = 0; i < Requests.levels.size(); i++) {
						LevelsPanel.getButton(i).setViewership(false);
					}

					for (String type : types) {
						if (viewers.get("chatters") != null) {
							JSONArray viewerList = viewers.getJSONObject("chatters").getJSONArray(type);
							for (int i = 0; i < viewerList.length(); i++) {
								String viewer = viewerList.get(i).toString().replaceAll("\"", "");
								for (int k = 0; k < Requests.levels.size(); k++) {
									if (LevelsPanel.getButton(k).getRequester().equalsIgnoreCase(viewer)) {
										LevelsPanel.getButton(k).setViewership(true);
									}
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(120000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
	}

	private static final ArrayList<String> isFollowingCache = new ArrayList<>();
	private static final HashMap<String, String> userIDCache = new HashMap<>();

	static boolean isFollowing(String user) {
		if(isFollowingCache.contains(user)){
			return true;
		}
		else {
			try {
				JSONObject isFollowing = twitchAPI("https://api.twitch.tv/helix/users/follows?from_id=" + getIDs(user) + "&to_id=" + getUserID());
				if (user.equalsIgnoreCase(Settings.getSettings("channel").asString())) {
					return true;
				}
				if (isFollowing != null) {
					String str = isFollowing.get("total").toString();
					System.out.println(str);
					if (str.equalsIgnoreCase("1")) {
						isFollowingCache.add(user);
						return true;
					}
				}
			} catch (Exception e) {
				Main.sendMessage("🔴 | @" + user + " failed to check following status.");
			}
		}
		return false;
	}

	public static String fetchURL(String url) {
		StringBuilder response = new StringBuilder();
		try {
			URL ids = new URL(url);
			Scanner s = new Scanner(ids.openStream());
			while (s.hasNextLine()) {
				response.append(s.nextLine()).append(" ");
			}
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response.toString();
	}

	public static long getFollowerCount() {
		JSONObject followCountJson;
		followCountJson = twitchAPI("https://api.twitch.tv/helix/users/follows?to_id=" + getUserID());
		assert followCountJson != null;
		String total = followCountJson.get("total").toString();
		return Long.parseLong(total);
	}

	public static String getChannel() {

		JSONObject nameObj = twitchAPI("https://api.twitch.tv/helix/users");
		assert nameObj != null;
		System.out.println(nameObj);
		try {
			return nameObj.getJSONArray("data").getJSONObject(0).get("display_name").toString().replaceAll("\"", "");
		}
		catch (JSONException e){
			e.printStackTrace();
			return null;
		}

	}

	public static String getPFP() {

		JSONObject nameObj = twitchAPI("https://api.twitch.tv/helix/users");
		assert nameObj != null;

		//String url = String.valueOf(nameObj.asObject().get("data").asArray().get(0).asObject().get("profile_image_url")).replaceAll("\"", "");

		return nameObj.getJSONArray("data").getJSONObject(0).get("profile_image_url").toString().replaceAll("\"", "");

	}

	private static JSONObject twitchAPI(String URL) {

		try {
			URL url = new URL(URL);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("Authorization", "OAuth " + Settings.getSettings("oauth").asString());
			conn.setRequestProperty("Client-ID", "fzwze6vc6d2f7qodgkpq2w8nnsz3rl");
			conn.setRequestProperty("Authorization", "Bearer " + Settings.getSettings("oauth").asString());
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			return new JSONObject(br.readLine());
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public static JSONObject getInfo() {

		JSONObject userID;
		try {
			userID = twitchAPI("https://api.twitch.tv/helix/users?login=" + Settings.getSettings("channel").asString());
		} catch (JSONException e) {
			e.printStackTrace();
			Settings.writeSettings("channel", getChannel());
			userID = twitchAPI("https://api.twitch.tv/helix/users?login=" + Settings.getSettings("channel").asString());
		}
		assert userID != null;

		return userID.getJSONArray("data").getJSONObject(0);
	}

	private static JSONObject user;

	public static JSONObject getUser(){
		return user;
	}

	public static String getUserID(){
		if(user == null){
			setUser(Settings.getSettings("channel").asString());
		}
		return user.getJSONArray("data").getJSONObject(0).get("id").toString().replaceAll("\"", "");
	}

	public static void setUser(String username) {
		JSONObject userID = twitchAPI("https://api.twitch.tv/helix/users?login=" + username.toLowerCase());
		assert userID != null;
		user = userID;
	}

	public static String getIDs(String username) {

		if(userIDCache.containsKey(username)){
			return userIDCache.get(username);
		}
		else {
			JSONObject userIDObject = twitchAPI("https://api.twitch.tv/helix/users?login=" + username.toLowerCase());
			assert userIDObject != null;

			String userID = userIDObject.getJSONArray("data").getJSONObject(0).get("id").toString().replaceAll("\"", "");

			userIDCache.put(username, userID);

			return userID;
		}
	}

	@SuppressWarnings("unused")
	public static String getClientID() {
		try {
			URL url = new URL("https://id.twitch.tv/oauth2/validate");
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("Authorization", "OAuth " + Settings.getSettings("oauth").asString());
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String x = br.readLine();
			return new JSONObject(x).get("client_id").toString().replace("\"", "");
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static void setOauth() {
		success.set(false);
		try {
			Twitch twitch = new Twitch();
			URI callbackUri = new URI("http://127.0.0.1:23522");

			twitch.setClientId("fzwze6vc6d2f7qodgkpq2w8nnsz3rl");
			URI authUrl = new URI(twitch.auth().getAuthenticationUrl(
					twitch.getClientId(), callbackUri, Scopes.USER_READ
			) + "chat:edit+channel:moderate+channel:read:redemptions+channel:read:subscriptions+chat:read+user_read&force_verify=true");
			Runtime rt = Runtime.getRuntime();
			rt.exec("rundll32 url.dll,FileProtocolHandler " + authUrl);
			if (twitch.auth().awaitAccessToken()) {
				Settings.writeSettings("oauth", twitch.auth().getAccessToken());
				Settings.writeSettings("channel", getChannel());
				AccountSettings.refreshTwitch(Settings.getSettings("channel").asString());
				Main.refreshBot();
				TwitchAccount.setInfo();
				success.set(true);
			} else {
				System.out.println(twitch.auth().getAuthenticationError());

			}
			if (!GDBoardBot.initialConnect) {
				GDBoardBot.initialConnect = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
