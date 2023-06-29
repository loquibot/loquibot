package com.alphalaneous.ChatBot;

import com.alphalaneous.Services.GeometryDash.Requests;
import com.alphalaneous.Main;
import com.alphalaneous.Services.Twitch.TwitchAPI;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Settings.Account;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.Services.Twitch.TwitchAccount;
import com.alphalaneous.Tabs.SettingsTab;
import com.alphalaneous.Utils.Utilities;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

public class ServerBot {

	public static SocketClient clientSocket;
	public static boolean reconnect = true;
	public static void connect() {
		if(reconnect) {
			new Thread(() -> {
				try {
					if (SettingsHandler.getSettings("isDev").asBoolean()) {
						if (SettingsHandler.getSettings("dev_Server").asString().equalsIgnoreCase("main"))
							clientSocket = new SocketClient("ws://164.152.25.111:2963");
						else clientSocket = new SocketClient("ws://localhost:2963");
					} else clientSocket = new SocketClient("ws://164.152.25.111:2963");

				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
		}
	}

	int waitTime = 1;

	public static void sendMessage(String message) {
		if(clientSocket != null && clientSocket.isOpen()) {
			clientSocket.send(message);
		}
	}

	public static void disconnect() {
		if(clientSocket != null) {
			clientSocket.close();
		}
	}
}
