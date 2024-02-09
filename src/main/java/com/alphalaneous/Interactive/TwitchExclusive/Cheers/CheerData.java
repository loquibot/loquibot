package com.alphalaneous.Interactive.TwitchExclusive.Cheers;

import com.alphalaneous.Interactive.CustomData;
import com.alphalaneous.Pages.CommandPages.CommandsPage;
import com.alphalaneous.Pages.InteractionPages.CheersPage;
import com.alphalaneous.Utilities.Utilities;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class CheerData extends CustomData {

    private static final ArrayList<CheerData> registeredCheers = new ArrayList<>();

    private String message;
    private String name;
    private long counter = 0;
    private boolean isAnyAmount = false;
    private String range;
    private boolean isEnabled = true;

    public CheerData(String name){
        this.name = name;
    }

    @Override
    public void register() {
        registeredCheers.add(this);
    }

    @Override
    public void deregister() {
        registeredCheers.remove(this);
        saveCheers(true);
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public void setAnyAmount(boolean isAnyAmount){
        this.isAnyAmount = isAnyAmount;
    }

    public void setRange(String range){
        this.range = range;
    }

    public String getRange(){
        return range;
    }

    public boolean isAnyAmount(){
        return isAnyAmount;
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
        saveCheers(reload);
    }

    @Override
    public void save() {
        saveCheers(false);
    }

    @Override
    public boolean isEnabled(){
        return isEnabled;
    }

    public static ArrayList<CheerData> getRegisteredCheers(){
        return registeredCheers;
    }

    public static void saveCheers(boolean reload) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        if (getRegisteredCheers() != null) {
            for (CheerData data : getRegisteredCheers()) {
                JSONObject commandObject = new JSONObject();
                commandObject.putOpt("name", data.getName());
                commandObject.putOpt("anyAmount", data.isAnyAmount());
                commandObject.putOpt("message", data.getMessage());
                commandObject.putOpt("counter", data.getCounter());
                commandObject.putOpt("range", data.getRange());
                commandObject.putOpt("enabled", data.isEnabled());

                jsonArray.put(commandObject);
            }
            jsonObject.put("cheers", jsonArray);
            try {
                Files.write(Paths.get(Utilities.saveDirectory + "/customCheers.json").toAbsolutePath(), jsonObject.toString(4).getBytes());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        if(reload) CheersPage.load();
    }

    public static boolean isValidRange(String range){

        String[] parts = range.split(",");

        for(String p : parts){
            if(p.split("-").length == 2){
                String[] rangePieces = p.split("-");
                if(!StringUtils.isNumeric(rangePieces[0]) || !StringUtils.isNumeric(rangePieces[1])){
                    return false;
                }
                else{
                    try{
                        Integer.parseInt(rangePieces[0]);
                        Integer.parseInt(rangePieces[1]);
                    }
                    catch (NumberFormatException e){
                        return false;
                    }
                }
            }
            else{
                if(!StringUtils.isNumeric(p)){
                    return false;
                }
                else{
                    try{
                        Integer.parseInt(p);
                    }
                    catch (NumberFormatException e){
                        return false;
                    }
                }
            }
        }
        return true;
    }



    public boolean isInRange(int value){

        if(!isValidRange(getRange())) return false;

        String[] parts = getRange().split(",");

        for(String p : parts){
            if(p.split("-").length == 2){
                String[] rangePieces = p.split("-");
                int start = Integer.parseInt(rangePieces[0]);
                int end = Integer.parseInt(rangePieces[1]);
                if (start <= value && value <= end){
                    return true;
                }
            }
            else{
                int part = Integer.parseInt(p);
                if(part == value) return true;
            }
        }
        return false;
    }
}
