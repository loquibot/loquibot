package com.alphalaneous.Interactive.Keywords;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Enums.UserLevel;
import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.Utilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class LoadKeywords {

    public static ArrayList<KeywordData> customKeywords = new ArrayList<>();

    public static void createPathIfDoesntExist(Path path){
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);

                JSONObject object = new JSONObject();
                JSONArray array = new JSONArray();
                object.put("keywords", array);

                Files.write(path, object.toString(4).getBytes());
            }
        }
        catch (Exception e){
            Logging.getLogger().error(e.getMessage(), e);
        }
    }

    @OnLoad
    public static void loadKeywords(){
        try {
            Path customCommandPath = Paths.get(Utilities.saveDirectory + "/customKeywords.json");
            createPathIfDoesntExist(customCommandPath);
            customKeywords = loadJsonToKeywordDataArrayList(Files.readString(customCommandPath, StandardCharsets.UTF_8));

            for(KeywordData data : customKeywords) data.register();

        }
        catch (Exception e){
            Logging.getLogger().error(e.getMessage(), e);
        }
    }

    private static ArrayList<KeywordData> loadJsonToKeywordDataArrayList(String jsonData){
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonData);
        }
        catch (Exception e){
            jsonObject = new JSONObject();
        }
        JSONArray keywordArray = jsonObject.getJSONArray("keywords");
        ArrayList<KeywordData> commandDataArrayList = new ArrayList<>();
        for(int i = 0; i < keywordArray.length(); i++){
            try {
                JSONObject commandDataJson = keywordArray.getJSONObject(i);
                KeywordData keywordData = new KeywordData(commandDataJson.getString("name"));
                keywordData.setCounter(commandDataJson.optLong("counter"));
                keywordData.setEnabled(commandDataJson.optBoolean("enabled", true));

                keywordData.setMessage(commandDataJson.optString("message"));
                keywordData.setCooldown(commandDataJson.optInt("cooldown"));
                int level = commandDataJson.optInt("level", 0);
                keywordData.setUserLevel(UserLevel.parse(level));

                commandDataArrayList.add(keywordData);
            }
            catch (JSONException e){
                Logging.getLogger().error(e.getMessage(), e);
            }
        }
        return commandDataArrayList;
    }
}
