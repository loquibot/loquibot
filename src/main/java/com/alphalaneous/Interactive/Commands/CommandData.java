package com.alphalaneous.Interactive.Commands;

import com.alphalaneous.Interactive.CustomData;
import com.alphalaneous.Pages.CommandPages.CommandsPage;
import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.Utilities;
import com.alphalaneous.Enums.UserLevel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CommandData extends CustomData {

    private static final HashMap<String, CommandData> registeredAliases = new HashMap<>();
    private static final ArrayList<CommandData> registeredCommands = new ArrayList<>();

    private String command;
    private String message;
    private UserLevel userLevel = UserLevel.EVERYONE;
    private boolean isEnabled = true;
    private List<String> aliases;
    private int cooldown = 0;
    private long counter = 0;

    public CommandData(String command){
        this.command = command;
    }

    @Override
    public void register() {
        registeredCommands.add(this);
    }

    @Override
    public void deregister() {
        registeredCommands.remove(this);
        if(aliases != null) {
            for (String alias : aliases) {
                registeredAliases.remove(alias.toLowerCase(), this);
            }
        }
        saveCustomCommands(true);
    }

    @Override
    public void setName(String command) {
        this.command = command;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    public void setUserLevel(UserLevel userLevel){
        this.userLevel = userLevel;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    @Override
    public void setEnabled(boolean isEnabled){
        this.isEnabled = isEnabled;
    }

    @Override
    public void setCounter(long counter) {
        this.counter = counter;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
        for(String alias : aliases){
            registeredAliases.put(alias.toLowerCase().trim(), this);
        }
    }

    @Override
    public String getName() {
        return command;
    }

    @Override
    public long getCounter() {
        return counter;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void save(boolean reload) {
        saveCustomCommands(reload);
    }

    @Override
    public void save() {
        saveCustomCommands(false);
    }

    public UserLevel getUserLevel(){
        return Objects.requireNonNullElse(userLevel, UserLevel.EVERYONE);
    }

    public List<String> getAliases(){
        return Objects.requireNonNullElseGet(aliases, ArrayList::new);
    }

    @Override
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

    public static void saveCustomCommands(boolean reload) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        if (getRegisteredCommands() != null) {
            for (CommandData data : getRegisteredCommands()) {
                JSONObject commandObject = new JSONObject();
                commandObject.putOpt("name", data.getName());
                commandObject.putOpt("enabled", data.isEnabled());
                commandObject.putOpt("level", data.getUserLevel().value);
                commandObject.putOpt("aliases", data.getAliases());
                commandObject.putOpt("message", data.getMessage());
                commandObject.putOpt("cooldown", data.getCooldown());
                commandObject.putOpt("counter", data.getCounter());
                jsonArray.put(commandObject);
            }
            jsonObject.put("commands", jsonArray);
            try {
                Files.write(Paths.get(Utilities.saveDirectory + "/customCommands.json").toAbsolutePath(), jsonObject.toString(4).getBytes());
            }
            catch (Exception e){
                Logging.getLogger().error(e.getMessage(), e);
            }
        }
        if(reload) CommandsPage.load();
    }
}
