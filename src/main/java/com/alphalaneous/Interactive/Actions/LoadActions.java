package com.alphalaneous.Interactive.Actions;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Utilities.Utilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class LoadActions {

    public static ArrayList<ActionData> customActions = new ArrayList<>();
    public static ArrayList<ActionData> getCustomActions(){
        return customActions;
    }

    public static void createPathIfDoesntExist(Path path){
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);

                JSONObject object = new JSONObject();
                JSONArray array = new JSONArray();
                object.put("actions", array);

                Files.write(path, object.toString(4).getBytes());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @OnLoad

    public static void loadActions(){
        try {
            Path customActionPath = Paths.get(Utilities.saveDirectory + "customActions.json");
            createPathIfDoesntExist(customActionPath);
            customActions = loadJsonToActionDataArrayList(Files.readString(customActionPath, StandardCharsets.UTF_8));

            for(ActionData data : customActions) data.register();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private static ArrayList<ActionData> loadJsonToActionDataArrayList(String jsonData){
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonData);
        }
        catch (Exception e){
            jsonData = "{\"actions\": []}";
            jsonObject = new JSONObject(jsonData);
        }

        JSONArray actionsArray = jsonObject.getJSONArray("actions");
        ArrayList<ActionData> actionDataArrayList = new ArrayList<>();
        for(int i = 0; i < actionsArray.length(); i++){
            try {
                JSONObject actionDataJson = actionsArray.getJSONObject(i);
                ActionData actionData = new ActionData(actionDataJson.getString("name"));
                actionData.setCounter(actionDataJson.optLong("counter"));

                actionData.setMessage(actionDataJson.optString("message"));
                actionDataArrayList.add(actionData);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        return actionDataArrayList;
    }
}
