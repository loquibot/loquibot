package com.alphalaneous.Interactive.Commands;

import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Services.Twitch.TwitchAPI;
import com.alphalaneous.Utilities.Language;
import org.json.JSONObject;

import java.util.Objects;

public class DefaultCommandActions {

    public static String runTitle(CommandActionData data) {
        ChatMessage message = data.getMessage();

        if (!message.isYouTube()) {
            System.out.println(message.isMod());
            if (data.getMessageParts().length == 1 || !message.isMod()) {
                JSONObject channelInfo = TwitchAPI.getChannelInfo();
                String title = channelInfo.getJSONArray("data").getJSONObject(0).getString("title");

                return String.format(Language.setLocale("$STREAM_TITLE_COMMAND_MESSAGE$"), title);
            }
            else if (message.isMod()) {
                String title = message.getMessage().substring(data.getMessageParts()[0].length()).trim();
                String response = TwitchAPI.setTitle(title);

                return Objects.requireNonNullElseGet(response, () -> String.format(Language.setLocale("$STREAM_TITLE_CHANGE_MESSAGE$"), title));
            }
        }
        return "";
    }

    public static String runGame(CommandActionData data) {
        ChatMessage message = data.getMessage();

        if (!message.isYouTube()) {
            if (data.getMessageParts().length == 1 || !message.isMod()) {
                JSONObject channelInfo = TwitchAPI.getChannelInfo();
                String title = channelInfo.getJSONArray("data").getJSONObject(0).getString("game_name");

                return String.format(Language.setLocale("$STREAM_GAME_COMMAND_MESSAGE$"), title);
            } else if (message.isMod()) {
                String game = message.getMessage().substring(data.getMessageParts()[0].length()).trim();
                String response = TwitchAPI.setGame(game);

                if (response == null) {
                    return String.format(Language.setLocale("$STREAM_GAME_CHANGE_MESSAGE$"), game);
                }
                if (response.equalsIgnoreCase("no_game")) {
                    return String.format(Language.setLocale("$GAME_NOT_FOUND_MESSAGE$"), game);
                }
                else return response;
            }
        }
        return "";
    }

    public static String runAddCommand(CommandActionData data) {
        return "";
    }

    public static String runDeleteCommand(CommandActionData data) {
        return "";
    }

    public static String runEditCommand(CommandActionData data) {
        return "";
    }

    public static String runGetCommands(CommandActionData data) {
        return "";
    }

    public static String runHelp(CommandActionData data) {
        return "";
    }

}
