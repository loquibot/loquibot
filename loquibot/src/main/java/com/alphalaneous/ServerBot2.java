package com.alphalaneous;

import com.alphalaneous.SettingsPanels.AccountSettings;
import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Tabs.RequestsTab;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerBot2 {

	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;

	{
		try {
			//clientSocket = new Socket("localhost", 2963); //test
			clientSocket = new Socket("142.93.12.163", 2963); //new server
			//clientSocket = new Socket("165.227.53.200", 2963); //current server
			
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void connect() {
		JSONObject authObj = new JSONObject();
		authObj.put("request_type", "connect");
		authObj.put("oauth", Settings.getSettings("oauth").asString());

		sendMessage(authObj.toString());

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
						Settings.writeSettings("channel", channel);
						AccountSettings.refreshTwitch(channel);
						APIs.setAllViewers();
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
						break;
					}
					case "error" : {
						String error = object.getString("error");
						switch (error) {
							case "invalid_blocked_ID" :
							case "no_id_block_reason_given" :
							case "id_already_blocked" :
							case "invalid_unblocked_ID" :
							case "id_not_blocked" :
							default : break;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
		System.out.println("dead");
	}

	static void showReconnectDialog() {
		String choice = DialogBox.showDialogBox("$CONNECTING_loquibot$", "$CONNECTING_loquibot_INFO$", "$CONNECTING_loquibot_SUBINFO$", new String[]{"$RECONNECT$", "$CANCEL$"});
		if (choice.equalsIgnoreCase("CANCEL")) {
			Main.close();
		}
		if (choice.equalsIgnoreCase("RECONNECT")) {
			APIs.success.set(false);
			APIs.setOauth();

		}
	}

	public void sendMessage(String message) {
		out.println(message);
	}

	void disconnect() {
		try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
