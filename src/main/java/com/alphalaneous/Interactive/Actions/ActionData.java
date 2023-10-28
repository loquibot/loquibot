package com.alphalaneous.Interactive.Actions;

import com.alphalaneous.Interactive.CustomData;
import com.alphalaneous.Pages.ActionsPage;
import com.alphalaneous.Utilities.Utilities;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ActionData extends CustomData {

    private static final ArrayList<ActionData> registeredActions = new ArrayList<>();

    private String name;
    private String message;
    private long counter = 0;

    public ActionData(String command){
        this.name = command;
    }


    @Override
    public void register() {
        registeredActions.add(this);
    }

    @Override
    public void deregister() {
        registeredActions.remove(this);
        saveCustomActions(true);
    }

    @Override
    public void setName(String command) {
        this.name = command;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void setCounter(long counter) {
        this.counter = counter;
    }

    @Override
    public void setEnabled(boolean enabled) {

    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return name;
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
        saveCustomActions(reload);
    }

    @Override
    public void save() {
        saveCustomActions(false);
    }

    public static ArrayList<ActionData> getRegisteredActions(){
        return registeredActions;
    }

    public static void saveCustomActions(boolean reload) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (ActionData data : registeredActions) {
            JSONObject actionObject = new JSONObject();
            actionObject.putOpt("name", data.getName());
            actionObject.putOpt("message", data.getMessage());
            actionObject.putOpt("counter", data.getCounter());
            jsonArray.put(actionObject);
        }
        jsonObject.put("actions", jsonArray);

        try {
            Files.write(Paths.get(Utilities.saveDirectory + "customActions.json").toAbsolutePath(), jsonObject.toString(4).getBytes());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        if(reload) ActionsPage.load();
    }
}
