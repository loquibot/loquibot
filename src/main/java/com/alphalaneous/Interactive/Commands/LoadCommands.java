package com.alphalaneous.Interactive.Commands;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Enums.UserLevel;
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
import java.util.Objects;
import java.util.stream.Collectors;

public class LoadCommands {

    public static ArrayList<CommandData> customCommands = new ArrayList<>();

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
            e.printStackTrace();
        }
    }

    @OnLoad
    public static void loadCommands(){
        try {
            Path customCommandPath = Paths.get(Utilities.saveDirectory + "customCommands.json");
            createPathIfDoesntExist(customCommandPath);
            customCommands = loadJsonToCommandDataArrayList(Files.readString(customCommandPath, StandardCharsets.UTF_8));

            for(CommandData data : customCommands) data.register();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private static ArrayList<CommandData> loadJsonToCommandDataArrayList(String jsonData){
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonData);
        }
        catch (Exception e){
            jsonData = "{\"commands\": []}";
            jsonObject = new JSONObject(jsonData);
        }

        JSONArray commandsArray = jsonObject.getJSONArray("commands");
        ArrayList<CommandData> commandDataArrayList = new ArrayList<>();
        for(int i = 0; i < commandsArray.length(); i++){
            try {
                JSONObject commandDataJson = commandsArray.getJSONObject(i);
                CommandData commandData = new CommandData(commandDataJson.getString("name"));
                commandData.setCounter(commandDataJson.optLong("counter"));

                commandData.setEnabled(commandDataJson.optBoolean("enabled", true));

                commandData.setMessage(commandDataJson.optString("message"));
                commandData.setCooldown(commandDataJson.optInt("cooldown"));
                int level = commandDataJson.optInt("level", 0);
                commandData.setUserLevel(UserLevel.parse(level));
                JSONArray array = commandDataJson.optJSONArray("aliases");
                if(array != null){
                    commandData.setAliases(array.toList().stream()
                            .map(objectA -> Objects.toString(objectA, null)).collect(Collectors.toList()));
                }
                commandDataArrayList.add(commandData);
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
