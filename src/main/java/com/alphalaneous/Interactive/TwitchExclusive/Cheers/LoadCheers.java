package com.alphalaneous.Interactive.TwitchExclusive.Cheers;

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

public class LoadCheers {

    public static ArrayList<CheerData> customCheers = new ArrayList<>();

    public static void createPathIfDoesntExist(Path path){
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);

                JSONObject object = new JSONObject();
                JSONArray array = new JSONArray();
                object.put("cheers", array);

                Files.write(path, object.toString(4).getBytes());
            }
        }
        catch (Exception e){
            Logging.getLogger().error(e.getMessage(), e);
        }
    }

    @OnLoad
    public static void loadCheers(){
        try {
            Path customCheerPath = Paths.get(Utilities.saveDirectory + "/customCheers.json");
            createPathIfDoesntExist(customCheerPath);
            customCheers = loadJsonToCheerDataArrayList(Files.readString(customCheerPath, StandardCharsets.UTF_8));

            for(CheerData data : customCheers) data.register();
        }
        catch (Exception e){
            Logging.getLogger().error(e.getMessage(), e);
        }
    }

    private static ArrayList<CheerData> loadJsonToCheerDataArrayList(String jsonData){
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonData);
        }
        catch (Exception e){
            jsonData = "{\"cheers\": []}";
            jsonObject = new JSONObject(jsonData);
        }

        JSONArray cheerArray = jsonObject.getJSONArray("cheers");
        ArrayList<CheerData> cheerDataArrayList = new ArrayList<>();
        for(int i = 0; i < cheerArray.length(); i++){
            try {
                JSONObject cheerDataJson = cheerArray.getJSONObject(i);
                CheerData cheerData = new CheerData(cheerDataJson.getString("name"));
                cheerData.setEnabled(cheerDataJson.optBoolean("enabled", true));
                cheerData.setCounter(cheerDataJson.optLong("counter"));
                cheerData.setAnyAmount(cheerDataJson.optBoolean("anyAmount"));
                cheerData.setRange(cheerDataJson.optString("range"));
                cheerData.setMessage(cheerDataJson.optString("message"));
                cheerDataArrayList.add(cheerData);
            }
            catch (JSONException e){
                Logging.getLogger().error(e.getMessage(), e);
            }
        }
        return cheerDataArrayList;
    }
}
