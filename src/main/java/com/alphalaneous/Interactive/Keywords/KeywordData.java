package com.alphalaneous.Interactive.Keywords;

import com.alphalaneous.Interactive.CustomData;
import com.alphalaneous.Pages.CommandPages.KeywordsPage;
import com.alphalaneous.Utilities.Utilities;
import org.json.JSONArray;
import org.json.JSONObject;
import com.alphalaneous.Enums.UserLevel;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

public class KeywordData extends CustomData {

    private static final ArrayList<KeywordData> registeredKeywords = new ArrayList<>();

    private String keyword;
    private String message;
    private UserLevel userLevel;
    private boolean isEnabled = true;

    private int cooldown = 0;
    private long counter = 0;


    public KeywordData(String keyword){
        this.keyword = keyword;
    }

    @Override
    public void register(){
        registeredKeywords.add(this);
    }

    @Override
    public void deregister(){
        registeredKeywords.remove(this);
        saveCustomKeywords(true);
    }

    @Override
    public void setName(String keyword) {
        this.keyword = keyword;

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

    @Override
    public String getName() {
        return keyword;
    }

    @Override
    public long getCounter(){
        return counter;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void save(boolean reload) {
        saveCustomKeywords(reload);
    }

    @Override
    public void save() {
        saveCustomKeywords(false);
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


    public static ArrayList<KeywordData> getRegisteredKeywords(){
        return registeredKeywords;
    }


    public static void saveCustomKeywords(boolean reload){
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for(KeywordData data : registeredKeywords){
            JSONObject keywordObject = new JSONObject();
            keywordObject.putOpt("name", data.getName());
            keywordObject.putOpt("enabled", data.isEnabled());
            keywordObject.putOpt("level", data.getUserLevel().value);
            keywordObject.putOpt("message", data.getMessage());
            keywordObject.putOpt("cooldown", data.getCooldown());
            keywordObject.putOpt("counter", data.getCounter());
            jsonArray.put(keywordObject);
        }
        jsonObject.put("keywords", jsonArray);

        try {
            Files.write(Paths.get(Utilities.saveDirectory + "customKeywords.json").toAbsolutePath(), jsonObject.toString(4).getBytes());
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if (reload) KeywordsPage.load();
    }
}
