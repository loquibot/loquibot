package com.alphalaneous.Utils;


import com.alphalaneous.Services.GeometryDash.LevelData;
import com.alphalaneous.Services.GeometryDash.RequestFunctions;
import com.alphalaneous.Services.GeometryDash.RequestsUtils;
import com.alphalaneous.Main;
import com.alphalaneous.Settings.BlockedCreators;
import com.alphalaneous.Settings.BlockedIDs;
import com.alphalaneous.Settings.BlockedUsers;
import com.alphalaneous.Swing.Components.LevelButton;
import com.alphalaneous.Tabs.RequestsTab;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.*;

public class ConnectorSocket extends WebSocketServer {

    private static final int portNumber = 19236;

    public ConnectorSocket() {
        super(new InetSocketAddress(portNumber));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {

    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        System.out.println("> Connector: " + s);
        LevelData data = RequestsTab.getRequest(RequestsUtils.getSelection()).getLevelData();

        if(s.split(" ", 2).length == 2) {

            String action = s.split(" ", 2)[0];
            String value = s.split(" ", 2)[1];

            switch (action) {
                case "block":
                    BlockedIDs.addBlockedLevel(value);
                    break;
                case "blockRequester":
                    BlockedUsers.addBlockedUser(value);
                    break;
                case "blockCreator":
                    BlockedCreators.addBlockedCreator(value);
                    break;
            }
        }


        switch (s.toLowerCase()){
            case "next":
                RequestFunctions.skipFunction();
                break;
            case "random":
                RequestFunctions.randomFunction();
                break;
            case "undo":
                RequestFunctions.undoFunction();
                break;
            case "toggle":
                RequestFunctions.requestsToggleFunction();
                break;
            case "opened":
                Main.sendMessageConnectedService(RequestsUtils.getInfoObject(data).toString());
                Main.sendMessageConnectedService(RequestsUtils.getNextInfoObject(RequestsTab.getRequest(RequestsUtils.getPosFromID(data.getGDLevel().id()) + 1).getLevelData()).toString());
                Main.sendMessageConnectedService(RequestsUtils.getCurrentInfoObject(RequestsTab.getRequest(RequestsUtils.getPosFromID(data.getGDLevel().id()) + 1).getLevelData()).toString());
                break;
            case "get":{
                Main.sendMessageConnectedService(RequestsUtils.getInfoObject(data, true).toString());
                break;
            }

            case "undo_get":{
                int pos = RequestFunctions.undoFunctionGetPos();
                RequestsTab.setRequestSelect(pos);

                LevelData data1 = null;
                LevelButton button = RequestsTab.getRequest(RequestsUtils.getSelection());

                if(button != null){
                    data1 = button.getLevelData();
                }

                Main.sendMessageConnectedService(RequestsUtils.getInfoObject(data1, true).toString());
                break;
            }

            case "random_get":{
                RequestFunctions.randomFunction();

                LevelData data1 = null;
                LevelButton button = RequestsTab.getRequest(RequestsUtils.getSelection());

                if(button != null){
                    data1 = button.getLevelData();
                }

                Main.sendMessageConnectedService(RequestsUtils.getInfoObject(data1, true).toString());
                break;
            }
            case "next_get":{
                RequestFunctions.skipFunction();

                LevelData data1 = null;
                LevelButton button = RequestsTab.getRequest(RequestsUtils.getSelection());

                if(button != null){
                    data1 = button.getLevelData();
                }

                Main.sendMessageConnectedService(RequestsUtils.getInfoObject(data1, true).toString());
                break;
            }
            case "main_pressed":
                Main.sendMessageConnectedService(RequestsUtils.getCurrentInfoObject(RequestsTab.getRequest(RequestsUtils.getPosFromID(data.getGDLevel().id()) + 1).getLevelData()).toString());
                break;
            case "block_id":
                RequestFunctions.blockFunction(true);
                break;
            case "clear":
                RequestFunctions.clearFunction(true);
                break;
            case "youtube":
                if(data.getYoutubeURL() != null) {
                    try {
                        Utilities.openURL(new URI(data.getYoutubeURL()));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            default:
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {

    }

    public void sendMessage(String message){
        new Thread(() -> broadcast(message)).start();
    }
}
