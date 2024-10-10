package com.alphalaneous.Services.Twitch;

import com.alphalaneous.Components.DialogBox;
import com.alphalaneous.Interactive.TwitchExclusive.ChannelPoints.ChannelPointReward;
import com.alphalaneous.Interfaces.Function;
import com.alphalaneous.Main;
import com.alphalaneous.Utilities.*;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.ChannelInformation;
import com.github.twitch4j.helix.domain.Game;
import com.github.twitch4j.helix.domain.GameList;
import com.github.twitch4j.helix.domain.InboundFollowers;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.List;

public class TwitchAPI {

	public static TwitchClient twitchClient;
	@SuppressWarnings("SpellCheckingInspection")
	static final String clientID = "fzwze6vc6d2f7qodgkpq2w8nnsz3rl";

	private static final HashMap<String, String> userIDCache = new HashMap<>();


	public static ArrayList<ChannelPointReward> getChannelPoints() {
		JSONArray awards = Objects.requireNonNull(twitchAPI("https://api.twitch.tv/helix/channel_points/custom_rewards?broadcaster_id=" + getUserID())).getJSONArray("data");
		ArrayList<ChannelPointReward> rewards = new ArrayList<>();
		for (int i = 0; i < awards.length(); i++) {
			String id = awards.getJSONObject(i).getString("id");
			String title = awards.getJSONObject(i).getString("title");
			String prompt = awards.getJSONObject(i).getString("prompt");
			long cost = awards.getJSONObject(i).getLong("cost");
			Color bgColor = Color.decode(awards.getJSONObject(i).getString("background_color"));
			String imgURL;
			ImageIcon image = null;
			boolean defaultIcon = false;
			try {
				imgURL = awards.getJSONObject(i).getJSONObject("image").getString("url_2x");
				image = new ImageIcon(Assets.downloadAsset(imgURL).getScaledInstance(30, 30, Image.SCALE_SMOOTH));

			} catch (JSONException | IOException e) {
				imgURL = "https://static-cdn.jtvnw.net/custom-reward-images/default-2.png";
				try {
					image = new ImageIcon(Assets.downloadAsset(imgURL).getScaledInstance(30, 30, Image.SCALE_SMOOTH));
					defaultIcon = true;
				}
				catch (Exception f){
					Logging.getLogger().error(f.getMessage(), f);
				}
			}

			ChannelPointReward reward = null;
			try {
				reward = new ChannelPointReward(id, title, prompt, cost, bgColor, new URL(imgURL), image, defaultIcon);
			} catch (MalformedURLException e) {
				Logging.getLogger().error(e.getMessage(), e);
			}
			if (reward != null) {
				rewards.add(reward);
			}
		}
		return rewards;
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
			Logging.getLogger().error(e.getMessage(), e);
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

	public static String getFollowerAge(String user){

		String ID = getIDs(user);

		if(user.equalsIgnoreCase(TwitchAccount.login)) return user + " is the broadcaster!";

		if(ID == null) return user + " doesn't exist!";

		InboundFollowers iF = TwitchAPI.twitchClient.getHelix().getChannelFollowers(SettingsHandler.getSettings("oauth").asString(), TwitchAccount.id, ID, null, null).execute();

		String followAge = "";

		if(iF.getFollows() != null){
			if(!iF.getFollows().isEmpty()) {
				Instant time = iF.getFollows().get(0).getFollowedAt();
				Duration age = Duration.between(time, Instant.now());

				TemporalDuration duration = new TemporalDuration(age);

				followAge = user + " has been following for " + duration + ".";
			}
			else{
				followAge = user + " is not following " + TwitchAccount.display_name + ".";
			}
		}

		if(followAge.isEmpty()){
			followAge = "Failed to get follow age for " + user;
		}

		return followAge;
	}

	public static String getFollowerAgeTime(String user){

		String ID = getIDs(user);

		if(user.equalsIgnoreCase(TwitchAccount.login)) return "IS_BROADCASTER";

		if(ID == null) return "NO_USER";

		InboundFollowers iF = TwitchAPI.twitchClient.getHelix().getChannelFollowers(SettingsHandler.getSettings("oauth").asString(), TwitchAccount.id, ID, null, null).execute();

		String followAge = "";

		if(iF.getFollows() != null){
			if(!iF.getFollows().isEmpty()) {
				Instant time = iF.getFollows().get(0).getFollowedAt();
				Duration age = Duration.between(time, Instant.now());

				TemporalDuration duration = new TemporalDuration(age);

				followAge = duration.toBasicString();
			}
			else{
				followAge = "NOT_FOLLOWING";
			}
		}

		if(followAge.isEmpty()){
			followAge = "ERROR";
		}

		return followAge;
	}


	public static String getChannel() {

		JSONObject nameObj = twitchAPI("https://api.twitch.tv/helix/users");
		assert nameObj != null;
		try {
			return nameObj.getJSONArray("data").getJSONObject(0).get("login").toString().replaceAll("\"", "");
		}
		catch (JSONException e){
			Logging.getLogger().error(e.getMessage(), e);
			return null;
		}

	}

	public static boolean isMod(String username){

		String id = getIDs(username);

		if(id != null) {
			JSONObject moderators = twitchAPI("https://api.twitch.tv/helix/moderation/moderators?broadcaster_id=" + TwitchAccount.id + "&user_id=" + id);
			if (moderators == null) return false;
			return !moderators.getJSONArray("data").isEmpty();
		}
		return false;
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
			Logging.getLogger().error(e.getMessage(), e);
		}
		return null;
	}

	private static JSONObject twitchAPI(String URL) {
		URLConnection conn = null;
		try {
			URL url = new URL(URL);
			conn = url.openConnection();
			conn.setRequestProperty("Authorization", "OAuth " + SettingsHandler.getSettings("oauth").asString());
			conn.setRequestProperty("Client-ID", clientID);
			conn.setRequestProperty("Authorization", "Bearer " + SettingsHandler.getSettings("oauth").asString());
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			return new JSONObject(br.readLine());
		}
		catch (Exception e){
			if(conn != null){
				try {
					int statusCode = ((HttpURLConnection) conn).getResponseCode();
					Logging.getLogger().info("Failed to authenticate user. Code: " + statusCode + " URL: " + URL);
				}
				catch (IOException f){
					Logging.getLogger().info("If we get here, idk");
				}
			}

			setOauth(true, () -> {});
			return null;
		}
	}

	public static JSONObject getInfo() {

		try {
			JSONObject userID;
			try {
				userID = twitchAPI("https://api.twitch.tv/helix/users?login=" + SettingsHandler.getSettings("twitchUsername").asString());
			} catch (JSONException e) {
				Logging.getLogger().error(e.getMessage(), e);
				SettingsHandler.writeSettings("channel", Objects.requireNonNull(getChannel()));
				userID = twitchAPI("https://api.twitch.tv/helix/users?login=" + SettingsHandler.getSettings("twitchUsername").asString());
			}
			if(userID == null){
				return null;
			}

			return userID.getJSONArray("data").getJSONObject(0);
		}
		catch (Exception e){
			Logging.getLogger().error(e.getMessage(), e);
		}
		return getInfo();
	}

	private static JSONObject user;

	public static JSONObject getUser(){
		return user;
	}

	public static String getUserID(){
		if(user == null){
			setUser(SettingsHandler.getSettings("twitchUsername").asString());
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

			if(!userIDObject.getJSONArray("data").isEmpty()) {
				String userID = userIDObject.getJSONArray("data").getJSONObject(0).get("id").toString().replaceAll("\"", "");
				userIDCache.put(username, userID);
				return userID;
			}
			return null;
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
			Logging.getLogger().error(e.getMessage(), e);
			return "";
		}
	}

	public static String setTitle(String title){

		try {
			twitchClient.getHelix().updateChannelInformation(SettingsHandler.getSettings("oauth").asString(), TwitchAccount.id, new ChannelInformation().withTitle(title)).execute();
		}
		catch (Exception e){
			Logging.getLogger().error(e.getMessage(), e);
			return "Please refresh your Twitch account in Settings > Accounts to use this.";
		}
		return null;
	}

	public static String setGame(String gameTitle){

		try {
			GameList gameList = twitchClient.getHelix().getGames(SettingsHandler.getSettings("oauth").asString(), null, List.of(gameTitle), null).execute();
			if (!gameList.getGames().isEmpty()) {
				Game game = gameList.getGames().get(0);
				twitchClient.getHelix().updateChannelInformation(SettingsHandler.getSettings("oauth").asString(), TwitchAccount.id, new ChannelInformation().withGameName(game.getName()).withGameId(game.getId())).execute();
			}
			else {
				return "no_game";
			}
		}
		catch (Exception e){
			Logging.getLogger().error(e.getMessage(), e);
			return "Please refresh your Twitch account in Settings > Accounts to use this.";
		}
		return null;
	}

	public static boolean oauthOpen = false;

	public static String authorize(boolean forceVerify, String... params) throws URISyntaxException {
		StringBuilder authLink = new StringBuilder("https://id.twitch.tv/oauth2/authorize?");

		authLink.append("response_type=token");
		authLink.append("&client_id=").append(clientID);
		authLink.append("&redirect_uri=http://localhost:23522");
		authLink.append("&scope=");

		for(String param : params){
			authLink.append(param);
			authLink.append("+");
		}

		authLink.deleteCharAt(authLink.length()-1);

		if(forceVerify) {
			authLink.append("&force_verify=true");
		}

		Utilities.openURL(new URI(authLink.toString()));
		TwitchHTTPServer.awaitAccessToken();

		return TwitchHTTPServer.getAccessToken();
	}

	public static void setOauth(boolean prompt, Function onFinish) {

		if(!oauthOpen) {

			if(prompt){
				new Thread(() -> {
					String option = DialogBox.showDialogBox("Failed to connect to Twitch!", "Would you like to try to log in again?", "", new String[]{"Yes", "Cancel"});
					if(option.equalsIgnoreCase("Yes")){
						setOauth(false, onFinish);
					}
				}).start();
				return;
			}

			oauthOpen = true;

			try {
				String token = authorize(true, "user_read",
						"chat:edit",
						"chat:read",
						"bits:read",
						"channel:moderate",
						"channel:read:redemptions",
						"channel:read:subscriptions",
						"channel:read:predictions",
						"channel:read:polls",
						"channel:manage:redemptions",
						"channel:manage:broadcast",
						"channel:manage:polls",
						"moderator:read:followers",
						"moderator:read:chatters",
						"moderation:read");

				if (token != null) {
					SettingsHandler.writeSettings("oauth", token);
					SettingsHandler.writeSettings("twitchUsername", Objects.requireNonNull(getChannel()));
					SettingsHandler.writeSettings("isTwitchLoggedIn", String.valueOf(true));

					TwitchAccount.setInfo(() -> {});
				} else {
					Logging.getLogger().info("Failed to get token");
				}
			}
			catch (Exception e){
				Logging.getLogger().error(e.getMessage(), e);
			}
			onFinish.run();

			oauthOpen = false;
		}
	}
}
