package com.alphalaneous.Utilities.Chat;

import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Utilities.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelfDestructingMessage {

    private static final List<SelfDestructingMessage> selfDestructingMessages = Collections.synchronizedList(new ArrayList<>());

    private final ChatMessage message;
    public SelfDestructingMessage(ChatMessage message){
        this.message = message;
        new Thread(() -> {
            selfDestructingMessages.add(this);
            Utilities.sleep(60000*5);
            selfDestructingMessages.remove(this);
        }).start();
    }

    public ChatMessage getMessage(){
        return message;
    }

    public static List<SelfDestructingMessage> getSelfDestructingMessages(){
        return selfDestructingMessages;
    }

    public static int getSize(){
        return selfDestructingMessages.size();
    }

}
