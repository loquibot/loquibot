package com.alphalaneous.Services.Twitch;

import com.alphalaneous.Interfaces.Function;
import com.alphalaneous.Pages.StreamInteractionsPage;
import com.alphalaneous.Utilities.Assets;
import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.SettingsHandler;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class TwitchAccount {

	public static String broadcaster_type;
	public static String offline_image_url;
	public static String description;
	public static String created_at;
	public static String profile_image_url;
	public static BufferedImage profileImage;
	public static String id;
	public static String login;
	public static String display_name;
	public static String type;
	public static long view_count;


	public static void setInfo(Function onFinish) {

		TwitchAPI.setUser(SettingsHandler.getSettings("twitchUsername").asString());

		JSONObject data = TwitchAPI.getInfo();

		if(data == null){
			TwitchAPI.setOauth(true, () -> TwitchAccount.setInfo(onFinish));
			return;
		}

		broadcaster_type = data.getString("broadcaster_type");
		offline_image_url = data.getString("offline_image_url");
		description = data.getString("description");
		created_at = data.getString("created_at");
		profile_image_url = data.getString("profile_image_url");
		id = data.getString("id");
		login = data.getString("login");
		display_name = data.getString("display_name");
		type = data.getString("type");
		view_count = data.getLong("view_count");
		try {
			profileImage = Assets.downloadAsset(profile_image_url);
		} catch (IOException e) {
			Logging.getLogger().error(e.getMessage(), e);
		}
		onFinish.run();
	}

	public static void logout(){
		SettingsHandler.writeSettings("isTwitchLoggedIn", "false");
		SettingsHandler.writeSettings("twitchUsername", "");
		SettingsHandler.writeSettings("oauth", "");
		StreamInteractionsPage.setEnabled(false);
	}
}
