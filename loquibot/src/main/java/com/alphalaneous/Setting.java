package com.alphalaneous;

import java.awt.*;

public class Setting {

    private final String result;
    private boolean noSetting = false;

    public Setting(String result){
        this.result = result;
    }
    public Setting(boolean noSetting){
        this.noSetting = noSetting;
        result = "";
    }
    public boolean asBoolean(){
        if(noSetting) return false;
        return Boolean.parseBoolean(result);
    }
    public int asInteger(){
        try {
            return Integer.parseInt(result);
        }
        catch (NumberFormatException e){
            return 0;
        }
    }
    public float asFloat(){
        try {
            return Float.parseFloat(result);
        }
        catch (NumberFormatException e){
            return 0;
        }
    }
    public double asDouble(){
        try {
            return Double.parseDouble(result);
        }
        catch (NumberFormatException e){
            return 0;
        }
    }
    public Color asColor(){
        return Color.decode("#" + result);
    }


    public String asString(){
        return result;
    }
    public boolean exists(){
        return !noSetting;
    }
}
