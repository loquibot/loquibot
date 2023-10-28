package com.alphalaneous.Services.Twitch;

import com.alphalaneous.SettingsHandler;
import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.Utilities;
import com.github.twitch4j.TwitchClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TwitchAPI {

	public static TwitchClient twitchClient;
	public static AtomicBoolean success = new AtomicBoolean(false);
	@SuppressWarnings("SpellCheckingInspection")
	static final String clientID = "fzwze6vc6d2f7qodgkpq2w8nnsz3rl";

	private static final HashMap<String, String> userIDCache = new HashMap<>();

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

			//todo prompt to re-log in, do not automatically open the browser
			setOauth(true);
			return null;
		}
	}

	public static JSONObject getInfo() {

		try {
			JSONObject userID;
			try {
				userID = twitchAPI("https://api.twitch.tv/helix/users?login=" + SettingsHandler.getSettings("twitchUsername").asString());
			} catch (JSONException e) {
				e.printStackTrace();
				SettingsHandler.writeSettings("channel", Objects.requireNonNull(getChannel()));
				userID = twitchAPI("https://api.twitch.tv/helix/users?login=" + SettingsHandler.getSettings("twitchUsername").asString());
			}
			if(userID == null){
				setOauth(true);
				return getInfo();
			}

			return userID.getJSONArray("data").getJSONObject(0);
		}
		catch (Exception e){
			setOauth(true);
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

	public static void setOauth(boolean prompt) {

		if(!oauthOpen) {

			if(prompt){
				//todo show prompt window to log-in again
			}

			oauthOpen = true;

			try {
				String token = authorize(true, "user_read",
						"chat:edit",
						"channel:moderate",
						"channel:read:redemptions",
						"channel:read:subscriptions",
						"moderation:read",
						"channel:manage:broadcast",
						"chat:read",
						"moderator:read:chatters");

				if (token != null) {
					SettingsHandler.writeSettings("oauth", token);
					SettingsHandler.writeSettings("twitchUsername", Objects.requireNonNull(getChannel()));
					TwitchAccount.setInfo();
				} else {
					Logging.getLogger().info("Failed to get token");
				}
			}
			catch (Exception e){
				e.printStackTrace();
			}

			oauthOpen = false;
		}
	}
}
