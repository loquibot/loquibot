package com.alphalaneous.Interactive.Commands;

import com.alphalaneous.Defaults;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CommandData {

    private static final HashMap<String, CommandData> registeredAliases = new HashMap<>();
    private static final ArrayList<CommandData> registeredCommands = new ArrayList<>();

    private String command;
    private String description;
    private String message;
    private String userLevel;
    private boolean isEnabled = true;
    private boolean hasDescription = false;
    private boolean isMethod = false;
    private boolean isDefault = false;
    private boolean isGD = false;

    private List<Object> aliases;
    private int cooldown = 0;
    private long counter = 0;


    public CommandData(String command){
        this.command = command;
    }
    public void registerCommand(){
        registeredCommands.add(this);
    }
    public void deRegisterCommand(){
        registeredCommands.remove(this);
        if(aliases != null) {
            for (Object alias : aliases) {
                registeredAliases.remove(((String) alias).toLowerCase(Locale.ROOT), this);
            }
        }
    }


    public void setCommand(String command) {
        this.command = command;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDefault(boolean isDefault){
        this.isDefault = isDefault;
    }
    public void setGD(boolean isGD){
        this.isGD = isGD;
    }
    public void setHasDescription(boolean hasDescription){
        this.hasDescription = hasDescription;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUserLevel(String userLevel){
        this.userLevel = userLevel;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public void setEnabled(boolean isEnabled){
        this.isEnabled = isEnabled;
    }

    public void setMethod(boolean isMethod){
        this.isMethod = isMethod;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public void setAliases(List<Object> aliases) {
        this.aliases = aliases;
        for(Object alias : aliases){
            registeredAliases.put(((String) alias).toLowerCase(Locale.ROOT).trim(), this);
        }
    }

    public void addAliases(String alias){
        if(this.aliases == null) aliases = new ArrayList<>();
        this.aliases.add(alias);
        registeredAliases.put(alias, this);
    }

    public String getCommand() {
        return command;
    }

    public boolean isDefault(){
        return isDefault;
    }
    public boolean isGD(){
        return isGD;
    }
    public long getCounter(){
        return counter;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasDescription(){
        return hasDescription;
    }

    public String getMessage() {
        return message;
    }

    public boolean isMethod(){
        return isMethod;
    }

    public String getUserLevel(){
        return Objects.requireNonNullElse(userLevel, "everyone");
    }

    public List<Object> getAliases(){
        return Objects.requireNonNullElseGet(aliases, ArrayList::new);
    }

    public boolean isEnabled(){
        return isEnabled;
    }

    public int getCooldown(){
        return cooldown;
    }
    public static HashMap<String, CommandData> getRegisteredAliases(){
        return registeredAliases;
    }
    public static ArrayList<CommandData> getRegisteredCommands(){
        return registeredCommands;
    }

    private static void save(Path path, JSONObject object){
        try {
            Files.write(path, object.toString(4).getBytes());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void saveCustomCommands(){
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for(CommandData data : LoadCommands.getCustomCommands()){
            JSONObject commandObject = new JSONObject();
            commandObject.putOpt("name", data.getCommand());
            commandObject.putOpt("enabled", data.isEnabled());
            commandObject.putOpt("level", data.getUserLevel());
            commandObject.putOpt("aliases", data.getAliases());
            commandObject.putOpt("message", data.getMessage());
            commandObject.putOpt("cooldown", data.getCooldown());
            commandObject.putOpt("counter", data.getCounter());
            jsonArray.put(commandObject);
        }
        jsonObject.put("commands", jsonArray);
        save(Paths.get(Defaults.saveDirectory + "/loquibot/customCommands.json"), jsonObject);
    }
    public static void saveDefaultCommands(){
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for(CommandData data : LoadCommands.getDefaultCommands()){
            saveCommands(jsonArray, data);
        }
        jsonObject.put("commands", jsonArray);
        save(Paths.get(Defaults.saveDirectory + "/loquibot/defaultCommands.json"), jsonObject);
    }
    public static void saveGeometryDashCommands(){
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for(CommandData data : LoadCommands.getGeometryDashCommands()){
            saveCommands(jsonArray, data);
        }
        jsonObject.put("commands", jsonArray);
        save(Paths.get(Defaults.saveDirectory + "/loquibot/geometryDashCommands.json"), jsonObject);
    }

    private static void saveCommands(JSONArray jsonArray, CommandData data) {
        JSONObject commandObject = new JSONObject();
        commandObject.putOpt("name", data.getCommand());
        commandObject.putOpt("enabled", data.isEnabled);
        commandObject.putOpt("level", data.getUserLevel());
        commandObject.putOpt("cooldown", data.getCooldown());
        jsonArray.put(commandObject);
    }
}
