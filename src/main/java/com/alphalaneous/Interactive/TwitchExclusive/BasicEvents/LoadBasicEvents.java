package com.alphalaneous.Interactive.TwitchExclusive.BasicEvents;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Utilities.Utilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class LoadBasicEvents {


    public static BasicEventData followData = new BasicEventData(BasicEventData.BasicEvent.FOLLOW);
    public static BasicEventData subscribeData = new BasicEventData(BasicEventData.BasicEvent.SUBSCRIBE);
    public static BasicEventData raidData = new BasicEventData(BasicEventData.BasicEvent.RAID);

    public static void createPathIfDoesntExist(Path path){
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);

                JSONObject object = new JSONObject();
                JSONArray array = new JSONArray();
                object.put("events", array);

                Files.write(path, object.toString(4).getBytes());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @OnLoad
    public static void loadBasicEvents(){
        try {
            Path customFollowsPath = Paths.get(Utilities.saveDirectory + "customBasicEvents.json");
            createPathIfDoesntExist(customFollowsPath);
            loadJsonToEventArrayList(Files.readString(customFollowsPath, StandardCharsets.UTF_8));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private static ArrayList<BasicEventData> loadJsonToEventArrayList(String jsonData){
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonData);
        }
        catch (Exception e){
            jsonData = "{\"events\": []}";
            jsonObject = new JSONObject(jsonData);
        }

        JSONArray eventArray = jsonObject.getJSONArray("events");
        ArrayList<BasicEventData> eventDataArrayList = new ArrayList<>();
        for(int i = 0; i < eventArray.length(); i++){
            try {
                JSONObject eventDataJson = eventArray.getJSONObject(i);
                BasicEventData.BasicEvent event = BasicEventData.BasicEvent.valueOf(eventDataJson.getString("type"));

                BasicEventData data = null;
                switch (event){
                    case RAID:
                        data = raidData;
                        break;
                    case FOLLOW:
                        data = followData;
                        break;
                    case SUBSCRIBE:
                        data = subscribeData;
                        break;
                }

                data.setCounter(eventDataJson.optLong("counter"));
                data.setMessage(eventDataJson.optString("message"));
                data.setName(Utilities.toFirstUpper(event.name()));
                data.setEnabled(eventDataJson.optBoolean("enabled", true));

            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        return eventDataArrayList;
    }
}