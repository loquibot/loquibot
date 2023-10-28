package com.alphalaneous.Interactive.Commands;

import com.alphalaneous.Interactive.CustomData;

public class FakeCustomData extends CustomData {

    String message;
    long counter = 0;


    public FakeCustomData(String message){
        this.message = message;
    }


    @Override
    public void register() {

    }

    @Override
    public void deregister() {

    }

    @Override
    public void setName(String command) {

    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void setCounter(long counter) {
        this.counter = counter;
    }

    @Override
    public void setEnabled(boolean enabled) {

    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public long getCounter() {
        return counter;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void save(boolean reload) {

    }

    @Override
    public void save() {

    }
}
