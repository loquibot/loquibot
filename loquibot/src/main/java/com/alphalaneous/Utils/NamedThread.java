package com.alphalaneous.Utils;

public class NamedThread extends Thread{

    public NamedThread(String name, Runnable runnable){
        super(runnable);
        setName(name);
    }
}
