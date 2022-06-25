package com.alphalaneous;

import com.alphalaneous.FileUtils.FileList;
import com.alphalaneous.FileUtils.GetInternalFiles;
import com.alphalaneous.FileUtils.InternalFile;
import com.alphalaneous.TwitchBot.ChatMessage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.Stream;

public class TwitchListener extends WebSocketClient {

	private JSONObject topicObject = new JSONObject();
	private boolean pingSuccess = false;
	private static TwitchListener currentTwitchListener;


	TwitchListener(URI serverURI) {
		super(serverURI);
		currentTwitchListener = this;
		topicObject.put("type", "LISTEN");
		JSONObject data = new JSONObject();
		JSONArray topics = new JSONArray();
		topics.put("channel-points-channel-v1." + APIs.getUserID());

		data.put("topics", topics);
		data.put("auth_token", Settings.getSettings("oauth").asString());
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
				System.out.println(message);
				String redemptionA = object.getJSONObject("data").get("message").toString().replaceAll("\\\\\"", "\"").replaceAll("\r", "").replaceAll("\n", "");
				System.out.println(redemptionA);
				String redemption = new JSONObject(redemptionA).getJSONObject("data").getJSONObject("redemption").getJSONObject("reward").get("title").toString().replaceAll("\"", "");
				String username = new JSONObject(redemptionA).getJSONObject("data").getJSONObject("redemption").getJSONObject("user").get("login").toString().replaceAll("\"", "");
				System.out.println(username);
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
						ChatMessage messageA = new ChatMessage(new String[]{}, username, username, userInput, new String[0], false, false, false, 0, false);
						Main.sendMessage(CommandNew.replaceBetweenParentheses(messageA, data.getMessage(), data.getMessage().split(" "), null));
					}

				} catch (Exception ignored) {
				}

			}
		}
	}

	@Override
	public void onMessage(ByteBuffer message) {
		System.out.println("received ByteBuffer");
	}

	@Override
	public void onError(Exception ex) {
		ex.printStackTrace();
	}

	void disconnectBot() {
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