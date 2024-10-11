package com.alphalaneous.Interactive.Commands;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Audio.Sounds;
import com.alphalaneous.Audio.TTS;
import com.alphalaneous.Enums.SoundType;
import com.alphalaneous.Interactive.Keywords.KeywordData;
import com.alphalaneous.Interactive.PersistentVariables;
import com.alphalaneous.Interactive.TwitchExclusive.BasicEvents.BasicEventData;
import com.alphalaneous.Interactive.TwitchExclusive.ChannelPoints.ChannelPointData;
import com.alphalaneous.Interactive.TwitchExclusive.Cheers.CheerData;
import com.alphalaneous.Interfaces.CommandAction;
import com.alphalaneous.Services.Twitch.TwitchAPI;
import com.alphalaneous.Services.Twitch.TwitchAccount;
import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.Utilities;
import com.eclipsesource.v8.V8;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class LoadCommandActions {

    private static final HashMap<String, CommandAction> commandActionHashMap = new HashMap<>();

    @OnLoad(test = true)
    public static void internalFunction(){

        CommandAction action = data -> {
            if (data.getCustomData() instanceof DefaultCommandData) {
                return ((DefaultCommandData) data.getCustomData()).getFunction().run(data);
            }
            return "";
        };

        commandActionHashMap.put("internal-function", action);
    }

    @OnLoad(test = true)
    public static void suppressAction(){
        CommandAction action = data -> "";

        commandActionHashMap.put("suppress", action);
        commandActionHashMap.put("suppress_message", action);
        commandActionHashMap.put("suppressmessage", action);
    }

    @OnLoad(test = true)
    public static void user(){
        CommandAction action = data -> data.getMessage().getSenderElseDisplay();

        commandActionHashMap.put("user", action);
    }

    @OnLoad(test = true)
    public static void toUser(){
        CommandAction action = data -> {

            if (data.getMessageParts().length > 1) {
                return data.getMessageParts()[1].trim();
            } else {
                return data.getMessage().getSenderElseDisplay();
            }
        };

        commandActionHashMap.put("touser", action);
        commandActionHashMap.put("to_user", action);

    }

    @OnLoad(test = true)
    public static void arg(){
        CommandAction action = data -> {

            int arg;
            try {
                arg = Integer.parseInt(data.afterIdentifier());
            } catch (NumberFormatException e) {
                return "Error";
            }
            if (data.getMessageParts().length >= arg) {
                return data.getMessageParts()[arg];
            } else {
                return "Error";
            }
        };

        commandActionHashMap.put("arg", action);
    }

    @OnLoad(test = true)
    public static void eval(){
        CommandAction action = data -> {

            try {
                V8 v8 = V8.createV8Runtime();
                String returnValue = String.valueOf(v8.executeScript(data.afterIdentifier()));
                v8.getLocker().release();
                return returnValue;

            } catch (Exception e) {
                return e.toString();
            }
        };

        commandActionHashMap.put("eval", action);
    }

    @OnLoad(test = true)
    public static void displayName(){
        CommandAction action = data -> data.getMessage().getDisplayName();

        commandActionHashMap.put("displayname", action);
        commandActionHashMap.put("display_name", action);
    }

    @OnLoad(test = true)
    public static void userId(){
        CommandAction action = data -> data.getMessage().getTag("user-id");

        commandActionHashMap.put("userid", action);
        commandActionHashMap.put("user_id", action);
    }

    @OnLoad(test = true)
    public static void userLevel(){
        CommandAction action = data -> String.valueOf(data.getMessage().getUserLevel());

        commandActionHashMap.put("userlevel", action);
        commandActionHashMap.put("user_level", action);
    }

    @OnLoad(test = true)
    public static void messageId(){
        CommandAction action = data -> data.getMessage().getTag("id");

        commandActionHashMap.put("messageid", action);
        commandActionHashMap.put("message_id", action);
    }

    private static final Random random = new Random();

    @OnLoad(test = true)
    public static void randomLine(){
        CommandAction action = data -> {

            String content;
            String path = data.afterIdentifier().toLowerCase();
            if(path.startsWith("file://") || path.startsWith("file:\\\\")){
                try {
                    content = new String(Files.readAllBytes(Paths.get(path.substring("file://".length()))));
                } catch (IOException e) {
                    return "Invalid File Path";
                }

            }
            else{
                try {
                    content = new Scanner(new URL(path).openStream(), StandardCharsets.UTF_8).useDelimiter("\\A").next();
                }
                catch (Exception e){
                    return "Invalid URL";
                }
            }

            String[] lines = content.split("\n");

            return lines[random.nextInt(lines.length)];

        };

        commandActionHashMap.put("randomline", action);
        commandActionHashMap.put("random_line", action);
    }

    @OnLoad(test = true)
    public static void playSound(){
        CommandAction action = data -> {

            if(data.afterIdentifier().toLowerCase().startsWith("file://")) {
                Logging.getLogger().info("Playing sound from file: " + data.afterIdentifier().substring(7));
                Sounds.playSound(data.afterIdentifier().substring(7), true, true, true, false, SoundType.SOUND);
            }
            else Sounds.playSound(data.afterIdentifier(), true, true, false, true, SoundType.SOUND);

            return "";
        };

        commandActionHashMap.put("play_sound", action);
        commandActionHashMap.put("playsound", action);
        commandActionHashMap.put("sound", action);
    }

    @OnLoad(test = true)
    public static void channel(){
        CommandAction action = data -> TwitchAccount.login;

        commandActionHashMap.put("channel", action);
        commandActionHashMap.put("broadcaster", action);
    }

    @OnLoad(test = true)
    public static void channelId(){
        CommandAction action = data -> TwitchAccount.id;

        commandActionHashMap.put("channelid", action);
        commandActionHashMap.put("channel_id", action);
    }

    @OnLoad(test = true)
    public static void tts(){
        CommandAction action = data -> {
            TTS.runTTS(data.afterIdentifier(), false);
            return "";
        };

        commandActionHashMap.put("tts", action);
    }

    @OnLoad(test = true)
    public static void ttsOverlap(){
        CommandAction action = data -> {
            TTS.runTTS(data.afterIdentifier(), true);
            return "";
        };

        commandActionHashMap.put("ttsoverlap", action);
        commandActionHashMap.put("tts-overlap", action);
        commandActionHashMap.put("tts_overlap", action);

    }

    @OnLoad(test = true)
    public static void count(){
        CommandAction action = data -> {
            long count = 0;
            long addedNumber = 1;
            if(data.afterIdentifier().split(" ").length > 0) {
                try {
                    addedNumber = Long.parseLong(data.afterIdentifier().split(" ")[0].trim());
                }
                catch (Exception ignored){}
            }
            if(data.getCustomData() != null) {
                count = data.getCustomData().getCounter();
                data.getCustomData().setCounter(count + addedNumber);
                data.getCustomData().save();
            }
            return String.valueOf(count+1);
        };

        commandActionHashMap.put("count", action);
    }

    @OnLoad(test = true)
    public static void message(){
        CommandAction action = data -> data.getMessage().getMessage();

        commandActionHashMap.put("message", action);
    }

    @OnLoad(test = true)
    public static void query(){
        CommandAction action = data -> {
            String command = data.getMessage().getMessage().split(" ")[0].trim();
            return data.getMessage().getMessage().substring(command.length()).trim();
        };

        commandActionHashMap.put("query", action);
    }

    @OnLoad(test = true)
    public static void queryString(){
        CommandAction action = data -> {
            String command = data.getMessage().getMessage().split(" ")[0].trim();
            String query = data.getMessage().getMessage().substring(command.length()).trim();
            return URLEncoder.encode(query, StandardCharsets.UTF_8);
        };

        commandActionHashMap.put("querystring", action);
        commandActionHashMap.put("query_string", action);
    }

    @OnLoad(test = true)
    public static void time(){
        CommandAction action = data -> {
            String timezone = data.afterIdentifier().split(" ")[0].trim();
            String restOfData = "";
            if(data.afterIdentifier().trim().equalsIgnoreCase("")){
                timezone = "America/Detroit";
            }
            else {
                restOfData = data.afterIdentifier().substring(timezone.length()).trim();
            }
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMMM do yyyy, h:mm:ss a z ZZZZ".replace("do", "'do'"));
            if(restOfData.startsWith("\"") && restOfData.endsWith("\"")){
                dateTimeFormatter = DateTimeFormatter.ofPattern(restOfData.substring(1, restOfData.length()-1).replace("do", "'do'"));
            }

            ZoneId time = ZoneId.of(timezone);
            ZonedDateTime today = ZonedDateTime.now(time);
            String dayNumberSuffix = CommandHandler.getDayNumberSuffix(today.getDayOfMonth());

            return today.format(dateTimeFormatter).replace("do",today.getDayOfMonth() + dayNumberSuffix);
        };

        commandActionHashMap.put("time", action);
    }

    @OnLoad(test = true)
    public static void urlFetch(){
        CommandAction action = data -> {
            try {
                return Utilities.fetchURL(data.afterIdentifier());
            }
            catch (Exception e){
                return "Error";
            }
        };

        commandActionHashMap.put("urlfetch", action);
        commandActionHashMap.put("url_fetch", action);
    }

    @OnLoad(test = true)
    public static void messageBreak(){
        CommandAction action = data -> "¦";

        commandActionHashMap.put("messagebreak", action);
        commandActionHashMap.put("message_break", action);
        commandActionHashMap.put("linebreak", action);
        commandActionHashMap.put("line_break", action);
        commandActionHashMap.put("newline", action);
        commandActionHashMap.put("new_line", action);
    }

    @OnLoad(test = true)
    public static void followAge(){
        CommandAction action = data -> TwitchAPI.getFollowerAge(data.afterIdentifier().split(" ")[0].trim());

        commandActionHashMap.put("followage", action);
    }

    @OnLoad(test = true)
    public static void followTime(){
        CommandAction action = data -> TwitchAPI.getFollowerAgeTime(data.afterIdentifier().split(" ")[0].trim());

        commandActionHashMap.put("followage_time", action);
    }

    @OnLoad(test = true)
    public static void weather(){
        CommandAction action = data -> {

            if(data.afterIdentifier().trim().equalsIgnoreCase("")){
                return "No location provided";
            }
            try {
                return CommandHandler.getWeather(data.afterIdentifier());
            }
            catch (Exception e){
                return "Couldn't find location";
            }
        };

        commandActionHashMap.put("weather", action);
    }

    @OnLoad(test = true)
    public static void foundWord(){
        CommandAction action = data -> {

            if(data.getCustomData() instanceof KeywordData){
                return data.getExtraData().get("foundWord");
            }
            return "";
        };

        commandActionHashMap.put("foundword", action);
    }

    @OnLoad(test = true)
    public static void rewardTime(){
        CommandAction action = data -> {

            if(data.getCustomData() instanceof ChannelPointData
                    || (data.getCustomData() instanceof BasicEventData
                    && ((BasicEventData) data.getCustomData()).getEvent() == BasicEventData.BasicEvent.REWARD)){
                return data.getExtraData().get("rewardTime");
            }
            return "";
        };

        commandActionHashMap.put("reward_time", action);
    }

    @OnLoad(test = true)
    public static void rewardCost(){
        CommandAction action = data -> {

            if(data.getCustomData() instanceof ChannelPointData
                    || (data.getCustomData() instanceof BasicEventData
                    && ((BasicEventData) data.getCustomData()).getEvent() == BasicEventData.BasicEvent.REWARD)){
                return data.getExtraData().get("rewardCost");
            }
            return "";
        };

        commandActionHashMap.put("reward_cost", action);
    }

    @OnLoad(test = true)
    public static void rewardTitle(){
        CommandAction action = data -> {

            if(data.getCustomData() instanceof ChannelPointData
                    || (data.getCustomData() instanceof BasicEventData
                    && ((BasicEventData) data.getCustomData()).getEvent() == BasicEventData.BasicEvent.REWARD)){
                return data.getExtraData().get("rewardTitle");
            }
            return "";
        };

        commandActionHashMap.put("reward_title", action);
    }

    @OnLoad(test = true)
    public static void rewardId(){
        CommandAction action = data -> {

            if(data.getCustomData() instanceof ChannelPointData){
                return ((ChannelPointData) data.getCustomData()).getId();
            }
            if(data.getCustomData() instanceof BasicEventData && ((BasicEventData) data.getCustomData()).getEvent() == BasicEventData.BasicEvent.REWARD){
                return data.getExtraData().get("rewardId");
            }
            return "";
        };

        commandActionHashMap.put("reward_id", action);
    }

    @OnLoad(test = true)
    public static void cheerAmount(){
        CommandAction action = data -> {

            if(data.getCustomData() instanceof CheerData
                    || (data.getCustomData() instanceof BasicEventData
                    && ((BasicEventData) data.getCustomData()).getEvent() == BasicEventData.BasicEvent.CHEER)){
                return data.getExtraData().get("cheerAmount");
            }
            return "";
        };

        commandActionHashMap.put("cheer_amount", action);
    }

    @OnLoad(test = true)
    public static void raidTime(){
        CommandAction action = data -> {

            if(data.getCustomData() instanceof BasicEventData){
                if(((BasicEventData) data.getCustomData()).getEvent() == BasicEventData.BasicEvent.RAID) {
                    return data.getExtraData().get("raidTime");
                }
            }
            return "";
        };

        commandActionHashMap.put("raid_time", action);
    }

    @OnLoad(test = true)
    public static void raidViewers(){
        CommandAction action = data -> {

            if(data.getCustomData() instanceof BasicEventData){
                if(((BasicEventData) data.getCustomData()).getEvent() == BasicEventData.BasicEvent.RAID) {
                    return data.getExtraData().get("raidViewers");
                }
            }
            return "";
        };

        commandActionHashMap.put("raid_viewers", action);
    }

    @OnLoad(test = true)
    public static void followEventTime(){
        CommandAction action = data -> {

            if(data.getCustomData() instanceof BasicEventData){
                if(((BasicEventData) data.getCustomData()).getEvent() == BasicEventData.BasicEvent.FOLLOW) {
                    return data.getExtraData().get("followTime");
                }
            }
            return "";
        };

        commandActionHashMap.put("follow_time", action);
    }

    @OnLoad(test = true)
    public static void subscriptionTime(){
        CommandAction action = data -> {

            if(data.getCustomData() instanceof BasicEventData){
                if(((BasicEventData) data.getCustomData()).getEvent() == BasicEventData.BasicEvent.SUBSCRIBE) {
                    return data.getExtraData().get("subscriptionTime");
                }
            }
            return "";
        };

        commandActionHashMap.put("subscription_time", action);
    }

    @OnLoad(test = true)
    public static void subscriptionGifted(){
        CommandAction action = data -> {

            if(data.getCustomData() instanceof BasicEventData){
                if(((BasicEventData) data.getCustomData()).getEvent() == BasicEventData.BasicEvent.SUBSCRIBE) {
                    return data.getExtraData().get("subscriptionGifted");
                }
            }
            return "";
        };

        commandActionHashMap.put("subscription_gifted", action);
    }

    @OnLoad(test = true)
    public static void subscriptionGiftedBy(){
        CommandAction action = data -> {

            if(data.getCustomData() instanceof BasicEventData){
                if(((BasicEventData) data.getCustomData()).getEvent() == BasicEventData.BasicEvent.SUBSCRIBE) {
                    return data.getExtraData().get("subscriptionGiftedBy");
                }
            }
            return "";
        };

        commandActionHashMap.put("subscription_gifted_by", action);
    }

    @OnLoad(test = true)
    public static void subscriptionGiftedMonths(){
        CommandAction action = data -> {

            if(data.getCustomData() instanceof BasicEventData){
                if(((BasicEventData) data.getCustomData()).getEvent() == BasicEventData.BasicEvent.SUBSCRIBE) {
                    return data.getExtraData().get("subscriptionGiftedMonths");
                }
            }
            return "";
        };

        commandActionHashMap.put("subscription_gifted_months", action);
    }

    @OnLoad(test = true)
    public static void subscriptionPlan(){
        CommandAction action = data -> {

            if(data.getCustomData() instanceof BasicEventData){
                if(((BasicEventData) data.getCustomData()).getEvent() == BasicEventData.BasicEvent.SUBSCRIBE) {
                    return data.getExtraData().get("subscriptionPlan");
                }
            }
            return "";
        };

        commandActionHashMap.put("subscription_plan", action);
    }

    @OnLoad(test = true)
    public static void subscriptionStreak(){
        CommandAction action = data -> {

            if(data.getCustomData() instanceof BasicEventData){
                if(((BasicEventData) data.getCustomData()).getEvent() == BasicEventData.BasicEvent.SUBSCRIBE) {
                    return data.getExtraData().get("subscriptionStreak");
                }
            }
            return "";
        };

        commandActionHashMap.put("subscription_streak", action);
    }

    @OnLoad(test = true)
    public static void subscriptionMultiMonthDuration(){
        CommandAction action = data -> {

            if(data.getCustomData() instanceof BasicEventData){
                if(((BasicEventData) data.getCustomData()).getEvent() == BasicEventData.BasicEvent.SUBSCRIBE) {
                    return data.getExtraData().get("subscriptionMultiMonthDuration");
                }
            }
            return "";
        };

        commandActionHashMap.put("subscription_multi_month_duration", action);
    }

    @OnLoad(test = true)
    public static void subscriptionMultiMonthTenure(){
        CommandAction action = data -> {

            if(data.getCustomData() instanceof BasicEventData){
                if(((BasicEventData) data.getCustomData()).getEvent() == BasicEventData.BasicEvent.SUBSCRIBE) {
                    return data.getExtraData().get("subscriptionMultiMonthTenure");
                }
            }
            return "";
        };

        commandActionHashMap.put("subscription_multi_month_tenure", action);
    }

    @OnLoad(test = true)
    public static void subscriptionMonths(){
        CommandAction action = data -> {

            if(data.getCustomData() instanceof BasicEventData){
                if(((BasicEventData) data.getCustomData()).getEvent() == BasicEventData.BasicEvent.SUBSCRIBE) {
                    return data.getExtraData().get("subscriptionMonths");
                }
            }
            return "";
        };

        commandActionHashMap.put("subscription_months", action);
    }

    @OnLoad(test = true)
    public static void subscriptionLength(){
        CommandAction action = data -> {

            if(data.getCustomData() instanceof BasicEventData){
                if(((BasicEventData) data.getCustomData()).getEvent() == BasicEventData.BasicEvent.SUBSCRIBE) {
                    return data.getExtraData().get("subscriptionLength");
                }
            }
            return "";
        };

        commandActionHashMap.put("subscription_length", action);
    }

    @OnLoad(test = true)
    public static void setVar(){
        CommandAction action = data -> {

            String[] args = data.afterIdentifier().split(" ");

            if(args.length == 2) {

                String varName = args[0].trim();
                String variable = args[1].trim();

                PersistentVariables.set(varName, variable);
                return "";
            }
            else{
                return "Invalid Arguments";
            }
        };

        commandActionHashMap.put("set_var", action);
    }

    @OnLoad(test = true)
    public static void getVar(){
        CommandAction action = data -> {

            String[] args = data.afterIdentifier().split(" ");

            if(args.length == 1) {

                String varName = args[0].trim();

                return PersistentVariables.get(varName);
            }
            else{
                return "Invalid Arguments";
            }
        };

        commandActionHashMap.put("get_var", action);
    }

    @OnLoad(test = true)
    public static void emptyMessage(){
        CommandAction action = data -> {
            String command = data.getMessage().getMessage().split(" ")[0].trim();
            String query = data.getMessage().getMessage().substring(command.length()).trim();
            return String.valueOf(query.trim().isEmpty());

        };

        commandActionHashMap.put("emptymessage", action);
        commandActionHashMap.put("empty_message", action);
    }


    public static void registerCommandAction(String identifier, CommandAction action){
        commandActionHashMap.put(identifier, action);
    }

    public static CommandAction getCommandAction(String identifier){
        return commandActionHashMap.get(identifier);
    }

    public static boolean containsAction(String identifier){
        return commandActionHashMap.containsKey(identifier);
    }

}
