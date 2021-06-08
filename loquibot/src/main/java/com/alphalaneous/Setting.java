package com.alphalaneous;

public class Setting {

    private final String result;
    private boolean noSetting;

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
        return Integer.parseInt(result);
    }
    public double asDouble(){
        return Double.parseDouble(result);
    }
    public String asString(){
        return result;
    }
}
