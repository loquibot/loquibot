package com.alphalaneous.Interactive.Commands;

import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Main;
import com.alphalaneous.Tabs.ChatbotPages.DefaultCommands;
import com.alphalaneous.Tabs.ChatbotPages.CustomCommands;
import com.alphalaneous.Utils.Utilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

public class LoadCommands {

    public static ArrayList<CommandData> defaultCommands;
    public static ArrayList<CommandData> geometryDashCommands;
    public static ArrayList<CommandData> mediaShareCommands;
    public static ArrayList<CommandData> customCommands = new ArrayList<>();

    private static final BufferedReader defaultCommandsReader = new BufferedReader(
            new InputStreamReader(Objects.requireNonNull(
                    Main.class.getClassLoader().getResourceAsStream("Commands/default.json"))));
    private static final BufferedReader geometryCommandsReader = new BufferedReader(
            new InputStreamReader(Objects.requireNonNull(
                    Main.class.getClassLoader().getResourceAsStream("Commands/geometry.json"))));
    private static final BufferedReader mediaShareCommandsReader = new BufferedReader(
            new InputStreamReader(Objects.requireNonNull(
                    Main.class.getClassLoader().getResourceAsStream("Commands/mediaShare.json"))));


    public static ArrayList<CommandData> getDefaultCommands(){
        return defaultCommands;
    }
    public static ArrayList<CommandData> getCustomCommands(){
        return customCommands;
    }
    public static ArrayList<CommandData> getGeometryDashCommands(){
        return geometryDashCommands;
    }
    public static ArrayList<CommandData> getMediaShareCommands(){
        return mediaShareCommands;
    }

    private static void combineData(ArrayList<CommandData> original, ArrayList<CommandData> toCombine){

        for(CommandData data : original){
            for(CommandData combineData : toCombine){
                if(data.getCommand().equalsIgnoreCase(combineData.getCommand())){
                    data.setEnabled(combineData.isEnabled());
                    data.setCooldown(combineData.getCooldown());
                    data.setUserLevel(combineData.getUserLevel());
                }
            }
        }
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
            e.printStackTrace();
        }
    }

    public static void reloadCustomCommands(){
        customCommands.clear();
        for(CommandData data : CommandData.getRegisteredCommands()){
            if(!data.isDefault()){
                customCommands.add(data);
            }
        }
        CustomCommands.loadCommands();
    }

    public static void loadCommands(){
        try {
            defaultCommands = loadJsonToCommandDataArrayList(readIntoString(defaultCommandsReader), true, false, false);
            Path defaultCommandPath = Paths.get(Defaults.saveDirectory + "/loquibot/defaultCommands.json");
            createPathIfDoesntExist(defaultCommandPath);
            ArrayList<CommandData> defaultCommandSettings = loadJsonToCommandDataArrayList(Files.readString(defaultCommandPath, StandardCharsets.UTF_8));
            combineData(defaultCommands, defaultCommandSettings);

            for(CommandData data : defaultCommands) data.registerCommand();

            geometryDashCommands = loadJsonToCommandDataArrayList(readIntoString(geometryCommandsReader), true, true, false);
            Path geometryDashCommandPath = Paths.get(Defaults.saveDirectory + "/loquibot/geometryDashCommands.json");
            createPathIfDoesntExist(geometryDashCommandPath);
            ArrayList<CommandData> geometryDashCommandSettings = loadJsonToCommandDataArrayList(Files.readString(geometryDashCommandPath, StandardCharsets.UTF_8));
            combineData(geometryDashCommands, geometryDashCommandSettings);

            for(CommandData data : geometryDashCommands) data.registerCommand();

            mediaShareCommands = loadJsonToCommandDataArrayList(readIntoString(mediaShareCommandsReader), true, false, true);
            Path mediaShareCommandPath = Paths.get(Defaults.saveDirectory + "/loquibot/mediaShareCommands.json");
            createPathIfDoesntExist(mediaShareCommandPath);
            ArrayList<CommandData> mediaShareCommandSettings = loadJsonToCommandDataArrayList(Files.readString(mediaShareCommandPath, StandardCharsets.UTF_8));
            combineData(mediaShareCommands, mediaShareCommandSettings);

            for(CommandData data : mediaShareCommands) data.registerCommand();

            Path customCommandPath = Paths.get(Defaults.saveDirectory + "/loquibot/customCommands.json");
            createPathIfDoesntExist(customCommandPath);
            customCommands = loadJsonToCommandDataArrayList(Files.readString(customCommandPath, StandardCharsets.UTF_8));

            for(CommandData data : customCommands) data.registerCommand();

        }
        catch (Exception e){
            e.printStackTrace();
        }
        DefaultCommands.loadCommands();
        CustomCommands.loadCommands();
    }

    private static ArrayList<CommandData> loadJsonToCommandDataArrayList(String jsonData){
        return loadJsonToCommandDataArrayList(jsonData, false, false, false);
    }

    private static ArrayList<CommandData> loadJsonToCommandDataArrayList(String jsonData, boolean isDefault, boolean isGD, boolean isMediaShare){
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonData);
        }
        catch (Exception e){
            jsonObject = new JSONObject();
        }
        JSONArray commandsArray = jsonObject.getJSONArray("commands");
        ArrayList<CommandData> commandDataArrayList = new ArrayList<>();
        for(int i = 0; i < commandsArray.length(); i++){
            try {
                JSONObject commandDataJson = commandsArray.getJSONObject(i);
                CommandData commandData = new CommandData(commandDataJson.getString("name"));
                commandData.setCounter(commandDataJson.optLong("counter"));
                commandData.setDefault(isDefault);
                commandData.setGD(isGD);
                commandData.setMediaShare(isMediaShare);
                commandData.setHasDescription(!commandDataJson.optString("description").equalsIgnoreCase(""));
                if(!commandDataJson.optString("description").equalsIgnoreCase(""))
                    commandData.setDescription(commandDataJson.optString("description"));
                else commandData.setDescription(commandDataJson.optString("message"));
                commandData.setEnabled(commandDataJson.optBoolean("enabled", true));
                commandData.setMethod(commandDataJson.optBoolean("runMethod"));

                commandData.setMessage(commandDataJson.optString("message"));
                commandData.setCooldown(commandDataJson.optInt("cooldown"));
                String level = commandDataJson.optString("level");
                if(level.equalsIgnoreCase("")){
                    level = "everyone";
                }
                commandData.setUserLevel(level);
                JSONArray array = commandDataJson.optJSONArray("aliases");
                if(array != null){
                    commandData.setAliases(array.toList());
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
