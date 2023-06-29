package com.alphalaneous.ChatBot;

import com.alphalaneous.Main;
import com.alphalaneous.Services.GeometryDash.Requests;
import com.alphalaneous.Services.Kick.KickAccount;
import com.alphalaneous.Services.Twitch.TwitchAPI;
import com.alphalaneous.Services.Twitch.TwitchAccount;
import com.alphalaneous.Services.Twitch.TwitchChatListener;
import com.alphalaneous.Settings.Account;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.Tabs.SettingsTab;
import com.alphalaneous.Utils.Utilities;
import jdk.jshell.execution.Util;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class SocketClient extends WebSocketClient {

    private boolean disconnected = false;
    private boolean isOfficer = false;
    private int waitTime = 1;
    public SocketClient(String serverUri) throws URISyntaxException {
        super(new URI(serverUri));
        try {
            connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        if(SettingsHandler.getSettings("twitchEnabled").asBoolean()) {
            JSONObject authObj = new JSONObject();
            authObj.put("request_type", "connect");
            authObj.put("oauth", SettingsHandler.getSettings("oauth").asString());
            send(authObj.toString());
        }
        if(SettingsHandler.getSettings("kickEnabled").asBoolean()){
            JSONObject connectObj = new JSONObject();
            connectObj.put("request_type", "kick_connect");
            connectObj.put("chatroomID", KickAccount.chatroomID);
            connectObj.put("username", KickAccount.username);

            send(connectObj.toString());
        }
        System.out.println("> ServerBot Started");

        new Thread(() -> {
            while(true){
                Utilities.sleep(10000);
                try {
                    sendPing();
                }
                catch (WebsocketNotConnectedException e){
                    ServerBot.disconnect();
                    ServerBot.connect();
                    break;
                }
            }
        }).start();

    }
    public static boolean sentStartupMessage = false;

    @Override
    public void onMessage(String s) {

        String event = "";
            try {
                JSONObject object = new JSONObject(s);
                if(object.isEmpty()) return;
                if (object.get("event") != null) {
                    event = object.get("event").toString().replaceAll("\"", "");
                }
                //System.out.println(event);

                switch (event) {
                    case "connected" : {
                        waitTime = 1;
                        System.out.println("> Connected to loquibot Servers");
                        String channel = object.getString("username");
                        if(object.optBoolean("is_officer")){
                            isOfficer = true;
                            SettingsTab.showReportedIDsTab();
                            RequestsTab.setOfficerVisible();
                        }
                        if(SettingsHandler.getSettings("twitchEnabled").asBoolean()) {
                            SettingsHandler.writeSettings("channel", channel);
                            Account.refreshTwitch(channel);
                        }
                        boolean loquiIsMod = TwitchAPI.isLoquiMod();

                        new Thread(() -> {
                            Utilities.sleep(1000);
                            if(!sentStartupMessage) {
                                if(loquiIsMod){
                                    Main.sendMessage(com.alphalaneous.Utils.Utilities.format("🔷 | $STARTUP_MESSAGE_MOD_VIP$"));
                                }
                                else {
                                    Main.sendMessage(com.alphalaneous.Utils.Utilities.format("🔷 | $STARTUP_MESSAGE$"));
                                }
                                Main.sendYTMessage(com.alphalaneous.Utils.Utilities.format("🔷 | $STARTUP_MESSAGE_MOD_VIP$"), null);
                                Main.sendKickMessage(com.alphalaneous.Utils.Utilities.format("🔷 | $STARTUP_MESSAGE_MOD_VIP$"), null);

                                sentStartupMessage = true;
                            }
                        }).start();
                        break;
                    }
                    case "connect_failed" : {
                        close();
                        break;
                    }
                    case "kick_chat_token": {
                        String token = object.getString("token");
                        SettingsHandler.writeSettings("kick_chat_token", token);
                        break;
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
                    case "blocked_users_updated" : {
                        System.out.println("> Blocked Users Updated");
                        JSONArray IDs = object.getJSONObject("users").getJSONArray("globallyBlockedUsers");
                        Requests.globallyBlockedUsers.clear();
                        for (int i = 0; i < IDs.length(); i++) {
                            long ID = IDs.getJSONObject(i).getLong("id");
                            String reason = IDs.getJSONObject(i).getString("reason");
                            Requests.globallyBlockedUsers.put(ID, reason);
                        }
                        break;
                    }
                    case "reported_ids_updated" : {
                        System.out.println("> Reported IDs Updated");

                        JSONArray IDs = object.getJSONObject("ids").getJSONArray("reportedIDs");
                        Requests.reportedIDs.clear();
                        for (int i = 0; i < IDs.length(); i++) {
                            Requests.reportedIDs.add(IDs.getJSONObject(i));
                        }
                        break;
                    }
                    case "broadcast" : {
                        String message = object.getString("message");
                        Main.sendMessage("\uD83D\uDCE2 | " + message);
                        Main.sendYTMessage("\uD83D\uDCE2 | " + message, null);
                        Main.sendKickMessage("\uD83D\uDCE2 | " + message, null);

                        break;
                    }

                    case "mod_connect_request" : {
                        String user = object.getString("username");
                        if(TwitchAPI.isMod(user)){

                            JSONObject object1 = new JSONObject();
                            object1.put("type", "mod_connect");
                            object1.put("success", true);
                            object1.put("to", user);
                            object1.put("from", TwitchAccount.login);

                            send(object1.toString());
                        }
                        break;
                    }
                    case "clients" : {
                        //JSONArray array = object.getJSONArray("clients");
                    }
                    case "error" : {
						String error = object.getString("error");
						/*switch (error) {
							case "invalid_blocked_ID" :
							case "no_id_block_reason_given" :
							case "id_already_blocked" :
							case "invalid_unblocked_ID" :
							case "id_not_blocked" :
							default : break;
						}*/
                        switch (error){
                            case "client_already_connected" : {
                                JOptionPane.showMessageDialog(null,
                                        "Loquibot Instance Already Connected to Servers with your IP!\n" +
                                                "Try killing any other loquibot instance in task manager (OpenJDK Platform Binary)",
                                        "Connection Error",
                                        JOptionPane.ERROR_MESSAGE);
                                ServerBot.reconnect = false;
                                Main.close();
                                break;
                            }
                            default:
                                break;
                        }
                        break;
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();

            }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        System.out.println("disconnected");
    }

    @Override
    public void onError(Exception e) {
        new Thread(this::reconnect).start();
    }
}
