package com.alphalaneous;

import com.alphalaneous.SettingsPanels.Account;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.Services.Twitch.TwitchAccount;
import com.alphalaneous.Utils.Utilities;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerBot {

	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
	private static ServerBot currentServerBot;
	private boolean disconnected = false;
	public ServerBot(){
		currentServerBot = this;
	}

	public static ServerBot getCurrentServerBot(){
		return currentServerBot;
	}

	public void connect() {

		try {
			if(Settings.getSettings("isDev").asBoolean()) {
				if (Settings.getSettings("dev_Server").asString().equalsIgnoreCase("main"))
					clientSocket = new Socket("142.93.12.163", 2963);
				else clientSocket = new Socket("localhost", 2963);
			}
			else clientSocket = new Socket("142.93.12.163", 2963);

			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			Utilities.sleep(2000);
			connect();
		}

		if(Settings.getSettings("twitchEnabled").asBoolean()) {
			JSONObject authObj = new JSONObject();
			authObj.put("request_type", "connect");
			authObj.put("oauth", Settings.getSettings("oauth").asString());
			sendMessage(authObj.toString());
		}

		String inputLine;
		connect: while (true) {
			try {
				if ((inputLine = in.readLine()) == null) break;
			} catch (Exception e) {
				break;
			}
			//System.out.println(inputLine);
			String event = "";
			try {
				JSONObject object = new JSONObject(inputLine);
				if (object.get("event") != null) {
					event = object.get("event").toString().replaceAll("\"", "");
				}
				//System.out.println(event);

				switch (event) {
					case "connected" : {

						System.out.println("> Connected to loquibot Servers");
						String channel = object.getString("username");
						if(object.optBoolean("is_officer")){
							RequestsTab.setOfficerVisible();
						}
						if(Settings.getSettings("twitchEnabled").asBoolean()) {
							Settings.writeSettings("channel", channel);
							Account.refreshTwitch(channel);
							APIs.setAllViewers();
						}
						break;
					}
					case "connect_failed" : {
						clientSocket.close();
						break connect;
					}
					case "blocked_ids_updated" : {
						System.out.println("> Blocked IDs Updated");
						JSONArray IDs = object.getJSONObject("ids").getJSONArray("globallyBlockedIDs");
						Requests.globallyBlockedIDs.clear();
						for (int i = 0; i < IDs.length(); i++) {
							long ID = IDs.getJSONObject(i).getLong("id");
							String reason = IDs.getJSONObject(i).getString("reason");
							Requests.globallyBlockedIDs.put(ID, reason);
						}
						break;
					}
					case "broadcast" : {
						String message = object.getString("message");
						Main.sendMessage("\uD83D\uDCE2 | " + message);
						Main.sendYTMessage("\uD83D\uDCE2 | " + message);
						break;
					}

					case "mod_connect_request" : {
						String user = object.getString("username");
						if(APIs.isMod(user)){

							JSONObject object1 = new JSONObject();
							object1.put("type", "mod_connect");
							object1.put("success", true);
							object1.put("to", user);
							object1.put("from", TwitchAccount.login);

							sendMessage(object1.toString());
						}
						break;
					}
					case "clients" : {
						//JSONArray array = object.getJSONArray("clients");
					}
					case "error" : {
						/*String error = object.getString("error");
						switch (error) {
							case "invalid_blocked_ID" :
							case "no_id_block_reason_given" :
							case "id_already_blocked" :
							case "invalid_unblocked_ID" :
							case "id_not_blocked" :
							default : break;
						}*/
						break;
					}

				}
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
		System.out.println("> Disconnected from ServerBot");
		if(!disconnected) {
			Utilities.sleep(2000);
			new ServerBot().connect();
		}
	}

	public void sendMessage(String message) {
		out.println(message);
	}

	public void disconnect() {
		try {
			disconnected = true;
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
