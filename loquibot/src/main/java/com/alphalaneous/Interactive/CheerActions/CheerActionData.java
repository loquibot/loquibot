package com.alphalaneous.Interactive.CheerActions;

import com.alphalaneous.Main;
import com.alphalaneous.Utils.Defaults;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

public class CheerActionData {

    private static final ArrayList<CheerActionData> registeredCheerActions = new ArrayList<>();

    private String name;
    private String message;
    private String userLevel;
    private long cheerAmount;

    private boolean isEnabled = true;
    private long counter = 0;

    public CheerActionData(String name){
        this.name = name;
    }
    public void registerCheerAction(){
        registeredCheerActions.add(this);
    }
    public void deregisterCheerAction(){
        registeredCheerActions.remove(this);
    }

    public long getCheerAmount() {
        return cheerAmount;
    }

    public void setCheerAmount(long cheerAmount) {
        this.cheerAmount = cheerAmount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUserLevel(String userLevel){
        this.userLevel = userLevel;
    }

    public void setEnabled(boolean isEnabled){
        this.isEnabled = isEnabled;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public String getName() {
        return name;
    }

    public long getCounter(){
        return counter;
    }

    public String getMessage() {
        return message;
    }

    public String getUserLevel(){
        return Objects.requireNonNullElse(userLevel, "everyone");
    }

    public boolean isEnabled(){
        return isEnabled;
    }

    public static ArrayList<CheerActionData> getRegisteredCheerActions(){
        return registeredCheerActions;
    }

    private static void save(Path path, JSONObject object){
        try {
            Files.write(path.toAbsolutePath(), object.toString(4).getBytes());
        }
        catch (Exception e){
            Main.logger.error(e.getLocalizedMessage(), e);
        }
    }

    public static void saveCustomCheerActions(){
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for(CheerActionData data : LoadCheerActions.getCustomCheerActions()){
            JSONObject commandObject = new JSONObject();
            commandObject.putOpt("name", data.getName());
            commandObject.putOpt("amount", data.getCheerAmount());
            commandObject.putOpt("enabled", data.isEnabled());
            commandObject.putOpt("level", data.getUserLevel());
            commandObject.putOpt("message", data.getMessage());
            commandObject.putOpt("counter", data.getCounter());
            jsonArray.put(commandObject);
        }
        jsonObject.put("cheerActions", jsonArray);
        save(Paths.get(Defaults.saveDirectory + "/loquibot/customCheerActions.json"), jsonObject);
    }
}
