package com.alphalaneous;

import com.alphalaneous.ChatbotTab.CustomKeywords;
import com.alphalaneous.SettingsPanels.ChannelPointSettings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class LoadPoints {

    public static ArrayList<ChannelPointData> customPoints = new ArrayList<>();
    public static ArrayList<ChannelPointData> getCustomPoints(){
        return customPoints;
    }


    public static void createPathIfDoesntExist(Path path){
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);

                JSONObject object = new JSONObject();
                JSONArray array = new JSONArray();
                object.put("points", array);

                Files.write(path, object.toString(4).getBytes());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void reloadCustomPoints(){
        customPoints.clear();
        customPoints.addAll(ChannelPointData.getRegisteredPoints());
    }

    public static void loadPoints(){
        try {
            Path customCommandPath = Paths.get(Defaults.saveDirectory + "/loquibot/customPoints.json");
            createPathIfDoesntExist(customCommandPath);
            customPoints = loadJsonToPointDataArrayList(Files.readString(customCommandPath, StandardCharsets.UTF_8));

            for(ChannelPointData data : customPoints) data.registerPoint();

        }
        catch (Exception e){
            e.printStackTrace();
        }
        CustomKeywords.loadKeywords();
    }

    private static ArrayList<ChannelPointData> loadJsonToPointDataArrayList(String jsonData){
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonData);
        }
        catch (Exception e){
            jsonObject = new JSONObject();
        }
        JSONArray pointArray = jsonObject.getJSONArray("points");
        ArrayList<ChannelPointData> commandDataArrayList = new ArrayList<>();
        for(int i = 0; i < pointArray.length(); i++){
            try {
                JSONObject commandDataJson = pointArray.getJSONObject(i);
                ChannelPointData channelPointData = new ChannelPointData(commandDataJson.getString("name"));
                channelPointData.setCounter(commandDataJson.optLong("counter"));
                channelPointData.setMessage(commandDataJson.optString("message"));
                commandDataArrayList.add(channelPointData);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        return commandDataArrayList;
    }

    private static String readIntoString(BufferedReader reader){
        return Utilities.readIntoString(reader);
    }
}
