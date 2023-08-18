package com.alphalaneous.Interactive.Keywords;

import com.alphalaneous.Main;
import com.alphalaneous.Utils.Defaults;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class KeywordData {

    private static final ArrayList<KeywordData> registeredKeywords = new ArrayList<>();

    private String keyword;
    private String message;
    private String userLevel;
    private boolean isEnabled = true;
    private boolean isRegex = false;

    private int cooldown = 0;
    private long counter = 0;
    private String foundWord;


    public KeywordData(String keyword){
        this.keyword = keyword;
    }
    public void registerKeyword(boolean isEdit){
        registeredKeywords.add(this);
        if(isEdit) saveCustomKeywords();
    }
    public void deregisterKeyword(){
        registeredKeywords.remove(this);
        saveCustomKeywords();
    }

    public void setKeyword(String keyword, boolean isEdit) {
        this.keyword = keyword;
        if(isEdit) saveCustomKeywords();

    }

    public void setRegex(boolean regex, boolean isEdit){
        this.isRegex = regex;
        if(isEdit) saveCustomKeywords();

    }
    public boolean isRegex(){
        return isRegex;
    }

    public void setFoundWord(String foundWord){
        this.foundWord = foundWord;
    }

    public void setMessage(String message, boolean isEdit) {
        this.message = message;
        if(isEdit) saveCustomKeywords();

    }

    public void setUserLevel(String userLevel, boolean isEdit){
        this.userLevel = userLevel;
        if(isEdit) saveCustomKeywords();

    }

    public void setCooldown(int cooldown, boolean isEdit) {
        this.cooldown = cooldown;
        if(isEdit) saveCustomKeywords();

    }

    public void setEnabled(boolean isEnabled, boolean isEdit){
        this.isEnabled = isEnabled;
        if(isEdit) saveCustomKeywords();

    }

    public void setCounter(long counter, boolean isEdit) {
        this.counter = counter;
        if(isEdit) saveCustomKeywords();

    }

    public String getKeyword() {
        return keyword;
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

    public int getCooldown(){
        return cooldown;
    }

    public String getFoundWord(){
        return foundWord;
    }

    public static ArrayList<KeywordData> getRegisteredKeywords(){
        return registeredKeywords;
    }

    private static void save(Path path, JSONObject object){
        try {
            Files.write(path.toAbsolutePath(), object.toString(4).getBytes());
        }
        catch (Exception e){
            Main.logger.error(e.getLocalizedMessage(), e);
        }
    }

    public static void saveCustomKeywords(){
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for(KeywordData data : LoadKeywords.getCustomKeywords()){
            JSONObject commandObject = new JSONObject();
            commandObject.putOpt("name", data.getKeyword());
            commandObject.putOpt("enabled", data.isEnabled());
            commandObject.putOpt("isRegex", data.isRegex());
            commandObject.putOpt("level", data.getUserLevel());
            commandObject.putOpt("message", data.getMessage());
            commandObject.putOpt("cooldown", data.getCooldown());
            commandObject.putOpt("counter", data.getCounter());
            jsonArray.put(commandObject);
        }
        jsonObject.put("keywords", jsonArray);
        save(Paths.get(Defaults.saveDirectory + "/loquibot/customKeywords.json"), jsonObject);
    }
}
