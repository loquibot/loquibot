package com.alphalaneous.Interactive.TwitchExclusive.BasicEvents;

import com.alphalaneous.Interactive.CustomData;
import com.alphalaneous.Pages.InteractionPages.BasicEventsPage;
import com.alphalaneous.Utilities.Utilities;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class BasicEventData extends CustomData {

    public enum BasicEvent {
        FOLLOW,
        SUBSCRIBE,
        RAID
    }

    private static final ArrayList<BasicEventData> registeredBasicEvents = new ArrayList<>();

    private String message;
    private final BasicEvent event;
    private long counter = 0;
    private boolean isEnabled = true;


    public BasicEventData(BasicEvent event){
        this.event = event;
        register();
    }

    @Override
    public void register() {
        registeredBasicEvents.add(this);
    }

    @Override
    public void deregister() {
        registeredBasicEvents.remove(this);
        saveCustomEvents(true);
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public void setMessage(String message) {
        this.message = message;
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
        return Utilities.toFirstUpper(event.name());
    }

    public BasicEvent getEvent(){
        return event;
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
        saveCustomEvents(reload);
    }

    @Override
    public void save() {
        saveCustomEvents(false);
    }

    @Override
    public boolean isEnabled(){
        return isEnabled;
    }

    public static ArrayList<BasicEventData> getRegisteredBasicEvents(){
        return registeredBasicEvents;
    }

    public static void saveCustomEvents(boolean reload) {

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        if (getRegisteredBasicEvents() != null) {
            for (BasicEventData data : getRegisteredBasicEvents()) {
                JSONObject commandObject = new JSONObject();
                commandObject.putOpt("type", data.getEvent().name());
                commandObject.putOpt("message", data.getMessage());
                commandObject.putOpt("counter", data.getCounter());
                commandObject.putOpt("enabled", data.isEnabled());
                jsonArray.put(commandObject);
            }
            jsonObject.put("events", jsonArray);
            try {
                Files.write(Paths.get(Utilities.saveDirectory + "customBasicEvents.json").toAbsolutePath(), jsonObject.toString(4).getBytes());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        if(reload) BasicEventsPage.load();
    }
}
