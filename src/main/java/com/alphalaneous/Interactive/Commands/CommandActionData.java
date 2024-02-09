package com.alphalaneous.Interactive.Commands;

import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Interactive.CustomData;

import java.util.HashMap;

public class CommandActionData {

    private final String afterIdentifier;
    private final String[] messageParts;
    private final CustomData customData;
    private final ChatMessage message;
    private final HashMap<String, String> extraData;

    public CommandActionData(String afterIdentifier, String[] messageParts, CustomData customData, ChatMessage message, HashMap<String, String> extraData) {
        this.messageParts = messageParts;
        this.customData = customData;
        this.message = message;
        this.extraData = extraData;
        this.afterIdentifier = afterIdentifier;
    }

    public String afterIdentifier() {
        return afterIdentifier;
    }

    public String[] getMessageParts() {
        return messageParts;
    }

    public CustomData getCustomData() {
        return customData;
    }

    public ChatMessage getMessage() {
        return message;
    }

    public HashMap<String, String> getExtraData() {
        return extraData;
    }
}
