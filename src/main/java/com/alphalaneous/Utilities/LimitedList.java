package com.alphalaneous.Utilities;

import java.util.ArrayList;

public class LimitedList<T> extends ArrayList<T> {

    private int limit;

    public LimitedList(int limit){
        this.limit = limit;
    }

    @Override
    public boolean add(T e) {

        if(this.size() >= limit){
            remove(0);
        }

        return super.add(e);

    }
}
