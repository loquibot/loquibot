package com.alphalaneous.Services.YouTube;

import com.alphalaneous.ChatBot.BotHandler;
import com.alphalaneous.Interactive.Commands.CommandHandler;
import com.alphalaneous.Interactive.Keywords.KeywordHandler;
import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Main;
import com.alphalaneous.Services.Twitch.TwitchChatListener;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Utils.SelfDestructingMessage;
import com.alphalaneous.Utils.Utilities;
import com.google.api.services.youtube.model.*;
import java.util.*;

public class YouTubeChatListener {

    private static final String LIVE_CHAT_FIELDS =
            "items(authorDetails(channelId,displayName,isChatModerator,isChatOwner,isChatSponsor,"
                    + "profileImageUrl),snippet(displayMessage,superChatDetails,publishedAt)),"
                    + "nextPageToken,pollingIntervalMillis";

    private static boolean isConnected = false;
    private static boolean isFirstMessage = true;

    private static Timer currentTimerTask = null;

    private static String liveChatID = null;
    private static final ArrayList<LiveChatMessage> messageObjects = new ArrayList<>();

    public static void startChatListener() {
        if(SettingsHandler.getSettings("youtubeEnabled").asBoolean()) {
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
                    Main.logger.error(e.getLocalizedMessage(), e);
                }

                Utilities.sleep(15000);

            }
        }
    }

    private static void listChatMessages(final String nextPageToken, long delayMs) {

        if(nextPageToken == null) Main.logger.info("Restarted listChatMessages");

        try {
            currentTimerTask = new Timer();

            currentTimerTask.schedule(
                new TimerTask() {
                    @SuppressWarnings("InstantiationOfUtilityClass")
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
                                System.out.println(messageObjects.contains(message));
                                if(!isSameMessage(message)) {
                                    ChatMessage message1 = buildChatMessage(message);
                                    if (!message1.getSender().equals("UCvTnC1Unw4Cy7m59WK65ufg")) {
                                        messageObjects.add(message);
                                        new SelfDestructingMessage();
                                        if (!isFirstMessage) {
                                            new Thread(() -> waitOnMessage(message1)).start();
                                        }
                                    }
                                }
                            }
                            isFirstMessage = false;

                            new Thread(() -> listChatMessages(
                                    response.getNextPageToken(),
                                    5000)).start();

                        } catch (Exception e) {
                            new Thread(() -> listChatMessages(
                                    null,
                                    5000)).start();

                            Main.logger.error(e.getLocalizedMessage(), e);
                        }
                    }
                }, delayMs);
        }
        catch (Exception e){
            Main.logger.error(e.getMessage(), e);
            listChatMessages(
                    null,
                    5000);
            Utilities.sleep(1);
        }
    }

    public static boolean isSameMessage(LiveChatMessage message){
        for(LiveChatMessage message1 : messageObjects){
            boolean sameAccountID = message1.getAuthorDetails().getChannelId().equals(message.getAuthorDetails().getChannelId());
            boolean sameTime = message1.getSnippet().getPublishedAt().equals(message.getSnippet().getPublishedAt());
            boolean sameMessage = message1.getSnippet().getDisplayMessage().equals(message.getSnippet().getDisplayMessage());

            if(sameTime && sameAccountID && sameMessage){
                return true;
            }
        }
        return false;
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
                false,
                false);
        message1.setYouTube(true);
        return message1;
    }

    private static void waitOnMessage(ChatMessage chatMessage) {
        BotHandler.onMessage(chatMessage);
        CommandHandler.run(chatMessage);
        KeywordHandler.run(chatMessage);
    }
}