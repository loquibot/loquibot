package com.alphalaneous;

import com.alphalaneous.Services.Twitch.TwitchEventUtils;
import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.SettingsHandler;
import com.alphalaneous.Utilities.Utilities;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.net.URI;

public class ServerConnection extends WebSocketClient {


    public ServerConnection(URI uri){
        super(uri);
        connect();
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        Logging.getLogger().info("Connected to Loquibot Servers");

        if(SettingsHandler.getSettings("isTwitchLoggedIn").asBoolean()){
            connectTwitch();
        }

        new Thread(() -> {
            while(true){
                Utilities.sleep(10000);
                try {
                    sendPing();
                }
                catch (WebsocketNotConnectedException e){
                    tryReconnect();
                    break;
                }
            }
        }).start();

    }

    public void connectTwitch(){
        JSONObject authObj = new JSONObject();
        authObj.put("request_type", "connect");
        authObj.put("oauth", SettingsHandler.getSettings("oauth").asString());
        send(authObj.toString());
    }

    @Override
    public void onMessage(String s) {

        JSONObject object = null;
        try{
            object = new JSONObject(s);
        }
        catch (JSONException e){
            Logging.getLogger().warn("Servers returned non-json response");
        }
        if(object != null){
            String event = "";
            if(object.has("event")){
                event = object.getString("event");
            }

            switch (event){

                case "connected": {
                    Servers.waitTime = 1;
                    break;
                }
                case "connect_failed" : {
                    tryReconnect();
                    break;
                }
            }
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        Logging.getLogger().warn("Servers disconnected with code " + i);
        tryReconnect();
    }

    @Override
    public void onError(Exception e) {
        Logging.getLogger().error(e.getMessage(), e);
        tryReconnect();
    }

    public void tryReconnect(){

        Servers.loadServers();
        Servers.waitTime *= 2;
    }
}
