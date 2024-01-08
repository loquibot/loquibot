package com.alphalaneous.Interactive.Timers;

import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Services.Twitch.TwitchChatListener;
import com.alphalaneous.Interactive.Commands.CommandHandler;
import com.alphalaneous.Interactive.CustomData;
import com.alphalaneous.Pages.CommandPages.TimersPage;
import com.alphalaneous.Utilities.Chat.SelfDestructingMessage;
import com.alphalaneous.Utilities.Utilities;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TimerData extends CustomData {

    private static final ArrayList<TimerData> registeredTimers = new ArrayList<>();

    private String name;
    private String message;
    private long counter = 0;
    private boolean isEnabled = true;

    private int interval = 5;
    private int lines = 2;

    private String runCommand = null;

    public TimerData(String name){
        this.name = name;
    }


    @Override
    public void register() {
        registeredTimers.add(this);
    }

    @Override
    public void deregister() {
        registeredTimers.remove(this);
        save(true);
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
        saveCustomTimers(reload);
    }

    public void setInterval(int interval){
        this.interval = interval;
    }
    public void setLines(int lines){
        this.lines = lines;
    }

    public int getInterval(){
        return interval;
    }
    public int getLines(){
        return lines;
    }

    public void setRunCommand(String runCommand){
        this.runCommand = runCommand;
    }

    public String getRunCommand(){
        return runCommand;
    }

    @Override
    public void setEnabled(boolean isEnabled){
        this.isEnabled = isEnabled;
    }

    @Override
    public boolean isEnabled(){
        return isEnabled;
    }


    @Override
    public void save() {
        saveCustomTimers(false);
    }

    public static ArrayList<TimerData> getRegisteredTimers(){
        return registeredTimers;
    }

    public void runTimer(int minute){
        new Thread(() -> {
            if(minute % interval == 0 && isEnabled && SelfDestructingMessage.getSize() >= lines) {
                if (runCommand != null && !runCommand.equalsIgnoreCase("")) {
                    ChatMessage message = new ChatMessage(new String[]{}, "TimerHandler", "TimerHandler", runCommand, new String[0], true, true, true, false, false);
                    CommandHandler.run(message);
                } else {
                    ChatMessage chatMessage = new ChatMessage(new String[0], "TimerHandler", "TimerHandler", message, new String[0], true, true, true, false, false);
                    TwitchChatListener.getCurrentListener().sendMessage(CommandHandler.replaceBetweenParentheses(chatMessage, message, this, null));
                }
            }
        }).start();
    }

    public static void saveCustomTimers(boolean reload){
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

        try {
            Files.write(Paths.get(Utilities.saveDirectory + "customTimers.json").toAbsolutePath(), jsonObject.toString(4).getBytes());
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if(reload) TimersPage.load();
    }
}
