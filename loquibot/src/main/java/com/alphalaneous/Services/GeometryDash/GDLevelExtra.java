package com.alphalaneous.Services.GeometryDash;


import jdash.common.entity.GDLevel;

import java.util.Optional;

public class GDLevelExtra {

    public enum RequestError {

    }

    private final GDLevel level;
    private long accountID = -1;

    private int length = -1;

    public GDLevel getLevel() {
        return level;
    }

    public long getAccountID() {
        return accountID;
    }

    public int getLength(){
        return length;
    }

    public GDLevelExtra(GDLevel level, long accountID) {
        this.level = level;
        this.accountID = accountID;
    }

    public GDLevelExtra(GDLevel level, long accountID, int length) {
        this.level = level;
        this.accountID = accountID;
        this.length = length;
    }

    public GDLevelExtra(GDLevel level) {
        this.level = level;
    }

    public void setAccountID(long accountID){
        this.accountID = accountID;
    }

    public void setLength(int length){
        this.length = length;
    }

}
