package com.alphalaneous.Interactive.Timers;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Utilities.Utilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class LoadTimers {

    public static ArrayList<TimerData> customTimers = new ArrayList<>();
    public static void createPathIfDoesntExist(Path path){
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);

                JSONObject object = new JSONObject();
                JSONArray array = new JSONArray();
                object.put("timers", array);

                Files.write(path, object.toString(4).getBytes());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @OnLoad
    public static void loadTimers(){
        try {
            Path customTimerPath = Paths.get(Utilities.saveDirectory + "customTimers.json");
            createPathIfDoesntExist(customTimerPath);
            String text = Files.readString(customTimerPath, StandardCharsets.UTF_8);
            customTimers = loadJsonToTimerDataArrayList(text);

            for(TimerData data : customTimers) {
                data.register();
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private static ArrayList<TimerData> loadJsonToTimerDataArrayList(String jsonData){
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonData);
        }
        catch (Exception e){
            jsonObject = new JSONObject();
        }
        JSONArray timersArray = jsonObject.getJSONArray("timers");
        ArrayList<TimerData> timerDataArrayList = new ArrayList<>();
        for(int i = 0; i < timersArray.length(); i++){
            try {
                JSONObject timerDataJson = timersArray.getJSONObject(i);
                TimerData timerData = new TimerData(timerDataJson.getString("name"));
                timerData.setMessage(timerDataJson.getString("message"));
                timerData.setInterval(timerDataJson.optInt("interval"));

                timerData.setEnabled(timerDataJson.optBoolean("enabled", true));

                timerData.setLines(timerDataJson.optInt("lines"));
                timerData.setRunCommand(timerDataJson.optString("runCommand"));

                timerDataArrayList.add(timerData);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        return timerDataArrayList;
    }
}
