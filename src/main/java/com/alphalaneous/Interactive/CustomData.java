package com.alphalaneous.Interactive;

public abstract class CustomData {

    public abstract void register();

    public abstract void deregister();

    public abstract void setName(String command);

    public abstract void setMessage(String message);

    public abstract void setCounter(long counter);

    public abstract void setEnabled(boolean enabled);

    public abstract boolean isEnabled();

    public abstract String getName();

    public abstract long getCounter();

    public abstract String getMessage();

    public abstract void save(boolean reload);

    public abstract void save();
}
