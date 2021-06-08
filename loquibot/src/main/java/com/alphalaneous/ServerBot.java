package com.alphalaneous;

import com.alphalaneous.SettingsPanels.AccountSettings;
import com.alphalaneous.SettingsPanels.RequestsSettings;
import com.alphalaneous.Windows.DialogBox;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class ServerBot {

	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;

	{
		try {
			clientSocket = new Socket("165.227.53.200", 2963);
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

		DialogBox.setUnfocusable();
		new Thread(ServerBot::showReconnectDialog).start();
		String inputLine;
		while (true) {
			try {
				if ((inputLine = in.readLine()) == null) break;
			} catch (Exception e) {
				break;
			}
			String event = "";
			try {
				JSONObject object = new JSONObject(inputLine);
				if (object.get("event") != null) {
					event = object.get("event").toString().replaceAll("\"", "");
				}
				if (event.equalsIgnoreCase("connected")) {
					System.out.println("> Connected to GDBoard Servers");
					while (true) {
						if (DialogBox.active) {
							DialogBox.closeDialogBox();
							break;
						}
						Thread.sleep(10);
					}
					String channel = object.get("username").toString().replaceAll("\"", "").replaceAll("#", "");
					Settings.writeSettings("channel", channel);
					AccountSettings.refreshTwitch(channel);
					APIs.setAllViewers();

				} else if (event.equalsIgnoreCase("connect_failed")) {
					clientSocket.close();
					break;
				}
				if (event.equalsIgnoreCase("blocked_ids_updated") && RequestsSettings.gdModeOption) {
					System.out.println("> Blocked IDs Updated");
					DialogBox.closeDialogBox();
					String[] IDs = object.get("ids").toString().replace("\"", "").replace("{", "").replace("}", "").replace("\\", "").split(",");
					for (String ID : IDs) {
						try {
							Requests.globallyBlockedIDs.put(Long.parseLong(ID.split(":", 2)[0]), ID.split(":", 2)[1]);
						}
						catch (NumberFormatException ignored){
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
		String choice = DialogBox.showDialogBox("$CONNECTING_GDBOARD$", "$CONNECTING_GDBOARD_INFO$", "$CONNECTING_GDBOARD_SUBINFO$", new String[]{"$RECONNECT$", "$CANCEL$"});
		if (choice.equalsIgnoreCase("CANCEL")) {
			Main.close();
		}
		if (choice.equalsIgnoreCase("RECONNECT")) {
			APIs.success.set(false);
			APIs.setOauth();

		}
	}

	void sendMessage(String message) {
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
