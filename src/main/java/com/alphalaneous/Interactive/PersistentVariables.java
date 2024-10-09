package com.alphalaneous.Interactive;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.Utilities;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;

public class PersistentVariables {

    private static final HashMap<String, String> variables = new HashMap<>();


    @OnLoad
    public static void load(){
        Path varPath = Paths.get(Utilities.saveDirectory + "/persistentVariables.json");

        if(Files.exists(varPath)){
            try {
                String fileContents = Files.readString(varPath);

                JSONObject object = new JSONObject(fileContents);
                object.keySet().forEach(v -> variables.put(v, object.getString(v)));

            } catch (IOException e) {
                Logging.getLogger().error(e.getMessage(), e);
            }
        }
        else{
            try {
                Files.writeString(varPath, "{}", StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            } catch (IOException e) {
                Logging.getLogger().error(e.getMessage(), e);
            }
        }
    }

    public static void set(String varName, String value){
        variables.put(varName, value);
        save();
    }

    public static String get(String varName){
        return variables.get(varName);
    }

    public static void save(){
        Path varPath = Paths.get(Utilities.saveDirectory + "/persistentVariables.json");

        if(Files.exists(varPath)){
            JSONObject object = new JSONObject();
            variables.forEach(object::put);
            try {
                Files.writeString(varPath, object.toString(4), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            } catch (IOException e) {
                Logging.getLogger().error(e.getMessage(), e);
            }

        }
        else{
            try {
                Files.writeString(varPath, "{}", StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            } catch (IOException e) {
                Logging.getLogger().error(e.getMessage(), e);
            }
        }
    }
}
