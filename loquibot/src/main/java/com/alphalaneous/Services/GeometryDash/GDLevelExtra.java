package com.alphalaneous.Services.GeometryDash;


import jdash.common.entity.GDLevel;

import java.util.Optional;

public class GDLevelExtra {

    public enum RequestError {

    }


    private GDLevel level;
    private long accountID = -1;

    public GDLevel getLevel() {
        return level;
    }

    public long getAccountID() {
        return accountID;
    }


    public GDLevelExtra(GDLevel level, long accountID) {
        this.level = level;
        this.accountID = accountID;
    }

    public GDLevelExtra(GDLevel level) {
        this.level = level;
    }

    public void setAccountID(long accountID){
        this.accountID = accountID;
    }

}
