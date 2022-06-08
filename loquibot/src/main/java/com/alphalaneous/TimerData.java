package com.alphalaneous;

import com.alphalaneous.TwitchBot.ChatMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TimerData {

    private static final ArrayList<TimerData> registeredTimers = new ArrayList<>();


    private String name;
    private String message;
    private boolean isEnabled = true;
    private int interval = 5;
    private int lines = 2;

    private String runCommand = null;

    public TimerData(String name, String message){
        this.name = name;
        this.message = message;
    }

    public void registerTimer(){
        registeredTimers.add(this);
    }
    public void deRegisterTimer(){
        registeredTimers.remove(this);
    }

    public void setName(String name){
        this.name = name;
    }
    public void setInterval(int interval){
        this.interval = interval;
    }
    public void setLines(int lines){
        this.lines = lines;
    }
    public void setEnabled(boolean isEnabled){
        this.isEnabled = isEnabled;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public void setRunCommand(String runCommand){
        this.runCommand = runCommand;
    }

    public String getName(){
        return name;
    }
    public boolean isEnabled(){
        return isEnabled;
    }
    public String getMessage(){
        return message;
    }
    public int getInterval(){
        return interval;
    }
    public int getLines(){
        return lines;
    }
    public String getRunCommand(){
        return runCommand;
    }
    public static ArrayList<TimerData> getRegisteredTimers(){
        return registeredTimers;
    }

    public void runTimer(int minute){
        if(minute % interval == 0 && isEnabled && ChatListener.SelfDestructingMessage.getSize() >= lines) {
            if (runCommand != null && !runCommand.equalsIgnoreCase("")) {
                String[] args = runCommand.split(" ");
                ChatMessage message = new ChatMessage(args, "TimerHandler", "TimerHandler", runCommand, new String[0], true, true, true, 0, false);
                CommandNew.run(message);
                message.setYouTube(true);
                CommandNew.run(message);
            } else {
                ChatMessage chatMessage = new ChatMessage(new String[0], "TimerHandler", "TimerHandler", message, new String[0], true, true, true, 0, false);
                Main.sendMessage(CommandNew.replaceBetweenParentheses(chatMessage, message, message.split(" "), null));
                Main.sendYTMessage(CommandNew.replaceBetweenParentheses(chatMessage, message, message.split(" "), null));
            }
        }
    }

    private static void save(Path path, JSONObject object){
        try {
            Files.write(path, object.toString(4).getBytes());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void saveCustomTimers(){
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for(TimerData data : registeredTimers){
            JSONObject timerObject = new JSONObject();
            timerObject.putOpt("name", data.getName());
            timerObject.putOpt("enabled", data.isEnabled());
            timerObject.putOpt("runCommand", data.getRunCommand());
            timerObject.putOpt("message", data.getMessage());
            timerObject.putOpt("interval", data.getInterval());
            timerObject.putOpt("lines", data.getLines());
            jsonArray.put(timerObject);
        }
        jsonObject.put("timers", jsonArray);
        save(Paths.get(Defaults.saveDirectory + "/loquibot/customTimers.json"), jsonObject);
    }
}
