package com.alphalaneous.Services.YouTube;

import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Interactive.Commands.CommandHandler;
import com.alphalaneous.Interactive.Keywords.KeywordHandler;
import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.SettingsHandler;
import com.alphalaneous.Utilities.Utilities;
import com.alphalaneous.Window;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageListResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;

public class YouTubeChatListener {

    private static final String LIVE_CHAT_FIELDS =
            "items(authorDetails(channelId,displayName,isChatModerator,isChatOwner,isChatSponsor,"
                    + "profileImageUrl),snippet(displayMessage,superChatDetails,publishedAt)),"
                    + "nextPageToken,pollingIntervalMillis";
    private static boolean isFirstMessage = true;

    private static Timer currentTimerTask = null;

    private static String liveChatID = null;

    private static Thread listenerThread;


    public static void startChatListener() {

        if(listenerThread != null && listenerThread.isAlive()){
            listenerThread.stop();
        }

        listenerThread = new Thread(() -> {
            while (true) {
                try {
                    String newLiveChatID = GetLiveChatID.getLiveChatId(YouTubeAccount.getYouTube());

                    if(newLiveChatID != null && !newLiveChatID.equalsIgnoreCase(liveChatID)){
                        if(currentTimerTask != null){
                            currentTimerTask.cancel();
                        }
                        liveChatID = newLiveChatID;
                        YouTubeAccount.liveChatId = liveChatID;
                        Window.loadYouTubeChat(GetLiveChatID.getVideoID(YouTubeAccount.getYouTube()));
                        listChatMessages(null);
                    }
                } catch (Exception e) {
                    Logging.getLogger().error(e.getMessage(), e);
                }
                Utilities.sleep(15000);
            }
        });
        listenerThread.start();
    }

    public static void stopChatListener(){
        if(listenerThread != null && listenerThread.isAlive()){
            listenerThread.stop();
        }
    }

    private static void listChatMessages(String nextPageToken) {

        new Thread(() -> {

            if(SettingsHandler.getSettings("isYouTubeLoggedIn").asBoolean()) {

                if (nextPageToken == null) Logging.getLogger().info("Restarted listChatMessages");

                try {
                    currentTimerTask = new Timer();

                    currentTimerTask.schedule(Utilities.createTimerTask(() -> {
                        try {
                            LiveChatMessageListResponse response = YouTubeAccount.getYouTube()
                                    .liveChatMessages()
                                    .list(liveChatID, Arrays.asList("snippet", "authorDetails"))
                                    .setPageToken(nextPageToken)
                                    .setFields(LIVE_CHAT_FIELDS)
                                    .execute();

                            List<LiveChatMessage> messages = response.getItems();
                            messages.forEach((message) -> {
                                ChatMessage message1 = buildChatMessage(message);
                                if (!message1.getSender().equals("UCvTnC1Unw4Cy7m59WK65ufg") && !isFirstMessage) {
                                    new Thread(() -> waitOnMessage(message1));
                                }
                            });

                            isFirstMessage = false;
                            listChatMessages(response.getNextPageToken());

                        } catch (Exception e) {
                            listChatMessages(null);
                        }
                    }), 5000);
                } catch (Exception e) {
                    Logging.getLogger().error(e.getMessage(), e);
                    listChatMessages(null);
                }
            }
        }).start();

    }

    private static ChatMessage buildChatMessage(LiveChatMessage message){
        return new ChatMessage(null,
                message.getAuthorDetails().getChannelId(),
                message.getAuthorDetails().getDisplayName(),
                message.getSnippet().getDisplayMessage(),
                null,
                message.getAuthorDetails().getIsChatModerator(),
                message.getAuthorDetails().getIsChatSponsor(),
                false,
                false,
                false,
                true);
    }

    private static void waitOnMessage(ChatMessage chatMessage) {
        CommandHandler.run(chatMessage);
        KeywordHandler.run(chatMessage);
    }
}