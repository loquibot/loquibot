package com.alphalaneous.Services.GeometryDash;


import jdash.common.entity.GDLevel;

import java.util.Optional;

public class GDLevelExtra {

    private GDLevel level;
    private long accountID;

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
}
