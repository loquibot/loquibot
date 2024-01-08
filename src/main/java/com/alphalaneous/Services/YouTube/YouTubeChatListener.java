package com.alphalaneous.Services.YouTube;

import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Interactive.Commands.CommandHandler;
import com.alphalaneous.Interactive.Keywords.KeywordHandler;
import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.Utilities;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageListResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class YouTubeChatListener {

    private static final String LIVE_CHAT_FIELDS =
            "items(authorDetails(channelId,displayName,isChatModerator,isChatOwner,isChatSponsor,"
                    + "profileImageUrl),snippet(displayMessage,superChatDetails,publishedAt)),"
                    + "nextPageToken,pollingIntervalMillis";
    private static boolean isFirstMessage = true;

    private static Timer currentTimerTask = null;

    private static String liveChatID = null;

    public static void startChatListener() {
        new Thread(() -> {
            while (true) {
                try {
                    String newLiveChatID = GetLiveChatID.getLiveChatId(YouTubeAccount.getYouTube());

                    if(newLiveChatID != null && !newLiveChatID.equalsIgnoreCase(liveChatID)){
                        if(currentTimerTask != null){
                            currentTimerTask.cancel();
                        }
                        liveChatID = newLiveChatID;
                        YouTubeAccount.liveChatId = liveChatID;
                        listChatMessages(null, 0);
                    }
                } catch (Exception e) {
                    Logging.getLogger().error(e.getMessage(), e);
                }
                Utilities.sleep(15000);
            }
        }).start();
    }

    private static void listChatMessages(final String nextPageToken, long delayMs) {

        if(nextPageToken == null) Logging.getLogger().info("Restarted listChatMessages");

        try {
            currentTimerTask = new Timer();

            currentTimerTask.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            LiveChatMessageListResponse response = YouTubeAccount.getYouTube()
                                    .liveChatMessages()
                                    .list(liveChatID, Arrays.asList("snippet", "authorDetails"))
                                    .setPageToken(nextPageToken)
                                    .setFields(LIVE_CHAT_FIELDS)
                                    .execute();

                            List<LiveChatMessage> messages = response.getItems();
                            for (LiveChatMessage message : messages) {
                                ChatMessage message1 = buildChatMessage(message);
                                if (!message1.getSender().equals("UCvTnC1Unw4Cy7m59WK65ufg")) {
                                    if (!isFirstMessage) {
                                        waitOnMessage(message1);
                                    }
                                }
                            }
                            isFirstMessage = false;

                            listChatMessages(
                                    response.getNextPageToken(),
                                    5000);
                            Utilities.sleep(1);

                        } catch (Exception e) {
                            listChatMessages(
                                    null,
                                    5000);
                            Utilities.sleep(1);

                            Logging.getLogger().error(e.getMessage(), e);
                        }
                    }
                }, delayMs);
        }
        catch (Exception e){
            listChatMessages(
                    null,
                    5000);
            Utilities.sleep(1);
        }
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
                false);
    }

    private static void waitOnMessage(ChatMessage chatMessage) {
        CommandHandler.run(chatMessage);
        KeywordHandler.run(chatMessage);
    }
}