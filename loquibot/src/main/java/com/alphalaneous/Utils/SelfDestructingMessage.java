package com.alphalaneous.Utils;

import com.alphalaneous.Services.Twitch.TwitchChatListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelfDestructingMessage {

    private static final List<SelfDestructingMessage> selfDestructingMessages = Collections.synchronizedList(new ArrayList<>());

    public SelfDestructingMessage(){
        new Thread(() -> {
            selfDestructingMessages.add(this);
            Utilities.sleep(60000*5);
            selfDestructingMessages.remove(this);
        }).start();
    }
    public static int getSize(){
        return selfDestructingMessages.size();
    }

}
