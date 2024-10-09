package com.alphalaneous.Interactive.Commands;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Enums.UserLevel;
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

public class LoadDefaultCommands {

    public static ArrayList<DefaultCommandData> defaultCommands = new ArrayList<>();

    public static void createPathIfDoesntExist(Path path){
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);

                JSONObject object = new JSONObject();
                JSONArray array = new JSONArray();
                object.put("commands", array);

                Files.write(path, object.toString(4).getBytes());
            }
        }
        catch (Exception e){
            Logging.getLogger().error(e.getMessage(), e);
        }
    }

    @OnLoad
    public static void loadCommands(){
        try {
            Path customCommandPath = Paths.get(Utilities.saveDirectory + "/defaultCommands.json");
            createPathIfDoesntExist(customCommandPath);
            defaultCommands = loadJsonToCommandDataArrayList(Files.readString(customCommandPath, StandardCharsets.UTF_8));

            for(DefaultCommandData data : defaultCommands) data.register();
        }
        catch (Exception e){
            Logging.getLogger().error(e.getMessage(), e);
        }
    }

    private static ArrayList<DefaultCommandData> loadJsonToCommandDataArrayList(String jsonData){
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonData);
        }
        catch (Exception e){
            jsonData = "{\"commands\": []}";
            jsonObject = new JSONObject(jsonData);
        }

        JSONArray commandsArray = jsonObject.getJSONArray("commands");
        ArrayList<DefaultCommandData> commandDataArrayList = new ArrayList<>();
        for(int i = 0; i < commandsArray.length(); i++){
            try {
                JSONObject commandDataJson = commandsArray.getJSONObject(i);
                DefaultCommandData commandData = new DefaultCommandData(commandDataJson.getString("name"));
                commandData.setEnabled(commandDataJson.optBoolean("enabled", true));
                commandData.setMessage(commandDataJson.optString("message"));
                commandData.setCooldown(commandDataJson.optInt("cooldown"));
                int level = commandDataJson.optInt("level", 0);
                commandData.setUserLevel(UserLevel.parse(level));

                commandDataArrayList.add(commandData);
            }
            catch (JSONException e){
                Logging.getLogger().error(e.getMessage(), e);
            }
        }
        return commandDataArrayList;
    }
}
