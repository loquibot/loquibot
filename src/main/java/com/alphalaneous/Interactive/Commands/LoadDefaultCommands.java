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
import java.util.List;

public class LoadDefaultCommands {

    public static ArrayList<DefaultCommandData> defaultCommands = new ArrayList<>();

    static {
        //todo methods
        DefaultCommandData setTitleCommand = new DefaultCommandData("title", data -> "", UserLevel.EVERYONE);
        DefaultCommandData setGameCommand = new DefaultCommandData("game", data -> "", UserLevel.EVERYONE);
        DefaultCommandData addCommand = new DefaultCommandData("addcommand", data -> "", UserLevel.MODERATOR);
        DefaultCommandData editCommand = new DefaultCommandData("editcommand", data -> "", UserLevel.MODERATOR);
        DefaultCommandData deleteCommand = new DefaultCommandData("deletecommand", data -> "", UserLevel.MODERATOR);
        DefaultCommandData getCommands = new DefaultCommandData("commands", data -> "", UserLevel.EVERYONE);
        DefaultCommandData getHelp = new DefaultCommandData("help", data -> "", UserLevel.EVERYONE);


        defaultCommands.addAll(List.of(setTitleCommand, setGameCommand, addCommand, editCommand, deleteCommand, getCommands, getHelp));
    }

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
                for (DefaultCommandData data : defaultCommands) {
                    if (data.getName().equals(commandDataJson.getString("name"))) {
                        data.setEnabled(commandDataJson.optBoolean("enabled", true));
                        data.setMessage(commandDataJson.optString("message"));
                        data.setCooldown(commandDataJson.optInt("cooldown"));
                        int level = commandDataJson.optInt("level", 0);
                        data.setUserLevel(UserLevel.parse(level));
                        break;
                    }
                }
            }
            catch (JSONException e){
                Logging.getLogger().error(e.getMessage(), e);
            }
        }
        return commandDataArrayList;
    }
}
