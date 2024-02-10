package com.alphalaneous.Interactive.Actions;

import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Interactive.Commands.CommandHandler;
import com.alphalaneous.Interactive.CustomData;
import com.alphalaneous.Pages.ActionsPage;
import com.alphalaneous.Servers;
import com.alphalaneous.Services.Twitch.TwitchChatListener;
import com.alphalaneous.Utilities.Logging;
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
    private int keyBind = -1;
    private boolean usesCtrl = false;
    private boolean usesAlt = false;

    public int getKeyBind() {
        return keyBind;
    }

    public void setKeyBind(int keyBind) {
        this.keyBind = keyBind;
    }

    public boolean isUsesCtrl() {
        return usesCtrl;
    }

    public void setUsesCtrl(boolean usesCtrl) {
        this.usesCtrl = usesCtrl;
    }

    public boolean isUsesAlt() {
        return usesAlt;
    }

    public void setUsesAlt(boolean usesAlt) {
        this.usesAlt = usesAlt;
    }

    public boolean isUsesShift() {
        return usesShift;
    }

    public void setUsesShift(boolean usesShift) {
        this.usesShift = usesShift;
    }

    private boolean usesShift = false;

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
            actionObject.putOpt("keybind", data.getKeyBind());
            actionObject.putOpt("keybindCtrl", data.isUsesCtrl());
            actionObject.putOpt("keybindAlt", data.isUsesAlt());
            actionObject.putOpt("keybindShift", data.isUsesShift());

            jsonArray.put(actionObject);
        }
        jsonObject.put("actions", jsonArray);

        try {
            Files.write(Paths.get(Utilities.saveDirectory + "/customActions.json").toAbsolutePath(), jsonObject.toString(4).getBytes());
        }
        catch (Exception e){
            Logging.getLogger().error(e.getMessage(), e);
        }
        if(reload) ActionsPage.load();
    }

    public void runAction(){
        new Thread(() -> {
            ChatMessage chatMessage = new ChatMessage(new String[0], "ActionHandler", "ActionHandler", "", new String[0], true, true, true, false, false);

            String reply = CommandHandler.replaceBetweenParentheses(chatMessage, getMessage(), this, null);

            TwitchChatListener.getCurrentListener().sendMessage(reply);
            Servers.sendYouTubeMessage(reply, null);

        }).start();
    }

}
