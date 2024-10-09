package com.alphalaneous.Interactive.TwitchExclusive.ChannelPoints;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.Utilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class LoadChannelPoints {

    public static ArrayList<ChannelPointData> customChannelPoints = new ArrayList<>();

    public static void createPathIfDoesntExist(Path path){
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);

                JSONObject object = new JSONObject();
                JSONArray array = new JSONArray();
                object.put("channelPoints", array);

                Files.write(path, object.toString(4).getBytes());
            }
        }
        catch (Exception e){
            Logging.getLogger().error(e.getMessage(), e);
        }
    }

    @OnLoad
    public static void loadChannelPoints(){
        try {
            Path customChannelPointsPath = Paths.get(Utilities.saveDirectory + "/customChannelPoints.json");
            createPathIfDoesntExist(customChannelPointsPath);
            customChannelPoints = loadJsonToChannelPointDataArrayList(Files.readString(customChannelPointsPath, StandardCharsets.UTF_8));

            for(ChannelPointData data : customChannelPoints) data.register();
        }
        catch (Exception e){
            Logging.getLogger().error(e.getMessage(), e);
        }
    }

    private static ArrayList<ChannelPointData> loadJsonToChannelPointDataArrayList(String jsonData){
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonData);
        }
        catch (Exception e){
            jsonData = "{\"channelPoints\": []}";
            jsonObject = new JSONObject(jsonData);
        }

        JSONArray channelPointArray = jsonObject.getJSONArray("channelPoints");
        ArrayList<ChannelPointData> channelPointDataArrayList = new ArrayList<>();
        for(int i = 0; i < channelPointArray.length(); i++){
            try {
                JSONObject channelPointDataJson = channelPointArray.getJSONObject(i);
                ChannelPointData channelPointData = new ChannelPointData(channelPointDataJson.getString("id"));
                channelPointData.setCounter(channelPointDataJson.optLong("counter"));

                channelPointData.setMessage(channelPointDataJson.optString("message"));
                channelPointDataArrayList.add(channelPointData);
            }
            catch (JSONException e){
                Logging.getLogger().error(e.getMessage(), e);
            }
        }
        return channelPointDataArrayList;
    }
}
