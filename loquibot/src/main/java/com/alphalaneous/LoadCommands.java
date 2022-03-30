package com.alphalaneous;

import com.alphalaneous.ChatbotTab.DefaultCommands;
import com.alphalaneous.ChatbotTab.CustomCommands;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class LoadCommands {

    public static ArrayList<CommandData> defaultCommands;
    public static ArrayList<CommandData> geometryDashCommands;
    public static ArrayList<CommandData> customCommands = new ArrayList<>();

    private static final BufferedReader defaultCommandsReader = new BufferedReader(
            new InputStreamReader(Objects.requireNonNull(
                    Main.class.getClassLoader().getResourceAsStream("Commands/default.json"))));
    private static final BufferedReader geometryCommandsReader = new BufferedReader(
            new InputStreamReader(Objects.requireNonNull(
                    Main.class.getClassLoader().getResourceAsStream("Commands/geometry.json"))));

    public static ArrayList<CommandData> getDefaultCommands(){
        return defaultCommands;
    }
    public static ArrayList<CommandData> getCustomCommands(){
        return customCommands;
    }
    public static ArrayList<CommandData> getGeometryDashCommands(){
        return geometryDashCommands;
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
            defaultCommands = loadJsonToCommandDataArrayList(readIntoString(defaultCommandsReader), true, false);
            Path defaultCommandPath = Paths.get(Defaults.saveDirectory + "/loquibot/defaultCommands.json");
            createPathIfDoesntExist(defaultCommandPath);
            ArrayList<CommandData> defaultCommandSettings = loadJsonToCommandDataArrayList(Files.readString(defaultCommandPath, StandardCharsets.UTF_8));
            combineData(defaultCommands, defaultCommandSettings);

            for(CommandData data : defaultCommands) data.registerCommand();

            geometryDashCommands = loadJsonToCommandDataArrayList(readIntoString(geometryCommandsReader), true, true);
            Path geometryDashCommandPath = Paths.get(Defaults.saveDirectory + "/loquibot/geometryDashCommands.json");
            createPathIfDoesntExist(geometryDashCommandPath);
            ArrayList<CommandData> geometryDashCommandSettings = loadJsonToCommandDataArrayList(Files.readString(geometryDashCommandPath, StandardCharsets.UTF_8));
            combineData(geometryDashCommands, geometryDashCommandSettings);

            for(CommandData data : geometryDashCommands) data.registerCommand();

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
        return loadJsonToCommandDataArrayList(jsonData, false, false);
    }

    private static ArrayList<CommandData> loadJsonToCommandDataArrayList(String jsonData, boolean isDefault, boolean isGD){
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray commandsArray = jsonObject.getJSONArray("commands");
        ArrayList<CommandData> commandDataArrayList = new ArrayList<>();
        for(int i = 0; i < commandsArray.length(); i++){
            try {
                JSONObject commandDataJson = commandsArray.getJSONObject(i);
                CommandData commandData = new CommandData(commandDataJson.getString("name"));
                commandData.setCounter(commandDataJson.optInt("counter"));
                commandData.setDefault(isDefault);
                commandData.setGD(isGD);
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
        StringBuilder builder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
        }
        catch (IOException ignored){}
        return builder.toString();
    }
}
