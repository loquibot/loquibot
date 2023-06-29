package com.alphalaneous.Services.Twitch;

import com.alphalaneous.Interactive.ChannelPoints.ChannelPointData;
import com.alphalaneous.Interactive.Commands.CommandHandler;
import com.alphalaneous.Main;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Utils.Utilities;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

public class TwitchListener extends WebSocketClient {

	private JSONObject topicObject = new JSONObject();
	private boolean pingSuccess = false;
	private static TwitchListener currentTwitchListener;


	public TwitchListener(URI serverURI) {
		super(serverURI);
		currentTwitchListener = this;
		topicObject.put("type", "LISTEN");
		JSONObject data = new JSONObject();
		JSONArray topics = new JSONArray();
		topics.put("channel-points-channel-v1." + TwitchAPI.getUserID());

		data.put("topics", topics);
		data.put("auth_token", SettingsHandler.getSettings("oauth").asString());
		topicObject.put("data", data);
	}

	public static TwitchListener getCurrentTwitchListener(){
		return currentTwitchListener;
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		while (true) {
			try {
				send(topicObject.toString());

				System.out.println("> Connected to Twitch Listener");
				break;

			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			Utilities.sleep(1000);
		}
		new Thread(() -> {
			while (true) {
				send("{\n" +
						"  \"type\": \"PING\"\n" +
						"}");
				pingSuccess = false;
				Utilities.sleep(300000);
				if (!pingSuccess) {
					send("{\n" +
							"  \"type\": \"RECONNECT\"\n" +
							"}");
				}
			}
		}).start();
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.println("> Disconnected from Twitch Listener: " + code + " | Additional info: " + reason);
		Utilities.sleep(2000);
		try {
			new TwitchListener(new URI("wss://pubsub-edge.twitch.tv")).connect();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessage(String message) {
		JSONObject object = new JSONObject(message);
		String event = object.get("type").toString().replaceAll("\"", "");
		if (event.equalsIgnoreCase("PONG")) {
			pingSuccess = true;
		}
		if (event.equalsIgnoreCase("MESSAGE")) {
			String topic = object.getJSONObject("data").get("topic").toString().replaceAll("\"", "");

			if (topic.startsWith("channel-points-channel-v1")) {
				String redemptionA = object.getJSONObject("data").get("message").toString().replaceAll("\r", "").replaceAll("\n", "");
				String redemption = new JSONObject(redemptionA).getJSONObject("data").getJSONObject("redemption").getJSONObject("reward").get("title").toString().replaceAll("\"", "");
				String username = new JSONObject(redemptionA).getJSONObject("data").getJSONObject("redemption").getJSONObject("user").get("login").toString().replaceAll("\"", "");
				String userID = new JSONObject(redemptionA).getJSONObject("data").getJSONObject("redemption").getJSONObject("user").get("id").toString().replaceAll("\"", "");

				boolean isUserinput = new JSONObject(redemptionA).getJSONObject("data").getJSONObject("redemption").getJSONObject("reward").getBoolean("is_user_input_required");
				String userInput = "";
				if (isUserinput) {
					userInput = new JSONObject(redemptionA).getJSONObject("data").getJSONObject("redemption").get("user_input").toString().replaceAll("\"", "");
					System.out.println(redemption + " redeemed by " + username + " with " + userInput);
				} else {
					System.out.println(redemption + " redeemed by " + username);
				}
				try {

					ChannelPointData data = null;

					for (ChannelPointData existingData : ChannelPointData.getRegisteredPoints()) {
						if (existingData.getName().equalsIgnoreCase(redemption)) {
							data = existingData;
							break;
						}
					}
					if(data != null){

						String[] tags = new String[1];
						tags[0] = "user-id=" + userID;

						ChatMessage messageA = new ChatMessage(tags, username, username, userInput, new String[0], false, false, false, 0, false, false);
						Main.sendMessage(CommandHandler.replaceBetweenParentheses(messageA, data.getMessage(), data.getMessage().split(" "), null));
					}

				} catch (Exception ignored) {
				}

			}
		}
	}

	@Override
	public void onMessage(ByteBuffer message) {

	}

	@Override
	public void onError(Exception ex) {
		ex.printStackTrace();
	}

	public void disconnectBot() {
		try {
			send("{\n" +
					"  \"type\": \"UNLISTEN\"\n" +
					"}");
		} catch (WebsocketNotConnectedException ignored) {

		}
	}

	public void reconnectBot() {
		send(topicObject.toString());
	}
}