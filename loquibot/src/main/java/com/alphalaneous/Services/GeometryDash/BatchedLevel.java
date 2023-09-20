package com.alphalaneous.Services.GeometryDash;

public class BatchedLevel {

    private long ID;
    private boolean exists;
    private GDLevelExtra levelExtra;

    public BatchedLevel(long ID, boolean exists, GDLevelExtra levelExtra){
        this.ID = ID;
        this.exists = exists;
        this.levelExtra = levelExtra;
    }

    public long getID() {
        return ID;
    }

    public boolean isExists() {
        return exists;
    }

    public GDLevelExtra getLevelExtra() {
        return levelExtra;
    }
}
