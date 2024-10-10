package com.alphalaneous.Interactive.Commands;

import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Enums.UserLevel;
import com.alphalaneous.Pages.CommandPages.CommandsPage;
import com.alphalaneous.Services.Twitch.TwitchAPI;
import com.alphalaneous.Utilities.Language;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

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
        ChatMessage message = data.getMessage();

        if(message.getArgs().length == 1){
            return Language.setLocale("$ADD_COMMAND_NO_ARGS_MESSAGE$");
        }

        int endOfArgsPos = 0;
        int cooldown = 0;
        UserLevel userLevel = UserLevel.EVERYONE;
        String[] aliases = null;

        String[] splitMessage = message.getMessage().split(" ");
        for(int i = 2; i < splitMessage.length; i++){
            if(splitMessage[i].startsWith("-")){
                if(splitMessage[i].split("=").length == 2) {
                    switch (splitMessage[i].split("=")[0].trim()) {
                        case "-cd":
                        case "-cooldown":
                            try {
                                cooldown = Integer.parseInt(splitMessage[i].split("=")[1]);
                                break;
                            }
                            catch (NumberFormatException e){
                                return Language.setLocale("$ADD_COMMAND_INVALID_COOLDOWN_MESSAGE$");
                            }
                        case "-ul":
                        case "-userlevel":
                            userLevel = UserLevel.forString(splitMessage[i].split("=")[1]);
                            break;
                        case "-a":
                        case "-aliases":
                            aliases = splitMessage[i].split("=")[1].split(",");
                            break;
                    }
                }
                else {
                    return Language.setLocale("$ADD_COMMAND_INVALID_ARGS_MESSAGE$");
                }
            }
            else{
                endOfArgsPos = i;
                break;
            }
        }
        if(message.getArgs().length >= 3) {
            CommandData newCommand = new CommandData(message.getArgs()[1]);

            int size = 0;
            for(int i = 0; i < endOfArgsPos; i++){
                size+= message.getArgs()[i].length()+1;
            }

            String text = message.getMessage().substring(size-1).trim();
            if(text.equalsIgnoreCase("")) {
                return Language.setLocale("$ADD_COMMAND_NO_MESSAGE_MESSAGE$");
            }
            else{
                newCommand.setMessage(text);
            }
            if(userLevel == null) return Language.setLocale("$ADD_COMMAND_INVALID_USERLEVEL_MESSAGE$");
            newCommand.setUserLevel(userLevel);

            if(aliases != null) newCommand.setAliases(List.of(aliases));
            newCommand.setCooldown(cooldown);

            if(newCommand.getName().equalsIgnoreCase("")
                    || newCommand.getName().trim().contains(" ") || newCommand.getName().trim().contains("\n")){
                return String.format(Language.setLocale("$ADD_COMMAND_INVALID_MESSAGE$"), newCommand.getName());
            }

            for (CommandData c : CommandData.getRegisteredCommands()) {
                if(newCommand.getName().equalsIgnoreCase(c.getName())) {
                    return String.format(Language.setLocale("$ADD_COMMAND_ALREADY_EXISTS_MESSAGE$"), newCommand.getName());
                }
            }

            newCommand.register();
            newCommand.save();
            CommandsPage.load();
            return String.format(Language.setLocale("$ADD_COMMAND_SUCCESS_MESSAGE$"), newCommand.getName());
        }
        return "";
    }

    public static String runDeleteCommand(CommandActionData data) {
        ChatMessage message = data.getMessage();

        if(message.getArgs().length == 1){
            return Language.setLocale("$DELETE_COMMAND_NO_ARGS_MESSAGE$");
        }
        for (CommandData c : CommandData.getRegisteredCommands()) {
            if(c.getName().equalsIgnoreCase(message.getArgs()[1])){
                c.deregister();
                CommandsPage.load();
                return String.format(Language.setLocale("$DELETE_COMMAND_SUCCESS_MESSAGE$"), message.getArgs()[1]);
            }
        }
        return String.format(Language.setLocale("$DELETE_COMMAND_DOESNT_EXIST_MESSAGE$"), message.getArgs()[1]);
    }

    public static String runEditCommand(CommandActionData data) {
        ChatMessage message = data.getMessage();

        if(message.getArgs().length == 1){
            return Language.setLocale("$EDIT_COMMAND_NO_ARGS_MESSAGE$");
        }
        int endOfArgsPos = 0;

        int cooldown = 0;
        UserLevel userLevel = UserLevel.UNKNOWN;
        String[] aliases = null;

        String[] splitMessage = message.getMessage().split(" ");
        for(int i = 2; i < splitMessage.length; i++){
            if(splitMessage[i].startsWith("-")){
                if(splitMessage[i].split("=").length == 2) {
                    switch (splitMessage[i].split("=")[0].trim()) {
                        case "-cd":
                        case "-cooldown":
                            try {
                                cooldown = Integer.parseInt(splitMessage[i].split("=")[1]);
                                break;
                            }
                            catch (NumberFormatException e){
                                return Language.setLocale("$EDIT_COMMAND_INVALID_COOLDOWN_MESSAGE$");
                            }
                        case "-ul":
                        case "-userlevel":
                            userLevel = UserLevel.forString(splitMessage[i].split("=")[1]);
                            break;
                        case "-a":
                        case "-aliases":
                            aliases = splitMessage[i].split("=")[1].split(",");
                            break;
                    }
                }
                else {
                    return Language.setLocale("$EDIT_COMMAND_INVALID_ARGS_MESSAGE$");
                }
            }
            else{
                endOfArgsPos = i;
                break;
            }
        }
        if(message.getArgs().length >= 3) {
            for (CommandData c : CommandData.getRegisteredCommands()) {
                if (c.getName().equalsIgnoreCase(message.getArgs()[1])) {

                    int size = 0;
                    if(endOfArgsPos == 0) endOfArgsPos = splitMessage.length;
                    for(int i = 0; i < endOfArgsPos; i++){
                        size+= message.getArgs()[i].length()+1;
                    }
                    String text = "";
                    if(message.getMessage().length() > size-1) text = message.getMessage().substring(size-1).trim();
                    if(!text.equalsIgnoreCase("")) {
                        c.setMessage(text);
                    }
                    if(userLevel == null) return Language.setLocale("$ADD_COMMAND_INVALID_USERLEVEL_MESSAGE$");
                    if(userLevel != UserLevel.UNKNOWN) c.setUserLevel(userLevel);

                    if(cooldown != 0) c.setCooldown(cooldown);
                    if(aliases != null) c.setAliases(List.of(aliases));

                    c.save();
                    CommandsPage.load();

                    return String.format(Language.setLocale("$EDIT_COMMAND_SUCCESS_MESSAGE$"), c.getName());
                }
            }
        }
        return "";
    }

    public static String runGetCommands(CommandActionData data) {
        return "";
    }

    public static String runHelp(CommandActionData data) {
        return "";
    }

}
