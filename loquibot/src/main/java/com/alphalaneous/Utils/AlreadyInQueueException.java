package com.alphalaneous.Utils;

import com.alphalaneous.Services.GeometryDash.GDLevelExtra;

public class AlreadyInQueueException extends RuntimeException{

    private GDLevelExtra level;
    public AlreadyInQueueException(GDLevelExtra level){
        this.level = level;
    }

    public GDLevelExtra getLevel(){
        return level;
    }
}
