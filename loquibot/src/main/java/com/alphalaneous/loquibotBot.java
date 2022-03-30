package com.alphalaneous;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.Socket;

class loquibotBot {
	static boolean initialConnect = false;
	private static int wait = 2000;
	private static PrintWriter out;
	private static BufferedReader in;
	private static Socket clientSocket;
	private static boolean firstOpen = true;

	static {
		while (true) {
			try {
				clientSocket = new Socket("165.227.53.200", 2963);
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				break;
			} catch (ConnectException | NoRouteToHostException e) {
				System.out.println("failed here");
				Utilities.sleep(wait);
				wait = wait * 2;
				if (Main.programLoaded) {
					wait = 2000;
				}
				start();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Utilities.sleep(1000);
		}
	}



	static void start() {
		JSONObject authObj = new JSONObject();
		authObj.put("request_type", "connect");
		authObj.put("oauth", Settings.getSettings("oauth").asString());
		sendMessage(authObj.toString());

		new Thread(() -> {
			if (!firstOpen) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				firstOpen = false;
			}
		}).start();


		new Thread(() -> {
			String inputLine;

			while (true) {

				while (clientSocket.isClosed() || !clientSocket.isConnected()) {
					Utilities.sleep(100);
				}
				try {
					if ((inputLine = in.readLine()) == null) break;
				} catch (Exception e) {
					try {
						clientSocket.close();
						out.close();
						in.close();
						start();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
				}
				String event = "";
				//System.out.println(inputLine);

				try {
					JSONObject object = new JSONObject(inputLine);
					if (object.get("event") != null) {
						event = object.get("event").toString().replaceAll("\"", "");
					}
					if (event.equalsIgnoreCase("connected")) {
						String channel = object.get("username").toString().replaceAll("\"", "").replaceAll("#", "");
						Settings.writeSettings("channel", channel);
						initialConnect = true;
						APIs.setAllViewers();
						/*
						  Reads chat as streamer, reduces load on servers for some actions
						  such as custom commands that don't use the normal prefix
						 */
					}
					if (event.equalsIgnoreCase("blocked_ids_updated") && Settings.getSettings("gdMode").asBoolean()) {
						String[] IDs = object.get("ids").toString().replace("\"", "").replace("{", "").replace("}", "").replace("\\", "").split(",");
						for (String ID : IDs) {
							Requests.globallyBlockedIDs.put(Long.parseLong(ID.split(":", 2)[0]), ID.split(":", 2)[1]);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					Utilities.sleep(wait);
					wait = wait * 2;
					if (Main.programLoaded) {
						wait = 2000;
					}
				}
				Utilities.sleep(10);
			}

			Utilities.sleep(wait);
			start();
			wait = wait * 2;
			if (Main.programLoaded) {
				wait = 2000;
			}
		}).start();
	}

	static void sendMessage(String message) {
		out.println(message);
	}

}
