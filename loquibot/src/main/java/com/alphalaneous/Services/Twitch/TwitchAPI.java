package com.alphalaneous.Services.Twitch;

import com.alphalaneous.ChatBot.ServerBot;
import com.alphalaneous.Services.GeometryDash.RequestFunctions;
import com.alphalaneous.Services.GeometryDash.Requests;
import com.alphalaneous.Images.Assets;
import com.alphalaneous.Interactive.ChannelPoints.ChannelPointReward;
import com.alphalaneous.Main;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Swing.BrowserWindow;
import com.alphalaneous.Swing.Components.LevelButton;
import com.alphalaneous.Swing.Components.LevelDetailsPanel;
import com.alphalaneous.Settings.Account;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.Utils.Utilities;
import com.alphalaneous.Windows.Window;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.domain.Chatter;
import com.github.twitch4j.helix.domain.ChattersList;
import com.github.twitch4j.tmi.domain.Chatters;
import com.github.twitch4j.util.PaginationUtil;
import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.auth.Scopes;
import com.netflix.hystrix.HystrixCommand;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
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
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class TwitchAPI {

	public static TwitchClient twitchClient;
	public static AtomicBoolean success = new AtomicBoolean(false);
	@SuppressWarnings("SpellCheckingInspection")
	static final String clientID = "fzwze6vc6d2f7qodgkpq2w8nnsz3rl";

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

	private static List<Chatter> getViewers(){

		try {
			if (twitchClient != null) {
				return PaginationUtil.getPaginated(
						cursor -> {
							try {
								return twitchClient
										.getHelix()
										.getChatters(SettingsHandler.getSettings("oauth").asString(),
												String.valueOf(TwitchAccount.id),
												String.valueOf(TwitchAccount.id),
												1000,
												cursor).execute();
							} catch (Exception e) {
								e.printStackTrace();
								return null;
							}
						},
						ChattersList::getChatters,
						call -> call.getPagination() != null ? call.getPagination().getCursor() : null
				);
			}
		}
		catch (Exception e){
			TwitchAPI.setOauth();
		}
		return null;
	}



	public static ArrayList<String> viewerList;

	public static boolean isViewer(String username){
		if(username != null) {
			for (String chatter : viewerList) {
				if (chatter != null) {
					if (chatter.equalsIgnoreCase(username)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@SuppressWarnings("InfiniteLoopStatement")
	public static void checkViewers() {
			while (true) {
				try {
					List<Chatter> viewers = getViewers();
					if(viewers != null) {
						/*for (int i = 0; i < RequestsTab.getQueueSize(); i++) {
							RequestsTab.getRequest(i).setViewership(false);
						}

						TwitchAPI.viewerList = (ArrayList<String>) viewers.stream().map(Chatter::getUserLogin).collect(Collectors.toList());

						for (int k = 0; k < RequestsTab.getQueueSize(); k++) {

							if(TwitchChatListener.SelfDestructingViewer.containsViewer(RequestsTab.getRequest(k).getRequester())){
								RequestsTab.getRequest(k).setViewership(true);
								TwitchAPI.viewerList.add(RequestsTab.getRequest(k).getRequester());
							}
						}

						/*if (SettingsHandler.getSettings("removeIfOffline").asBoolean()) {
							for (int i = 0; i < RequestsTab.getQueueSize(); i++) {
								if (RequestsTab.getRequest(i).isMarkedForRemoval()) {
									RequestsTab.getRequest(i).removeSelfViewer();
									i--;
								}
							}
							RequestsTab.updateLevelsPanel();
						}

						for (String s : TwitchAPI.viewerList) {
							String viewer = s.toString().replaceAll("\"", "");

							if (SettingsHandler.getSettings("removeIfOffline").asBoolean()) {
								for (LevelButton button : Requests.getRemovedForOffline()) {
									if (button.getLevelData().getRequester().equalsIgnoreCase(viewer)) {
										if (Requests.getPosFromID(button.getID()) == -1) {
											RequestsTab.addRequest(button);
											Requests.removeFromRemovedForOffline(button);
											if (RequestsTab.getQueueSize() == 1) {
												RequestsTab.getLevelsPanel().setSelect(0);
												LevelDetailsPanel.setPanel(RequestsTab.getRequest(0).getLevelData());
											}
										}
									}
								}
							}
						}

						RequestsTab.updateLevelsPanel();
						RequestFunctions.saveFunction();
						Window.setTitle("loquibot - " + RequestsTab.getQueueSize());
						 */
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				Utilities.sleep(60000);
			}
	}

	private static final ArrayList<Long> isFollowingCache = new ArrayList<>();
	private static final HashMap<String, String> userIDCache = new HashMap<>();

	public static boolean isNotFollowing(String user, long ID) {
		if(isFollowingCache.contains(ID)){
			return false;
		}
		if(ID == -1) return false;
		else {
			try {
				JSONObject isFollowing = twitchAPI("https://api.twitch.tv/helix/users/follows?from_id=" + ID + "&to_id=" + getUserID());
				System.out.println(isFollowing.toString());
				if (user.equalsIgnoreCase(SettingsHandler.getSettings("channel").asString())) {
					return false;
				}
				if (isFollowing != null) {
					String str = isFollowing.get("total").toString();
					if (str.equalsIgnoreCase("1")) {
						isFollowingCache.add(ID);
						return false;
					}
				}
			} catch (Exception e) {
				Main.sendMessage("🔴 | @" + user + " failed to check following status.");
			}
		}
		return true;
	}

	public static String fetchURL(String url) {
		return fetchURL(url, false);
	}

	public static String fetchURL(String url, boolean keepLines) {
		StringBuilder response = new StringBuilder();
		try {
			URL ids = new URL(url);
			Scanner s = new Scanner(ids.openStream());
			while (s.hasNextLine()) {
				if(keepLines) response.append(s.nextLine()).append("\n");
				else response.append(s.nextLine()).append(" ");
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
		try {
			return nameObj.getJSONArray("data").getJSONObject(0).get("login").toString().replaceAll("\"", "");
		}
		catch (JSONException e){
			e.printStackTrace();
			return null;
		}

	}

	public static boolean isMod(String username){

		String id = getIDs(username);

		JSONObject moderators = twitchAPI("https://api.twitch.tv/helix/moderation/moderators?broadcaster_id=" + TwitchAccount.id + "&user_id=" + id);
		if(moderators == null) return false;
		return moderators.getJSONArray("data").length() > 0;
	}

	public static boolean isLoquiMod(){
		return isMod("loquibot");
	}

	public static JSONObject getChannelInfo(){
		return twitchAPI("https://api.twitch.tv/helix/channels?broadcaster_id=" + TwitchAccount.id);
	}

	private static String sendTwitchRequest(String URL, String data){
		try {
			CloseableHttpClient http = HttpClientBuilder.create().build();
			HttpPatch updateRequest = new HttpPatch(URL);
			updateRequest.setHeader("Authorization", "Bearer " + SettingsHandler.getSettings("oauth").asString());
			updateRequest.setHeader("Client-ID", clientID);
			updateRequest.setHeader("Content-Type", "application/json");
			updateRequest.setEntity(new StringEntity(data));
			HttpResponse response = http.execute(updateRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode != 204) {
				if(statusCode >= 500 && statusCode < 600){
					return "Internal Twitch Error";
				}
				else if(statusCode >= 400 && statusCode < 500 ) {
					return "Please refresh your Twitch account in Settings > Accounts to use this.";
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	private static JSONObject twitchAPI(String URL) {

		try {
			URL url = new URL(URL);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("Authorization", "OAuth " + SettingsHandler.getSettings("oauth").asString());
			conn.setRequestProperty("Client-ID", clientID);
			conn.setRequestProperty("Authorization", "Bearer " + SettingsHandler.getSettings("oauth").asString());
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			return new JSONObject(br.readLine());
		}
		catch (Exception e){
			setOauth();
			e.printStackTrace();
			return null;
		}
	}

	public static JSONObject getInfo() {

		try {
			JSONObject userID;
			try {
				userID = twitchAPI("https://api.twitch.tv/helix/users?login=" + SettingsHandler.getSettings("channel").asString());
			} catch (JSONException e) {
				e.printStackTrace();
				SettingsHandler.writeSettings("channel", Objects.requireNonNull(getChannel()));
				userID = twitchAPI("https://api.twitch.tv/helix/users?login=" + SettingsHandler.getSettings("channel").asString());
			}
			if(userID == null){
				TwitchAPI.setOauth();
				return getInfo();
			}

			return userID.getJSONArray("data").getJSONObject(0);
		}
		catch (Exception e){
			TwitchAPI.setOauth();
		}
		return getInfo();
	}

	private static JSONObject user;

	public static JSONObject getUser(){
		return user;
	}

	public static String getUserID(){
		if(user == null){
			setUser(SettingsHandler.getSettings("channel").asString());
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
			conn.setRequestProperty("Authorization", "OAuth " + SettingsHandler.getSettings("oauth").asString());
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String x = br.readLine();
			return new JSONObject(x).get("client_id").toString().replace("\"", "");
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static boolean oauthOpen = false;
	public static void setOauth() {
		success.set(false);
		try {
			Twitch twitch = new Twitch();
			URI callbackUri = new URI("http://localhost:23522");

			twitch.setClientId(clientID);

			URI authUrl = new URI(twitch.auth().getAuthenticationUrl(
					twitch.getClientId(), callbackUri, Scopes.USER_READ
			//) + "chat:edit+channel:moderate+channel:read:redemptions+channel:read:subscriptions+moderation:read+channel:manage:broadcast+chat:read+user_read+moderator:manage:announcements+moderator:manage:banned_users+moderator:manage:chat_messages&force_verify=true");
			) + "chat:edit+channel:moderate+channel:read:redemptions+channel:read:subscriptions+moderation:read+channel:manage:broadcast+chat:read+user_read+moderator:read:chatters&force_verify=true");
			authUrl = new URI(authUrl.toString().replace("https://api.twitch.tv/kraken/", "https://id.twitch.tv/"));
			BrowserWindow browserWindow = new BrowserWindow(authUrl.toString());
			oauthOpen = true;
			if (twitch.auth().awaitAccessToken()) {
				SettingsHandler.writeSettings("oauth", twitch.auth().getAccessToken());
				SettingsHandler.writeSettings("channel", Objects.requireNonNull(getChannel()));
				TwitchAccount.setInfo(true);
				Account.refreshTwitch(SettingsHandler.getSettings("channel").asString(), true);
				//Main.refreshBot();
				success.set(true);
				oauthOpen = false;
				ServerBot.disconnect();
			} else {
				System.out.println(twitch.auth().getAuthenticationError());
				oauthOpen = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			oauthOpen = false;
		}
	}

	public static String getBotOauth(){
		try {
			Twitch twitch = new Twitch();
			URI callbackUri = new URI("http://localhost:23522");

			twitch.setClientId(clientID);

			URI authUrl = new URI(twitch.auth().getAuthenticationUrl(
					twitch.getClientId(), callbackUri, Scopes.USER_READ
			) + "chat:edit+channel:moderate+channel:read:redemptions+channel:read:subscriptions+moderation:read+channel:manage:broadcast+chat:read+user_read+moderator:manage:announcements+moderator:manage:banned_users+moderator:manage:chat_messages&force_verify=true");
			authUrl = new URI(authUrl.toString().replace("https://api.twitch.tv/kraken/", "https://id.twitch.tv/"));
			BrowserWindow browserWindow = new BrowserWindow(authUrl.toString());
			oauthOpen = true;

			if (twitch.auth().awaitAccessToken()) {
				oauthOpen = false;
				return twitch.auth().getAccessToken();
			} else {
				System.out.println(twitch.auth().getAuthenticationError());
				oauthOpen = false;
			}
		} catch (Exception e) {
			oauthOpen = false;
			e.printStackTrace();
		}
		return null;
	}

	public static String getOauth() {
		try {
			Twitch twitch = new Twitch();
			URI callbackUri = new URI("http://localhost:23522");

			twitch.setClientId(clientID);

			URI authUrl = new URI(twitch.auth().getAuthenticationUrl(
					twitch.getClientId(), callbackUri, Scopes.USER_READ
			) + "chat:edit+channel:moderate+channel:read:redemptions+channel:read:subscriptions+moderation:read+channel:manage:broadcast+chat:read+user_read&force_verify=true");
			authUrl = new URI(authUrl.toString().replace("https://api.twitch.tv/kraken/", "https://id.twitch.tv/"));
			BrowserWindow browserWindow = new BrowserWindow(authUrl.toString());
			oauthOpen = true;

			if (twitch.auth().awaitAccessToken()) {
				oauthOpen = false;
				return twitch.auth().getAccessToken();
			} else {
				System.out.println(twitch.auth().getAuthenticationError());
				oauthOpen = false;
			}
		} catch (Exception e) {
			oauthOpen = false;
			e.printStackTrace();
		}
		return null;
	}

	public static String setTitle(String title){
		JSONObject newChannelInfo = new JSONObject();
		newChannelInfo.put("title", title);
		return sendTwitchRequest("https://api.twitch.tv/helix/channels?broadcaster_id=" + TwitchAccount.id, newChannelInfo.toString());
	}
	public static String setGame(String game){
		JSONObject newChannelInfo = new JSONObject();
		JSONObject gameInfo = twitchAPI("https://api.twitch.tv/helix/games?name=" + URLEncoder.encode(game, StandardCharsets.UTF_8));
		assert gameInfo != null;
		if(gameInfo.getJSONArray("data").length() == 0){
			return "no_game";
		}
		String gameID = gameInfo.getJSONArray("data").getJSONObject(0).getString("id");

		newChannelInfo.put("game_id", gameID);
		return sendTwitchRequest("https://api.twitch.tv/helix/channels?broadcaster_id=" + TwitchAccount.id, newChannelInfo.toString());
	}
}
