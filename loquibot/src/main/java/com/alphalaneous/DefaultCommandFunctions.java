package com.alphalaneous;

import com.alphalaneous.Components.CommandConfigCheckbox;
import com.alphalaneous.Moderation.LinkPermit;
import com.alphalaneous.TwitchBot.ChatMessage;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DefaultCommandFunctions {
    
    public static String runBlock(ChatMessage message){
        if(message.getArgs().length == 0){
            return Utilities.format("$BLOCK_NO_ID_MESSAGE$", message.getSender());
        }
        return RequestsUtils.block(message.getSender(), message.getArgs());
    }
    public static String runBlockUser(ChatMessage message){
        if(message.getArgs().length == 0){
            return Utilities.format("$BLOCK_NO_USER_MESSAGE$", message.getSender());
        }
        return RequestsUtils.blockUser(message.getSender(), message.getArgs());
    }
    public static String runClear(ChatMessage message){
        RequestsUtils.clear();
        return Utilities.format("$CLEAR_MESSAGE$", message.getSender());
    }
    public static String runEnd(ChatMessage message){
        if(message.getSender().equalsIgnoreCase("Alphalaneous")){
            RequestsUtils.endLoquibot();
        }
        return "";
    }

    public static String runStopSounds(ChatMessage message){
        Sounds.stopAllSounds();
        return Utilities.format("$STOP_SOUNDS_MESSAGE$", message.getSender());
    }

    public static String runFart(ChatMessage message){
        if(message.getSender().equalsIgnoreCase("Alphalaneous") || message.isMod()){
            Sounds.playSound("/sounds/fart.mp3", true, true, false, false);
        }
        return "";
    }
    public static String runPing(ChatMessage message){
        if(message.getSender().equalsIgnoreCase("Alphalaneous") || message.isMod()){
            Sounds.playSound("/sounds/ping.mp3", true, true, false, false);
        }
        return "";
    }

    public static String runEval(ChatMessage message){
        if(message.getSender().equalsIgnoreCase("Alphalaneous") || message.isMod()){
            return Board.eval(message.getMessage());
        }
        return "";
    }
    public static String runGDPing(ChatMessage message){
        return "GD ping: " + Board.testSearchPing() + " ms";
    }
    public static String runHelp(ChatMessage message){
        return RequestsUtils.getHelp(message);
    }
    public static String runCommandList(ChatMessage message){
        return RequestsUtils.getCommand(message);
    }


    public static String runInfo(ChatMessage message){
        int intArg;
        try {
            intArg = Integer.parseInt(message.getArgs()[1]);
        }
        catch (Exception e){
            intArg = RequestsUtils.getSelection() + 1;
        }
        if (message.getArgs().length <= 1 || message.getArgs().length >= RequestsUtils.getSize()) {
            intArg = RequestsUtils.getSelection() + 1;
        }
        if (RequestsUtils.getSize() > 0 && intArg <= RequestsUtils.getSize()) {
            return Utilities.format("$INFO_COMMAND_MESSAGE$", message.getSender(),
                    RequestsUtils.getLevel(intArg - 1, "name"),
                    RequestsUtils.getLevel(intArg - 1, "id"),
                    RequestsUtils.getLevel(intArg - 1, "author"),
                    RequestsUtils.getLevel(intArg - 1, "requester"),
                    RequestsUtils.getLevel(intArg - 1, "downloads"),
                    RequestsUtils.getLevel(intArg - 1, "likes"),
                    RequestsUtils.getLevel(intArg - 1, "objects"),
                    RequestsUtils.getLevel(intArg - 1, "difficulty"));
        }
        return "";
    }
    public static String runMove(ChatMessage message){
        if(message.getArgs().length == 1){
            return Utilities.format("$MOVE_NO_ID_MESSAGE$", message.getSender());
        }
        if(message.getArgs().length == 2){
            return Utilities.format("$MOVE_NO_POS_MESSAGE$", message.getSender());
        }
        try {
            if (RequestsUtils.getPosFromID(Integer.parseInt(message.getArgs()[1])) != -1) {
                int newPos = 0;
                try {
                    newPos = Integer.parseInt(message.getArgs()[2]) - 1;
                }
                catch (NumberFormatException ignored){
                }
                if (newPos <= 0) {
                    newPos = 0;
                }
                RequestsUtils.movePosition(RequestsUtils.getPosFromID(Integer.parseInt(message.getArgs()[1])), newPos);
                return Utilities.format("$MOVE_MESSAGE$", message.getSender(), message.getArgs()[1], (newPos + 1));
            }
            else{
                return Utilities.format("$MOVE_FAILED_MESSAGE$", message.getSender(), message.getArgs()[1]);
            }
        }
        catch (NumberFormatException e){
            e.printStackTrace();
            return Utilities.format("$MOVE_FAILED_MESSAGE$", message.getSender(), message.getArgs()[1]);
        }
    }
    public static String runNext(ChatMessage message){
        if(!Settings.getSettings("basicMode").asBoolean()) {
            if (RequestsUtils.getSize() > 1) {

                return Utilities.format("$NEXT_MESSAGE$", message.getSender(),
                        RequestsUtils.getLevel(1, "name"),
                        RequestsUtils.getLevel(1, "author"),
                        RequestsUtils.getLevel(1, "id"),
                        RequestsUtils.getLevel(1, "requester"));
            }
        }
        else {
            if (RequestsUtils.getSize() > 1) {
                return Utilities.format("$NEXT_MESSAGE_BASIC$", message.getSender(),
                        RequestsUtils.getLevel(1, "id"));
            }
        }
        return "";
    }
    public static String runPermit(ChatMessage message){
        if(Settings.getSettings("linkFilterEnabled").asBoolean()){
            if(message.getArgs().length == 1){
                return Utilities.format("$NO_USER_MESSAGE$", message.getSender());
            }
            LinkPermit.giveLinkPermit(message.getArgs()[1]);
            return Utilities.format("$PERMIT_SUCCESS_MESSAGE$", message.getSender(), message.getArgs()[1]);
        }
        return "";
    }
    public static String runPosition(ChatMessage message){
        int intArg;
        try{
            intArg = Integer.parseInt(message.getArgs()[1]);
        }
        catch (Exception e){
            intArg = 1;
        }
        if(message.getArgs().length == 1 || intArg < 1){
            intArg = 1;
        }
        ArrayList<Integer> userPosition = new ArrayList<>();
        for(var i = 0; i < RequestsUtils.getSize(); i++){
            if(RequestsUtils.getLevel(i, "requester").equalsIgnoreCase(message.getSender())){
                userPosition.add(i);
            }
        }
        if(userPosition.size() == 0){
            return Utilities.format("$POSITION_NONE_MESSAGE$", message.getSender());
        }
        if(intArg > userPosition.size()){
            return Utilities.format("$POSITION_WRONG_MESSAGE$", message.getSender(), userPosition.size());
        }
        int pos = userPosition.get(intArg-1)+1;
        return Utilities.format("$POSITION_MESSAGE$", message.getSender(),
                RequestsUtils.getLevel(userPosition.get(intArg-1), "name"), pos);
    }
    public static String runQueue(ChatMessage message){
        int intArg;
        try {
            intArg = Integer.parseInt(message.getArgs()[1]);
        }
        catch (Exception e){
            intArg = 1;
        }
        int queueLength = Settings.getSettings("queueLevelLength").asInteger();
        if(message.getArgs().length == 1){
            intArg = 1;
        }
        int pages = 0;
        if(queueLength != 0) {
            pages = (((RequestsUtils.getSize() - 1) / queueLength) + 1);
        }
        if(RequestsUtils.getSize() == 0){
            return Utilities.format("$QUEUE_NO_LEVELS_MESSAGE$", message.getSender());
        }
        if(intArg > pages){
            return Utilities.format("$QUEUE_NO_PAGE_MESSAGE$", message.getSender(), intArg);
        }
        if(intArg < 1){
            intArg = 1;
        }
        String queueMessage = Utilities.format("$QUEUE_MESSAGE$", message.getSender(), intArg, pages) + " | ";
        for(int i = (intArg - 1)*queueLength; i < intArg * queueLength; i++){
            if(i < RequestsUtils.getSize()){
                if(i % queueLength != 0){
                    queueMessage = queueMessage.concat(", ");
                }
                queueMessage = queueMessage.concat(i+1 + ": " + RequestsUtils.getLevel(i, "name") + " (" + RequestsUtils.getLevel(i, "id") + ")");
            }
        }
        return queueMessage;
    }
    public static String runRemove(ChatMessage message){
        int intArg;
        try {
            intArg = Integer.parseInt(message.getArgs()[1]);
        }
        catch (NumberFormatException e){
            intArg = 1;
        }
        if(message.getArgs().length == 1){
            intArg = 1;
        }
        if(message.isMod()){
            try {
                Integer.parseInt(message.getArgs()[1]);
            }
            catch (NumberFormatException e){
                return "";
            }
        }
        return RequestsUtils.remove(message.getSender(), message.isMod(), intArg);
    }
    public static String runRequest(ChatMessage message){
        if(message.getArgs().length == 1){
            return Utilities.format("$SPECIFY_ID_MESSAGE$", message.getSender());
        }
        Requests.request(message.getSender(), message.isMod(), message.isSub(), message.getMessage(), message.getTag("id"), Long.parseLong(message.getTag("user-id")));
        return "";
    }
    public static String runSong(ChatMessage message){
        if(!Settings.getSettings("basicMode").asBoolean()) {
            int intArg;
            try {
                intArg = Integer.parseInt(message.getArgs()[1]);
            }
            catch (Exception e){
                intArg = RequestsUtils.getSelection() + 1;
            }
            if (message.getArgs().length == 1) {
                intArg = RequestsUtils.getSelection() + 1;
            }
            if (RequestsUtils.getSize() > 0 && intArg <= RequestsUtils.getSize()) {
                return Utilities.format("$SONG_MESSAGE$", message.getSender(),
                        RequestsUtils.getLevel(intArg - 1, "songName"),
                        RequestsUtils.getLevel(intArg - 1, "songArtist"),
                        RequestsUtils.getLevel(intArg - 1, "songID"));
            } else {
                return "";
            }
        }
        return "";
    }
    public static String runToggle(ChatMessage message){
        RequestsUtils.toggleRequests();
        return "";
    }
    public static String runTop(ChatMessage message){
        int pos;
        try {
            pos = RequestsUtils.getPosFromID(Long.parseLong(message.getArgs()[1]));
        }
        catch (Exception e){
            pos = -1;
        }
        if(message.getArgs().length == 1){
            return Utilities.format("$TOP_NO_ID_MESSAGE$", message.getSender());
        }
        if(pos != -1 && message.getArgs().length > 1){
            RequestsUtils.movePosition(pos, 1);
            return Utilities.format("$TOP_MESSAGE$", message.getSender(), message.getArgs()[1]);
        }
        else{
            return Utilities.format("$TOP_FAILED_MESSAGE$", message.getSender(), message.getArgs()[1]);
        }
    }
    public static String runUnblock(ChatMessage message){
        if(message.getArgs().length == 1){
            return Utilities.format("$BLOCK_NO_ID_MESSAGE$", message.getSender());
        }
        return RequestsUtils.unblock( message.getSender(), message.getArgs());
    }
    public static String runUnblockUser(ChatMessage message){
        if(message.getArgs().length == 1){
            return Utilities.format("$BLOCK_NO_USER_MESSAGE$", message.getSender());
        }
        return RequestsUtils.unblockUser( message.getSender(), message.getArgs());
    }
    public static String runWrongLevel(ChatMessage message){
        return RequestsUtils.removeLatest(message.getSender());
    }
    public static String runAddcom(ChatMessage message){

        if(message.getArgs().length == 1){
            return Utilities.format("$ADD_COMMAND_NO_ARGS_MESSAGE$", message.getSender());
        }
        int endOfArgsPos = 0;

        int cooldown = 0;
        String userLevel = "";
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
                                return Utilities.format("$ADD_COMMAND_INVALID_COOLDOWN_MESSAGE$", message.getSender());
                            }
                        case "-ul":
                        case "-userlevel":
                            userLevel = getValidUserlevel(splitMessage[i].split("=")[1]);
                            break;
                        case "-a":
                        case "-aliases":
                            aliases = splitMessage[i].split("=")[1].split(",");
                            break;
                    }
                }
                else {
                    return Utilities.format("$ADD_COMMAND_INVALID_ARGS_MESSAGE$", message.getSender());
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
                return Utilities.format("$ADD_COMMAND_NO_MESSAGE_MESSAGE$", message.getSender());
            }
            else{
                newCommand.setMessage(text);
                newCommand.setDescription(text);
            }
            if(userLevel == null) return Utilities.format("$ADD_COMMAND_INVALID_USERLEVEL_MESSAGE$", message.getSender());
            if(!userLevel.equalsIgnoreCase("")) newCommand.setUserLevel(userLevel);

            if(aliases != null) newCommand.setAliases(List.of(aliases));
            newCommand.setCooldown(cooldown);

            if(CommandConfigCheckbox.checkIfNameExists(newCommand.getCommand(), "") || newCommand.getCommand().equalsIgnoreCase("")
                    || newCommand.getCommand().trim().contains(" ") || newCommand.getCommand().trim().contains("\n")){
                return Utilities.format("$ADD_COMMAND_ALREADY_EXISTS_MESSAGE$", message.getSender(), newCommand.getCommand());
            }

            newCommand.registerCommand();

            LoadCommands.reloadCustomCommands();
            return Utilities.format("$ADD_COMMAND_SUCCESS_MESSAGE$", message.getSender(), newCommand.getCommand());
        }
        return "";
    }
    public static String runDelcom(ChatMessage message){
        if(message.getArgs().length == 1){
            return Utilities.format("$DELETE_COMMAND_NO_ARGS_MESSAGE$", message.getSender());
        }
        for(CommandData data : LoadCommands.getCustomCommands()){
            if(data.getCommand().equalsIgnoreCase(message.getArgs()[1])){
                data.deRegisterCommand();
                LoadCommands.reloadCustomCommands();
                return Utilities.format("$DELETE_COMMAND_SUCCESS_MESSAGE$", message.getSender(), message.getArgs()[1]);

            }
        }
        return Utilities.format("$DELETE_COMMAND_DOESNT_EXIST_MESSAGE$", message.getSender(), message.getArgs()[1]);
    }


    public static String runEditcom(ChatMessage message){
        if(message.getArgs().length == 1){
            return Utilities.format("$EDIT_COMMAND_NO_ARGS_MESSAGE$", message.getSender());
        }
        int endOfArgsPos = 0;

        int cooldown = 0;
        String userLevel = "";
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
                                return Utilities.format("$EDIT_COMMAND_INVALID_COOLDOWN_MESSAGE$", message.getSender());
                            }
                        case "-ul":
                        case "-userlevel":
                            userLevel = getValidUserlevel(splitMessage[i].split("=")[1]);
                            break;
                        case "-a":
                        case "-aliases":
                            aliases = splitMessage[i].split("=")[1].split(",");
                            break;
                    }
                }
                else {
                    return Utilities.format("$EDIT_COMMAND_INVALID_ARGS_MESSAGE$", message.getSender());
                }
            }
            else{
                endOfArgsPos = i;
                break;
            }
        }
        if(message.getArgs().length >= 3) {
            for (CommandData data : LoadCommands.getCustomCommands()) {
                if (data.getCommand().equalsIgnoreCase(message.getArgs()[1])) {

                    int size = 0;
                    if(endOfArgsPos == 0) endOfArgsPos = splitMessage.length;
                    for(int i = 0; i < endOfArgsPos; i++){
                        size+= message.getArgs()[i].length()+1;
                    }
                    String text = "";
                    if(message.getMessage().length() > size-1) text = message.getMessage().substring(size-1).trim();
                    if(!text.equalsIgnoreCase("")) {
                        data.setMessage(text);
                        data.setDescription(text);
                    }
                    if(userLevel == null) return Utilities.format("$ADD_COMMAND_INVALID_USERLEVEL_MESSAGE$", message.getSender());
                    if(!userLevel.equalsIgnoreCase("")) data.setUserLevel(userLevel);

                    if(cooldown != 0) data.setCooldown(cooldown);
                    if(aliases != null) data.setAliases(List.of(aliases));

                    LoadCommands.reloadCustomCommands();
                    return Utilities.format("$EDIT_COMMAND_SUCCESS_MESSAGE$", message.getSender(), data.getCommand());

                }
            }
        }
        return "";
    }
    public static String runTitle(ChatMessage message){
        if(message.getArgs().length == 1 || !message.isMod()){
            JSONObject channelInfo = APIs.getChannelInfo();
            String title = channelInfo.getJSONArray("data").getJSONObject(0).getString("title");
            return Utilities.format("$STREAM_TITLE_COMMAND_MESSAGE$",  message.getSender(), title);
        }
        else if(message.isMod()){

            String title = message.getMessage().substring(message.getMessage().split(" ")[0].length()).trim();
            String response = APIs.setTitle(title);
            if(response == null) return Utilities.format("$STREAM_TITLE_CHANGE_MESSAGE$",  message.getSender(), title);
            else return response;

        }
        return "";
    }
    public static String runGame(ChatMessage message){
        if(message.getArgs().length == 1 || !message.isMod()){
            JSONObject channelInfo = APIs.getChannelInfo();
            String title = channelInfo.getJSONArray("data").getJSONObject(0).getString("game_name");
            return Utilities.format("$STREAM_GAME_COMMAND_MESSAGE$",  message.getSender(), title);
        }
        else if(message.isMod()){

            String game = message.getMessage().substring(message.getMessage().split(" ")[0].length()+1).trim();
            String response = APIs.setGame(game);
            if(response == null) return Utilities.format("$STREAM_GAME_CHANGE_MESSAGE$",  message.getSender(), game);
            if(response.equalsIgnoreCase("no_game")) return Utilities.format("$GAME_NOT_FOUND_MESSAGE$",  message.getSender(), game);
            else return response;

        }
        return "";
    }

    public static String runFileSay(ChatMessage message){
        if(message.getArgs().length == 1){
            return Utilities.format("$FILESAY_COMMAND_NO_ARGS_MESSAGE$", message.getSender());
        }

        String url = message.getArgs()[1];

        String[] fileText = APIs.fetchURL(url, true).split("\n");

        for(String line : fileText){
            Main.sendMessage(line);
        }

        return "";
    }

    private static String getValidUserlevel(String userlevel){
        switch (userlevel){
            case "everyone":
                return "everyone";
            case "sub":
            case "subs":
            case "subscriber":
            case "subscribers":
                return "subscriber";
            case "twitch_vip":
            case "vip":
            case "vips":
                return "twitch_vip";
            case "mod":
            case "mods":
            case "moderator":
            case "moderators":
                return "moderator";
            case "owner":
            case "broadcaster":
                return "owner";
        }
        return null;
    }
}
