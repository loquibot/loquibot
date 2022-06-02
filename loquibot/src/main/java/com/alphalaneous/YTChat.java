package com.alphalaneous;

import com.alphalaneous.TwitchBot.ChatMessage;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import java.util.*;

public class YTChat {

    private static final String LIVE_CHAT_FIELDS =
            "items(authorDetails(channelId,displayName,isChatModerator,isChatOwner,isChatSponsor,"
                    + "profileImageUrl),snippet(displayMessage,superChatDetails,publishedAt)),"
                    + "nextPageToken,pollingIntervalMillis";

    private static boolean isConnected = false;
    private static boolean isFirstMessage = true;


    public static void startChatListener(String liveChatID) {
        if(Settings.getSettings("youtubeEnabled").asBoolean()) {
            try {
                while (true) {
                    if (!isConnected) {
                        try {
                            String liveChatId = liveChatID != null
                                    ? GetLiveChatId.getLiveChatId(YouTubeAccount.getYouTube(), liveChatID)
                                    : GetLiveChatId.getLiveChatId(YouTubeAccount.getYouTube());
                            if (liveChatId != null) {
                                isConnected = true;
                                listChatMessages(liveChatId, null, 0);
                            }
                        } catch (Exception e) {
                            isConnected = false;
                        }
                        Utilities.sleep(60000);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void listChatMessages(final String liveChatId, final String nextPageToken, long delayMs) {
        new Timer().schedule(
            new TimerTask() {
                @SuppressWarnings("InstantiationOfUtilityClass")
                @Override
                public void run() {
                    try {
                        LiveChatMessageListResponse response = YouTubeAccount.getYouTube()
                                .liveChatMessages()
                                .list(liveChatId, Arrays.asList("snippet", "authorDetails"))
                                .setPageToken(nextPageToken)
                                .setFields(LIVE_CHAT_FIELDS)
                                .execute();

                        List<LiveChatMessage> messages = response.getItems();
                        for (LiveChatMessage message : messages) {
                            ChatMessage message1 = buildChatMessage(message);
                            if (!message1.getSender().equals("UCvTnC1Unw4Cy7m59WK65ufg")) {
                                new ChatListener.SelfDestructingMessage();
                                if (Settings.getSettings("multiMode").asBoolean()) {
                                    if(!isFirstMessage) waitOnMessage(message1, true);
                                } else {
                                    if(!isFirstMessage) waitOnMessage(message1, false);
                                }
                            }
                        }
                        isFirstMessage = false;


                        listChatMessages(
                                liveChatId,
                                response.getNextPageToken(),
                                15000);
                        Utilities.sleep(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, delayMs);
    }

    private static ChatMessage buildChatMessage(LiveChatMessage message){
        ChatMessage message1 = new ChatMessage(null,
                message.getAuthorDetails().getChannelId(),
                message.getAuthorDetails().getDisplayName(),
                message.getSnippet().getDisplayMessage(),
                null,
                message.getAuthorDetails().getIsChatModerator(),
                message.getAuthorDetails().getIsChatSponsor(),
                false,
                0,
                false);
        message1.setYouTube(true);
        return message1;
    }

    private static void waitOnMessage(ChatMessage chatMessage, boolean multi) {
        if(multi) {
            new Thread(() -> {
                CommandNew.run(chatMessage);
                KeywordHandler.run(chatMessage);
            }).start();
        }
        else{
            CommandNew.run(chatMessage);
            KeywordHandler.run(chatMessage);
        }
        BotHandler.onMessage(chatMessage.getSender(), chatMessage.getMessage(), chatMessage.isMod(), chatMessage.isSub(), chatMessage.getCheerCount(), null, 0, chatMessage);
        Utilities.sleep(500);
    }
}