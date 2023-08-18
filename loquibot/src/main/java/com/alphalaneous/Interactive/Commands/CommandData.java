package com.alphalaneous.Interactive.Commands;

import com.alphalaneous.Main;
import com.alphalaneous.Utils.Defaults;
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
    private boolean isMediaShare = false;

    private List<Object> aliases;
    private int cooldown = 0;
    private long counter = 0;

    public CommandData(String command){
        this.command = command;
    }
    public void registerCommand(boolean isEdit){
        registeredCommands.add(this);
        if(isEdit) saveAll();
    }
    public void deRegisterCommand(){
        registeredCommands.remove(this);
        if(aliases != null) {
            for (Object alias : aliases) {
                registeredAliases.remove(((String) alias).toLowerCase(), this);
            }
        }
        saveAll();
    }


    public void setCommand(String command, boolean isEdit) {
        this.command = command;
        if(isEdit) saveAll();

    }

    public void setDescription(String description, boolean isEdit) {
        this.description = description;
        if(isEdit) saveAll();

    }

    public void setDefault(boolean isDefault, boolean isEdit){
        this.isDefault = isDefault;
        if(isEdit) saveAll();

    }
    public void setGD(boolean isGD, boolean isEdit){
        this.isGD = isGD;
        if(isEdit) saveAll();

    }

    public void setMediaShare(boolean mediaShare, boolean isEdit){
        this.isMediaShare = mediaShare;
        if(isEdit) saveAll();

    }


    public void setHasDescription(boolean hasDescription, boolean isEdit){
        this.hasDescription = hasDescription;
        if(isEdit) saveAll();

    }

    public void setMessage(String message, boolean isEdit) {
        this.message = message;
        if(isEdit) saveAll();

    }

    public void setUserLevel(String userLevel, boolean isEdit){
        this.userLevel = userLevel;
        if(isEdit) saveAll();

    }

    public void setCooldown(int cooldown, boolean isEdit) {
        this.cooldown = cooldown;
        if(isEdit) saveAll();

    }

    public void setEnabled(boolean isEnabled, boolean isEdit){
        this.isEnabled = isEnabled;
        if(isEdit) saveAll();

    }

    public void setMethod(boolean isMethod, boolean isEdit){
        this.isMethod = isMethod;
        if(isEdit) saveAll();

    }
    public void setCounter(long counter, boolean isEdit) {
        this.counter = counter;
        if(isEdit) saveAll();
    }

    public void setAliases(List<Object> aliases, boolean isEdit) {
        this.aliases = aliases;
        for(Object alias : aliases){
            registeredAliases.put(((String) alias).toLowerCase().trim(), this);
        }
        if(isEdit) saveAll();
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

    public boolean isMediaShare(){
        return isMediaShare;
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
            Main.logger.error(e.getLocalizedMessage(), e);
        }
    }

    public static void saveAll(){
        saveCustomCommands();
        saveDefaultCommands();
        saveGeometryDashCommands();
    }

    public static void saveCustomCommands() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        if (LoadCommands.getCustomCommands() != null) {
            for (CommandData data : LoadCommands.getCustomCommands()) {
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
            save(Paths.get(Defaults.saveDirectory + "/loquibot/customCommands.json").toAbsolutePath(), jsonObject);
        }
    }
    public static void saveDefaultCommands() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        if (LoadCommands.getDefaultCommands() != null) {
            for (CommandData data : LoadCommands.getDefaultCommands()) {
                saveCommands(jsonArray, data);
            }
            jsonObject.put("commands", jsonArray);
            save(Paths.get(Defaults.saveDirectory + "/loquibot/defaultCommands.json").toAbsolutePath(), jsonObject);
        }
    }
    public static void saveGeometryDashCommands() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        if (LoadCommands.getGeometryDashCommands() != null) {
            for (CommandData data : LoadCommands.getGeometryDashCommands()) {
                saveCommands(jsonArray, data);
            }
            jsonObject.put("commands", jsonArray);
            save(Paths.get(Defaults.saveDirectory + "/loquibot/geometryDashCommands.json").toAbsolutePath(), jsonObject);
        }
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
