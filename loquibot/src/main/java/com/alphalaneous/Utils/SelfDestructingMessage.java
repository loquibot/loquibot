package com.alphalaneous.Utils;

import com.alphalaneous.Main;
import com.alphalaneous.Services.Twitch.TwitchChatListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelfDestructingMessage {

    private static final List<SelfDestructingMessage> selfDestructingMessages = Collections.synchronizedList(new ArrayList<>());

    public SelfDestructingMessage(){
        try {
            new Thread(() -> {
                selfDestructingMessages.add(this);
                Utilities.sleep(60000 * 5);
                selfDestructingMessages.remove(this);
            }).start();
        }
        catch (Exception e){
            Main.logger.error(e.getMessage(), e);
        }
    }
    public static int getSize(){
        return selfDestructingMessages.size();
    }

}
