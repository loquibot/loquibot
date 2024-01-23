package com.alphalaneous.Utils;


import com.alphalaneous.Services.GeometryDash.LevelData;
import com.alphalaneous.Services.GeometryDash.RequestFunctions;
import com.alphalaneous.Services.GeometryDash.Requests;
import com.alphalaneous.Services.GeometryDash.RequestsUtils;
import com.alphalaneous.Main;
import com.alphalaneous.Settings.BlockedCreators;
import com.alphalaneous.Settings.BlockedIDs;
import com.alphalaneous.Settings.BlockedUsers;
import com.alphalaneous.Swing.Components.LevelButton;
import com.alphalaneous.Swing.Components.LevelDetailsPanel;
import com.alphalaneous.Tabs.RequestsTab;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONObject;

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
        Main.logger.info("Connector Socket sent: " + s);
        Main.logger.info("Double check");

        LevelButton levelButton = RequestsTab.getRequest(RequestsUtils.getSelection());
        LevelData data = null;
        if(levelButton != null){
            data = levelButton.getLevelData();
        }

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
                case "set_pos":
                    RequestsTab.getLevelsPanel().setSelect(Integer.parseInt(value));
                    LevelDetailsPanel.setPanel(RequestsTab.getRequest(RequestsUtils.getSelection()).getLevelData());
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
                if(data != null) {
                    Main.sendMessageConnectedService(RequestsUtils.getInfoObject(data).toString());
                    Main.sendMessageConnectedService(RequestsUtils.getNextInfoObject(RequestsTab.getRequest(RequestsUtils.getPosFromID(data.getGDLevel().getLevel().id()) + 1).getLevelData()).toString());
                    Main.sendMessageConnectedService(RequestsUtils.getCurrentInfoObject(RequestsTab.getRequest(RequestsUtils.getPosFromID(data.getGDLevel().getLevel().id()) + 1).getLevelData()).toString());
                }
                break;
            case "get":{
                Main.sendMessageConnectedService(RequestsUtils.getInfoObject(data, true).toString());
                break;
            }

            case "current_get": {

                LevelData data1;
                LevelButton button = RequestsTab.getRequest(RequestsUtils.getSelection());


                if (button != null) {
                    data1 = button.getLevelData();
                    LevelDetailsPanel.setPanel(data1);
                    Main.sendMessageConnectedService(RequestsUtils.getInfoObject(data1, true).toString());
                }
                else sendEmptyMessage();

                break;
            }

            case "undo_get":{
                int pos = RequestFunctions.undoFunctionGetPos();
                RequestsTab.setRequestSelect(pos);

                LevelData data1;
                LevelButton button = RequestsTab.getRequest(RequestsUtils.getSelection());

                if(button != null) {
                    data1 = button.getLevelData();
                    Main.sendMessageConnectedService(RequestsUtils.getInfoObject(data1, true).toString());
                }
                else sendNextEmptyMessage();

                break;
            }

            case "random_get":{
                if(RequestsTab.getQueueSize() > 1) {
                    RequestFunctions.randomFunction();
                }
                else{
                    sendNextEmptyMessage();
                    break;
                }

                LevelData data1;
                LevelButton button = RequestsTab.getRequest(RequestsUtils.getSelection());

                if(button != null) {
                    data1 = button.getLevelData();
                    Main.sendMessageConnectedService(RequestsUtils.getInfoObject(data1, true).toString());
                }
                else sendNextEmptyMessage();

                break;
            }
            case "next_get": {
                if (RequestsTab.getQueueSize() > 1) {
                    RequestFunctions.skipFunction();
                }
                else{
                    sendNextEmptyMessage();
                    break;
                }
                LevelData data1;
                LevelButton button = RequestsTab.getRequest(RequestsUtils.getSelection());

                if (button != null) {
                    data1 = button.getLevelData();
                    Main.sendMessageConnectedService(RequestsUtils.getInfoObject(data1, true).toString());
                } else sendNextEmptyMessage();

                break;
            }
            case "top_get": {
                if(RequestsTab.getQueueSize() > 1) {
                    RequestFunctions.skipFunction();
                }
                else{
                    sendNextEmptyMessage();
                    break;
                }

                RequestsTab.getLevelsPanel().setSelect(0);

                LevelData data1;
                LevelButton button = RequestsTab.getRequest(RequestsUtils.getSelection());

                if(button != null) {
                    data1 = button.getLevelData();
                    Main.sendMessageConnectedService(RequestsUtils.getInfoObject(data1, true).toString());
                }
                else sendNextEmptyMessage();

            }

            case "list_get":{

                JSONObject levelList = new JSONObject();
                levelList.put("type", "level_list");

                JSONArray levels = new JSONArray();

                for(int i = 0; i < RequestsTab.getQueueSize(); i++){

                    LevelData data1;
                    LevelButton button = RequestsTab.getRequest(i);

                    if(button != null) {
                        data1 = button.getLevelData();
                        JSONObject levelObject = RequestsUtils.getInfoObject(data1, true);
                        levels.put(levelObject);
                    }
                }

                levelList.put("levels", levels);
                levelList.put("levels_enabled", Requests.requestsEnabled);

                Main.sendMessageConnectedService(levelList.toString());


                break;
            }
            case "get_selected_yt":{

                LevelButton button = RequestsTab.getRequest(RequestsUtils.getSelection());
                String videoTitle = button.getLevelData().getVideoTitle();
                String videoCreator = button.getLevelData().getVideoCreator();
                String videoViews = button.getLevelData().getVideoViews();
                String videoID = button.getLevelData().getVideoID();

                JSONObject videoData = new JSONObject();
                videoData.put("type", "youtube_info");
                videoData.put("title", videoTitle);
                videoData.put("creator", videoCreator);
                videoData.put("views", videoViews);
                videoData.put("id", videoID);

                Main.sendMessageConnectedService(videoData.toString());

            }
            case "main_pressed":
                if(data != null) {
                    Main.sendMessageConnectedService(RequestsUtils.getCurrentInfoObject(RequestsTab.getRequest(RequestsUtils.getPosFromID(data.getGDLevel().getLevel().id()) + 1).getLevelData()).toString());
                }
                break;
            case "block_id":
                RequestFunctions.blockFunction(true);
                break;
            case "clear":
                RequestFunctions.clearFunction(true);
                break;
            case "youtube":
                if(data != null) {
                    if (data.getYoutubeURL() != null) {
                        try {
                            Utilities.openURL(new URI(data.getYoutubeURL()));
                        } catch (URISyntaxException e) {
                            Main.logger.error(e.getLocalizedMessage(), e);

                        }
                    }
                }
            default:
        }
    }

    public void sendEmptyMessage(){
        JSONObject object = new JSONObject();
        object.put("status", "empty");
        object.put("version", 2);
        object.put("service", "gd");

        Main.sendMessageConnectedService(object.toString());
    }
    public void sendNextEmptyMessage(){
        JSONObject object = new JSONObject();
        object.put("next_status", "empty");
        object.put("version", 2);
        object.put("service", "gd");

        Main.sendMessageConnectedService(object.toString());
    }
    @Override
    public void onError(WebSocket webSocket, Exception e) {
        Main.logger.error(e.getMessage(), e);
    }

    @Override
    public void onStart() {

    }

    public void sendMessage(String message){
        new Thread(() -> broadcast(message)).start();
    }
}
