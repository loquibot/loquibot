package com.alphalaneous.Settings.Logs;

import com.alphalaneous.Utils.Defaults;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class LoggedID {

    private int ID;
    private int version;

    private static final ArrayList<LoggedID> loggedIDS = new ArrayList<>();

    public LoggedID(int ID, int version){
        this.ID = ID;
        this.version = version;

        boolean exists = false;
        for(LoggedID loggedID : loggedIDS){
            if(loggedID.getID() == ID){
                exists = true;
                break;
            }
        }
        if(!exists) {
            loggedIDS.add(this);
            saveLoggedIDs();
        }
    }

    public void removeID(){
        loggedIDS.remove(this);
        saveLoggedIDs();
    }

    public static ArrayList<LoggedID> getLoggedIDS() {
        return loggedIDS;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
        saveLoggedIDs();
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
        saveLoggedIDs();
    }

    public static void removeID(int ID){
        loggedIDS.removeIf(id -> id.getID() == ID);
        saveLoggedIDs();
    }

    public static void removeAll(){
        loggedIDS.clear();
        saveLoggedIDs();
    }

    public static void loadLoggedIDs(){
        Path loggedIDs = Paths.get(Defaults.saveDirectory + "\\loquibot\\requestsLog.txt");
        if(Files.exists(loggedIDs)){
            String loggedIDsString;
            try {
                loggedIDsString = Files.readString(loggedIDs, StandardCharsets.UTF_8).trim();
                String[] loggedID = loggedIDsString.split("\n");
                for(String log : loggedID){
                    if(!log.equalsIgnoreCase("")) {
                        int ID = Integer.parseInt(log.split(",")[0].trim());
                        int version;
                        if(log.split(",").length < 2) version = 0;
                        else version = Integer.parseInt(log.split(",")[1].trim());
                        new LoggedID(ID, version);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void saveLoggedIDs(){

        StringBuilder loggedIDsString = new StringBuilder();
        for(LoggedID loggedID : loggedIDS){
            loggedIDsString.append(loggedID.getID()).append(",").append(loggedID.getVersion()).append("\n");
        }
        try {
            Path logFile = Paths.get(Defaults.saveDirectory + "\\loquibot\\requestsLog.txt").toAbsolutePath();
            Files.write(logFile, loggedIDsString.toString().getBytes());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
