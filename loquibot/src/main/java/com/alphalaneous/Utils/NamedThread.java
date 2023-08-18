package com.alphalaneous.Utils;

import com.alphalaneous.Interfaces.Function;

public class NamedThread extends Thread{

    public NamedThread(String name, Runnable runnable){
        super(runnable);
        setName(name);
    }
}
