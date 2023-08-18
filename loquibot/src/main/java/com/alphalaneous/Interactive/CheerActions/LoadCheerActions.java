package com.alphalaneous.Interactive.CheerActions;

import com.alphalaneous.Main;
import com.alphalaneous.Tabs.ChatbotPages.CustomKeywords;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Utils.Utilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class LoadCheerActions {

    public static ArrayList<CheerActionData> customCheerActions = new ArrayList<>();
    public static ArrayList<CheerActionData> getCustomCheerActions(){
        return customCheerActions;
    }


    public static void createPathIfDoesntExist(Path path){
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);

                JSONObject object = new JSONObject();
                JSONArray array = new JSONArray();
                object.put("cheerActions", array);

                Files.write(path, object.toString(4).getBytes());
            }
        }
        catch (Exception e){
            Main.logger.error(e.getLocalizedMessage(), e);
        }
    }

    public static void reloadCustomCheerActions(){
        customCheerActions.clear();
        customCheerActions.addAll(CheerActionData.getRegisteredCheerActions());
        CustomKeywords.loadKeywords();
    }

    public static void loadCheerActions(){
        try {
            Path customCommandPath = Paths.get(Defaults.saveDirectory + "/loquibot/customCheerActions.json");
            createPathIfDoesntExist(customCommandPath);
            customCheerActions = loadJsonToKeywordDataArrayList(Files.readString(customCommandPath, StandardCharsets.UTF_8));

            for(CheerActionData data : customCheerActions) data.registerCheerAction();

        }
        catch (Exception e){
            Main.logger.error(e.getLocalizedMessage(), e);
        }
        CustomKeywords.loadKeywords();
    }

    private static ArrayList<CheerActionData> loadJsonToKeywordDataArrayList(String jsonData){
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonData);
        }
        catch (Exception e){
            jsonObject = new JSONObject();
        }
        JSONArray cheerActionArray = jsonObject.getJSONArray("cheerActions");
        ArrayList<CheerActionData> commandDataArrayList = new ArrayList<>();
        for(int i = 0; i < cheerActionArray.length(); i++){
            try {
                JSONObject commandDataJson = cheerActionArray.getJSONObject(i);
                CheerActionData cheerActionData = new CheerActionData(commandDataJson.getString("name"));
                cheerActionData.setCounter(commandDataJson.optLong("counter"));
                cheerActionData.setEnabled(commandDataJson.optBoolean("enabled", true));
                cheerActionData.setCheerAmount(commandDataJson.optLong("amount", -1));
                cheerActionData.setMessage(commandDataJson.optString("message"));
                String level = commandDataJson.optString("level");
                if(level.equalsIgnoreCase("")) level = "everyone";
                cheerActionData.setUserLevel(level);

                commandDataArrayList.add(cheerActionData);
            }
            catch (JSONException e){
                Main.logger.error(e.getLocalizedMessage(), e);
            }
        }
        return commandDataArrayList;
    }

    private static String readIntoString(BufferedReader reader){
        return Utilities.readIntoString(reader);
    }
}
