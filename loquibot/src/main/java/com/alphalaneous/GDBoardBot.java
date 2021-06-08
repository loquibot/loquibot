package com.alphalaneous;

import com.alphalaneous.SettingsPanels.RequestsSettings;
import com.alphalaneous.Windows.DialogBox;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

class GDBoardBot {
	static boolean initialConnect = false;
	private static int wait = 2000;
	private static final AtomicBoolean isConnect = new AtomicBoolean(false);
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
				try {
					Thread.sleep(wait);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				wait = wait * 2;
				if (Main.programLoaded) {
					wait = 2000;
				}
				start();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	static void start() {
		start(false);
	}

	static void start(boolean reconnect) {
		System.out.println("started");

		JSONObject authObj = new JSONObject();
		authObj.put("request_type", "connect");
		authObj.put("oauth", Settings.getSettings("oauth").asString());
		sendMessage(authObj.toString());

		new Thread(() -> {
			isConnect.set(false);
			if (!firstOpen) {
				DialogBox.setUnfocusable();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				firstOpen = false;
			}
			if (!isConnect.get()) {
				if (!reconnect) {
                    ServerBot.showReconnectDialog();
                }
			}
		}).start();


		new Thread(() -> {
			String inputLine;

			while (true) {

				while (clientSocket.isClosed() || !clientSocket.isConnected()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
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
						DialogBox.closeDialogBox();
						String channel = object.get("username").toString().replaceAll("\"", "").replaceAll("#", "");
						Settings.writeSettings("channel", channel);
						initialConnect = true;
						isConnect.set(true);
						APIs.setAllViewers();
						/*
						  Reads chat as streamer, reduces load on servers for some actions
						  such as custom commands that don't use the normal prefix
						 */
					}
					if (event.equalsIgnoreCase("blocked_ids_updated") && RequestsSettings.gdModeOption) {
						String[] IDs = object.get("ids").toString().replace("\"", "").replace("{", "").replace("}", "").replace("\\", "").split(",");
						for (String ID : IDs) {
							Requests.globallyBlockedIDs.put(Long.parseLong(ID.split(":", 2)[0]), ID.split(":", 2)[1]);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					try {
						Thread.sleep(wait);
					} catch (InterruptedException f) {
						f.printStackTrace();
					}
					wait = wait * 2;
					if (Main.programLoaded) {
						wait = 2000;
					}
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			try {
				Thread.sleep(wait);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			start(true);
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
