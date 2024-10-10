package com.alphalaneous.Interactive.Commands;

import com.alphalaneous.Interactive.CustomData;
import com.alphalaneous.Interfaces.Function;
import com.alphalaneous.Interfaces.InternalFunction;
import com.alphalaneous.Pages.CommandPages.CommandsPage;
import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.Utilities;
import com.alphalaneous.Enums.UserLevel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

public class DefaultCommandData extends CommandData {

    private static final ArrayList<DefaultCommandData> registeredCommands = new ArrayList<>();

    private String command;
    private UserLevel userLevel;
    private boolean isEnabled = true;
    private int cooldown = 0;
    private InternalFunction function;
    private String id;

    public DefaultCommandData(String command, InternalFunction function, UserLevel userLevel, String id) {
        super(command);
        this.command = command;
        this.function = function;
        this.id = id;
        this.userLevel = userLevel;
        register();
    }

    @Override
    public void register() {
        registeredCommands.add(this);
    }

    @Override
    public void deregister() {
        registeredCommands.remove(this);
        saveCustomCommands(true);
    }

    @Override
    public void setName(String command) {
        this.command = command;
    }

    @Override
    public void setMessage(String message) {
    }

    public void setUserLevel(UserLevel userLevel){
        this.userLevel = userLevel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
    }

    @Override
    public String getName() {
        return command;
    }

    @Override
    public long getCounter() {
        return 0;
    }

    public InternalFunction getFunction() {
        return function;
    }

    public void setFunction(InternalFunction function) {
        this.function = function;
    }

    @Override
    public String getMessage() {
        return "$(internal-function)";
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

    @Override
    public boolean isEnabled(){
        return isEnabled;
    }

    public int getCooldown(){
        return cooldown;
    }

    public static ArrayList<DefaultCommandData> getRegisteredDefaultCommands(){
        return registeredCommands;
    }

    public static void saveCustomCommands(boolean reload) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        if (getRegisteredDefaultCommands() != null) {
            for (DefaultCommandData data : getRegisteredDefaultCommands()) {
                JSONObject commandObject = new JSONObject();
                commandObject.putOpt("name", data.getName());
                commandObject.putOpt("enabled", data.isEnabled());
                commandObject.putOpt("level", data.getUserLevel().value);
                commandObject.putOpt("cooldown", data.getCooldown());
                jsonArray.put(commandObject);
            }
            jsonObject.put("commands", jsonArray);
            try {
                Files.write(Paths.get(Utilities.saveDirectory + "/defaultCommands.json").toAbsolutePath(), jsonObject.toString(4).getBytes());
            }
            catch (Exception e){
                Logging.getLogger().error(e.getMessage(), e);
            }
        }
        if(reload) CommandsPage.load();
    }
}
