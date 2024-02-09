package com.alphalaneous.Interactive.TwitchExclusive.ChannelPoints;

import com.alphalaneous.Interactive.CustomData;
import com.alphalaneous.Pages.CommandPages.CommandsPage;
import com.alphalaneous.Utilities.Utilities;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ChannelPointData extends CustomData {

    private static final ArrayList<ChannelPointData> registeredChannelPoints = new ArrayList<>();

    private String message;
    private final String id;
    private String name;

    private long counter = 0;

    public ChannelPointData(String id){
        this.id = id;
    }

    @Override
    public void register() {
        registeredChannelPoints.add(this);
    }

    @Override
    public void deregister() {
        registeredChannelPoints.remove(this);
        saveCustomChannelPoints(true);
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void setEnabled(boolean isEnabled){
    }

    @Override
    public void setCounter(long counter) {
        this.counter = counter;
    }
    @Override
    public String getName() {
        return name;
    }

    public String getId(){
        return id;
    }

    @Override
    public long getCounter() {
        return counter;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void save(boolean reload) {
        saveCustomChannelPoints(reload);
    }

    @Override
    public void save() {
        saveCustomChannelPoints(false);
    }

    @Override
    public boolean isEnabled(){
        return true;
    }

    public static ArrayList<ChannelPointData> getRegisteredChannelPoints(){
        return registeredChannelPoints;
    }

    public static void saveCustomChannelPoints(boolean reload) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        if (getRegisteredChannelPoints() != null) {
            for (ChannelPointData data : getRegisteredChannelPoints()) {
                JSONObject commandObject = new JSONObject();
                commandObject.putOpt("id", data.getId());
                commandObject.putOpt("message", data.getMessage());
                commandObject.putOpt("counter", data.getCounter());
                jsonArray.put(commandObject);
            }
            jsonObject.put("channelPoints", jsonArray);
            try {
                Files.write(Paths.get(Utilities.saveDirectory + "/customChannelPoints.json").toAbsolutePath(), jsonObject.toString(4).getBytes());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        if(reload) CommandsPage.load();
    }
}
