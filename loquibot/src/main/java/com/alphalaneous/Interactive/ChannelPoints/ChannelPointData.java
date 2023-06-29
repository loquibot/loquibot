package com.alphalaneous.Interactive.ChannelPoints;

import com.alphalaneous.Utils.Defaults;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ChannelPointData {

    private static final ArrayList<ChannelPointData> registeredPoints = new ArrayList<>();

    private String name;
    private String message;

    private long counter = 0;


    public ChannelPointData(String name){
        this.name = name;
    }
    public void registerPoint(){
        registeredPoints.add(this);
    }
    public void deregisterPoint(){
        registeredPoints.remove(this);
    }

    public void setName(String keyword) {
        this.name = keyword;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public String getName() {
        return name;
    }

    public long getCounter(){
        return counter;
    }

    public String getMessage() {
        return message;
    }


    public static ArrayList<ChannelPointData> getRegisteredPoints(){
        return registeredPoints;
    }

    private static void save(Path path, JSONObject object){
        try {
            Files.write(path.toAbsolutePath(), object.toString(4).getBytes());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void saveCustomPoints(){
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for(ChannelPointData data : LoadPoints.getCustomPoints()){
            JSONObject commandObject = new JSONObject();
            commandObject.putOpt("name", data.getName());
            commandObject.putOpt("message", data.getMessage());
            commandObject.putOpt("counter", data.getCounter());
            jsonArray.put(commandObject);
        }
        jsonObject.put("points", jsonArray);
        save(Paths.get(Defaults.saveDirectory + "/loquibot/customPoints.json").toAbsolutePath(), jsonObject);
    }
}
