package com.alphalaneous.Interactive.Commands;

import com.alphalaneous.Audio.Sounds;
import com.alphalaneous.Audio.TTS;
import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Interactive.PersistentVariables;
import com.alphalaneous.Services.Twitch.TwitchChatListener;
import com.alphalaneous.Enums.SoundType;
import com.alphalaneous.Interactive.CustomData;
import com.alphalaneous.Interactive.Keywords.KeywordData;
import com.alphalaneous.Interactive.TwitchExclusive.BasicEvents.BasicEventData;
import com.alphalaneous.Interactive.TwitchExclusive.ChannelPoints.ChannelPointData;
import com.alphalaneous.Interactive.TwitchExclusive.Cheers.CheerData;
import com.alphalaneous.Services.Twitch.TwitchAPI;
import com.alphalaneous.Services.Twitch.TwitchAccount;
import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.Utilities;
import com.eclipsesource.v8.V8;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import com.alphalaneous.Enums.UserLevel;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CommandHandler {

    private static final Random random = new Random();

    public static void run(ChatMessage message) {
        new Thread(() -> {
            String reply = "";

            if(message.isCustomReward()) return;

            CommandData foundCommand = null;

            for (CommandData command : CommandData.getRegisteredCommands()) {
                if ((message.getMessage() + " ").toLowerCase().startsWith(command.getName().toLowerCase() + " ")) {
                    foundCommand = command;
                    break;
                }
            }

            if (foundCommand == null){
                for(String key : CommandData.getRegisteredAliases().keySet()){
                    if ((message.getMessage() + " ").toLowerCase().startsWith(key.toLowerCase() + " ")) {
                        foundCommand = CommandData.getRegisteredAliases().get(key);
                    }
                }
            }

            if (foundCommand != null && foundCommand.isEnabled()
                    && !isCooldown(foundCommand) && checkUserLevel(foundCommand, message)) {

                String response = foundCommand.getMessage();

                reply = replaceBetweenParentheses(message, response, foundCommand, null);
                startCooldown(foundCommand);
            }

            if (!reply.trim().equalsIgnoreCase("")) {
                TwitchChatListener.getCurrentListener().sendMessage(reply, message.getTag("id"));
            }
        }).start();

    }

    public static String replaceBetweenParentheses(ChatMessage message, String text, CustomData data, HashMap<String, String> extraData) {

        if(text == null) text = "";

        return replaceBetweenParentheses(message, text, null, text, data, extraData);
    }

    private static String evaluateIfStatements(String value, ChatMessage message, CustomData commandData, HashMap<String, String> extraData){

        String identifier = value.split(" ")[0];
        String replacement = "";

        if(identifier.toLowerCase().startsWith("if")){

            String innerData = value.substring(2).trim();

            String[] parts = innerData.split("]", 2)[1].split("\\|");
            String ifData = parts[0].trim();
            String elseData = "";
            if(parts.length > 1) {
                elseData = parts[1].trim();
            }

            int state = 0;
            int startPos = 0;
            int endPos = 0;
            all: for(int i = 0; i < innerData.length(); i++) {
                char c = innerData.charAt(i);
                switch (state) {
                    case 0: {
                        if (c == '['){
                            state = 1;
                            startPos = i;
                        }
                        break;
                    }
                    case 1:{
                        if(c == ']'){
                            state = 2;
                            endPos = i;
                            break all;
                        }
                        break;
                    }
                }
            }

            if(state == 2){
                String ifValue = innerData.substring(startPos+1, endPos);
                String ifValueAfter = parseParenthesis(message, ifValue, null, ifValue, commandData, extraData, false);

                if(compare(ifValueAfter)) replacement = ifData;
                else replacement = elseData;
            }
            else{
                replacement = "Malformed If Statement";
            }
        }
        else{
            replacement = "$(" + value + ")";
        }
        return replacement;
    }

    private static boolean compare(String value){

        try {
            V8 v8 = V8.createV8Runtime();
            boolean b = (boolean) v8.executeScript(value);
            v8.getLocker().release();
            return b;

        } catch (Exception e) {
            return false;
        }
    }

    private static String runCommandActions(String value, CustomData customData, ChatMessage message, HashMap<String, String> extraData){

        if(extraData == null){
            extraData = new HashMap<>();
        }

        String data;
        String[] dataArr = value.split(" ", 2);
        if (dataArr.length > 1) data = value.split(" ", 2)[1].trim().strip().replaceAll("\\s+", " ");
        else {
            data = "";
        }
        String identifier = value.split(" ")[0];
        String replacement = "";

        String[] messageSplit = message.getMessage().split(" ");

        switch (identifier.toLowerCase()) {

            case "suppress":
            case "suppress_message":
            case "supressmessage":{
                replacement = "";
                break;
            }

            case "user": {
                replacement = message.getSenderElseDisplay();
                break;
            }
            case "touser":
            case "to_user": {
                if (messageSplit.length > 1) {
                    replacement = messageSplit[1].trim();
                } else {
                    replacement = message.getSenderElseDisplay();
                }
                break;
            }
            case "arg": {
                int arg;
                try {
                    arg = Integer.parseInt(data);
                } catch (NumberFormatException e) {
                    replacement = "Error";
                    break;
                }
                if (messageSplit.length >= arg) {
                    replacement = messageSplit[arg];
                } else {
                    replacement = "Error";
                }
                break;
            }
            case "eval": {
                try {
                    V8 v8 = V8.createV8Runtime();
                    replacement = String.valueOf(v8.executeScript(data));
                    v8.getLocker().release();

                } catch (Exception e) {
                    replacement = e.toString();
                }
                break;
            }
            case "displayname":
            case "display_name": {
                replacement = message.getDisplayName();
                break;
            }
            case "userid":
            case "user_id": {
                replacement = message.getTag("user-id");
                break;
            }
            case "userlevel":
            case "user_level": {
                replacement = String.valueOf(message.getUserLevel());
                break;
            }
            case "messageid":
            case "message_id": {
                replacement = message.getTag("id");
                break;
            }
            case "randomline":
            case "random_line": {

                String content;
                String path = data.toLowerCase();
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
            }
            case "play_sound":
            case "playsound":
            case "sound": {

                if(data.toLowerCase().startsWith("file://")) {
                    Logging.getLogger().info("Playing sound from file: " + dataArr[1].trim().substring(7));
                    Sounds.playSound(dataArr[1].trim().substring(7), true, true, true, false, SoundType.SOUND);
                }
                else Sounds.playSound(dataArr[1].trim(), true, true, false, true, SoundType.SOUND);

                break;
            }


            case "channel":
            case "broadcaster": {
                replacement = TwitchAccount.login;
                break;
            }
            case "channelid":
            case "channel_id": {
                replacement = TwitchAccount.id;
                break;
            }
            case "tts" : {
                TTS.runTTS(data, false);
                break;
            }
            case "tts-overlap" : {
                TTS.runTTS(data, true);
                break;
            }
            case "count" : {
                long count = 0;
                long addedNumber = 1;
                if(data.split(" ").length > 0) {
                    try {
                        addedNumber = Long.parseLong(data.split(" ")[0].trim());
                    }
                    catch (Exception ignored){}
                }
                if(customData != null) {
                    count = customData.getCounter();
                    customData.setCounter(count + addedNumber);
                    customData.save();
                }
                replacement = String.valueOf(count+1);
                break;
            }
            case "message" : {
                replacement = message.getMessage();
                break;
            }
            case "query": {
                String command = message.getMessage().split(" ")[0].trim();
                replacement = message.getMessage().substring(command.length()).trim();
                break;
            }
            case "querystring":
            case "query_string": {
                String command = message.getMessage().split(" ")[0].trim();
                String query = message.getMessage().substring(command.length()).trim();
                replacement = URLEncoder.encode(query, StandardCharsets.UTF_8);
                break;
            }
            case "time": {

                String timezone = data.split(" ")[0].trim();
                String restOfData = "";
                if(data.trim().equalsIgnoreCase("")){
                    timezone = "America/Detroit";
                }
                else {
                    restOfData = data.substring(timezone.length()).trim();
                }
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMMM do yyyy, h:mm:ss a z ZZZZ".replace("do", "'do'"));
                if(restOfData.startsWith("\"") && restOfData.endsWith("\"")){
                    dateTimeFormatter = DateTimeFormatter.ofPattern(restOfData.substring(1, restOfData.length()-1).replace("do", "'do'"));
                }

                ZoneId time = ZoneId.of(timezone);
                ZonedDateTime today = ZonedDateTime.now(time);
                String dayNumberSuffix = getDayNumberSuffix(today.getDayOfMonth());

                replacement = today.format(dateTimeFormatter).replace("do",today.getDayOfMonth() + dayNumberSuffix);
                break;
            }
            case "urlfetch":
            case "url_fetch": {
                try {
                    replacement = Utilities.fetchURL(data);
                }
                catch (Exception e){
                    replacement = "Error";
                }
                break;
            }
            case "messagebreak":
            case "linebreak":
            case "newline" : {
                replacement = "¦";
                break;
            }
            case "followage": {
                replacement = TwitchAPI.getFollowerAge(data.split(" ")[0].trim());
                break;
            }
            case "followage_time": {
                replacement = TwitchAPI.getFollowerAgeTime(data.split(" ")[0].trim());
                break;
            }
            case "weather": {
                if(data.trim().equalsIgnoreCase("")){
                    replacement = "No location provided";
                    break;
                }
                try {
                    replacement = getWeather(data);
                    break;
                }
                catch (Exception e){
                    e.printStackTrace();
                    replacement = "Couldn't find location";
                    break;
                }
            }
            case "foundword": {
                if(customData instanceof KeywordData){
                    replacement = extraData.get("foundWord");
                }
                break;
            }
            case "reward_time": {
                if(customData instanceof ChannelPointData
                        || (customData instanceof BasicEventData
                        && ((BasicEventData) customData).getEvent() == BasicEventData.BasicEvent.REWARD)){
                    replacement = extraData.get("rewardTime");
                }
                break;
            }
            case "reward_cost": {
                if(customData instanceof ChannelPointData
                        || (customData instanceof BasicEventData
                        && ((BasicEventData) customData).getEvent() == BasicEventData.BasicEvent.REWARD)){
                    replacement = extraData.get("rewardCost");
                }
                break;
            }
            case "reward_title": {
                if(customData instanceof ChannelPointData
                        || (customData instanceof BasicEventData
                        && ((BasicEventData) customData).getEvent() == BasicEventData.BasicEvent.REWARD)){
                    replacement = extraData.get("rewardTitle");
                }
                break;
            }
            case "reward_id": {
                if(customData instanceof ChannelPointData){
                    replacement = ((ChannelPointData) customData).getId();
                }
                if(customData instanceof BasicEventData && ((BasicEventData) customData).getEvent() == BasicEventData.BasicEvent.REWARD){
                    replacement = extraData.get("rewardId");
                }
                break;
            }
            case "cheer_amount": {
                if(customData instanceof CheerData
                        || (customData instanceof BasicEventData
                        && ((BasicEventData) customData).getEvent() == BasicEventData.BasicEvent.CHEER)){
                    replacement = extraData.get("cheerAmount");
                }
                break;
            }
            case "raid_time": {
                if(customData instanceof BasicEventData){
                    if(((BasicEventData) customData).getEvent() == BasicEventData.BasicEvent.RAID) {
                        replacement = extraData.get("raidTime");
                    }
                }
                break;
            }
            case "raid_viewers": {
                if(customData instanceof BasicEventData){
                    if(((BasicEventData) customData).getEvent() == BasicEventData.BasicEvent.RAID) {
                        replacement = extraData.get("raidViewers");
                    }
                }
                break;
            }
            case "follow_time": {
                if(customData instanceof BasicEventData){
                    if(((BasicEventData) customData).getEvent() == BasicEventData.BasicEvent.FOLLOW) {
                        replacement = extraData.get("followTime");
                    }
                }
                break;
            }
            case "subscription_time": {
                if(customData instanceof BasicEventData){
                    if(((BasicEventData) customData).getEvent() == BasicEventData.BasicEvent.SUBSCRIBE) {
                        replacement = extraData.get("subscriptionTime");
                    }
                }
                break;
            }
            case "subscription_gifted": {
                if(customData instanceof BasicEventData){
                    if(((BasicEventData) customData).getEvent() == BasicEventData.BasicEvent.SUBSCRIBE) {
                        replacement = extraData.get("subscriptionGifted");
                    }
                }
                break;
            }
            case "subscription_gifted_by": {
                if(customData instanceof BasicEventData){
                    if(((BasicEventData) customData).getEvent() == BasicEventData.BasicEvent.SUBSCRIBE) {
                        replacement = extraData.get("subscriptionGiftedBy");
                    }
                }
                break;
            }
            case "subscription_gifted_months": {
                if(customData instanceof BasicEventData){
                    if(((BasicEventData) customData).getEvent() == BasicEventData.BasicEvent.SUBSCRIBE) {
                        replacement = extraData.get("subscriptionGiftedMonths");
                    }
                }
                break;
            }
            case "subscription_plan": {
                if(customData instanceof BasicEventData){
                    if(((BasicEventData) customData).getEvent() == BasicEventData.BasicEvent.SUBSCRIBE) {
                        replacement = extraData.get("subscriptionPlan");
                    }
                }
                break;
            }
            case "subscription_streak": {
                if(customData instanceof BasicEventData){
                    if(((BasicEventData) customData).getEvent() == BasicEventData.BasicEvent.SUBSCRIBE) {
                        replacement = extraData.get("subscriptionStreak");
                    }
                }
                break;
            }
            case "subscription_multi_month_duration": {
                if(customData instanceof BasicEventData){
                    if(((BasicEventData) customData).getEvent() == BasicEventData.BasicEvent.SUBSCRIBE) {
                        replacement = extraData.get("subscriptionMultiMonthDuration");
                    }
                }
                break;
            }
            case "subscription_multi_month_tenure": {
                if(customData instanceof BasicEventData){
                    if(((BasicEventData) customData).getEvent() == BasicEventData.BasicEvent.SUBSCRIBE) {
                        replacement = extraData.get("subscriptionMultiMonthTenure");
                    }
                }
                break;
            }
            case "subscription_months": {
                if(customData instanceof BasicEventData){
                    if(((BasicEventData) customData).getEvent() == BasicEventData.BasicEvent.SUBSCRIBE) {
                        replacement = extraData.get("subscriptionMonths");
                    }
                }
                break;
            }
            case "subscription_length": {
                if(customData instanceof BasicEventData){
                    if(((BasicEventData) customData).getEvent() == BasicEventData.BasicEvent.SUBSCRIBE) {
                        replacement = extraData.get("subscriptionLength");
                    }
                }
                break;
            }
            case "set_var": {
                String[] args = data.split(" ");

                if(args.length == 2) {

                    String varName = args[0].trim();
                    String variable = args[1].trim();

                    PersistentVariables.set(varName, variable);
                }
                else{
                    replacement = "Invalid Arguments";
                }
                break;
            }
            case "get_var": {
                String[] args = data.split(" ");

                if(args.length == 1) {

                    String varName = args[0].trim();

                    replacement = PersistentVariables.get(varName);
                }
                else{
                    replacement = "Invalid Arguments";
                }
                break;
            }
            /*case "ad_soon": {
                if(customData instanceof BasicEventData){
                    if(((BasicEventData) customData).getEvent() == BasicEventData.BasicEvent.AD) {
                        replacement = extraData.get("adSoon");
                    }
                }
                break;
            }
            case "ad_started": {
                if(customData instanceof BasicEventData){
                    if(((BasicEventData) customData).getEvent() == BasicEventData.BasicEvent.AD) {
                        replacement = extraData.get("adStarted");
                    }
                }
                break;
            }
            case "ad_ended": {
                if(customData instanceof BasicEventData){
                    if(((BasicEventData) customData).getEvent() == BasicEventData.BasicEvent.AD) {
                        replacement = extraData.get("adEnded");
                    }
                }
                break;
            }
            case "ad_duration_until": {
                if(customData instanceof BasicEventData){
                    if(((BasicEventData) customData).getEvent() == BasicEventData.BasicEvent.AD) {
                        replacement = extraData.get("adDurationUntil");
                    }
                }
                break;
            }
            case "ad_duration": {
                if(customData instanceof BasicEventData){
                    if(((BasicEventData) customData).getEvent() == BasicEventData.BasicEvent.AD) {
                        replacement = extraData.get("adDuration");
                    }
                }
                break;
            }*/
            case "emptymessage":
            case "empty_message": {
                String command = message.getMessage().split(" ")[0].trim();
                String query = message.getMessage().substring(command.length()).trim();
                replacement = String.valueOf(query.trim().isEmpty());
                break;
            }
            default: {
                replacement = "$(" + value + ")";
                break;
            }
        }
        return replacement;
    }

    public static String parseParenthesis(ChatMessage message, String text, ArrayList<ParenthesisSubstrings> parenthesisSubstrings, String original, CustomData customData, HashMap<String, String> extraData, boolean ifPass){

        String newResult = original;

        if (parenthesisSubstrings == null) parenthesisSubstrings = new ArrayList<>();

        int pValue = 0;
        int sIndex = 0;
        int eIndex = 0;


        if(text == null) {
            text = "";
        }

        for (int i = 1; i < text.length(); i++) {
            if (text.charAt(i) == '(' && text.charAt(i - 1) != '\\') {
                pValue++;
                sIndex = i;
            }
            if (text.charAt(i) == ')' && text.charAt(i - 1) != '\\') {
                if (pValue != 0) {
                    eIndex = i + 1;
                    break;
                }
            }
        }
        String strS = text.substring(0, sIndex);
        String strE = text.substring(eIndex);

        if (pValue != 0) {
            try {
                parenthesisSubstrings.add(new ParenthesisSubstrings(sIndex, eIndex - 1, original.substring(sIndex + 1, eIndex - 1)));
            } catch (IndexOutOfBoundsException e) {
                return "Malformed Command String";
            }
            String filled = StringUtils.repeat("█", eIndex - sIndex);
            String result = strS + filled + strE;
            return parseParenthesis(message, result, parenthesisSubstrings, original, customData, extraData, ifPass);
        }

        String lastResult = original;

        for (ParenthesisSubstrings parenthesisSubstrings1 : parenthesisSubstrings) {

            if (parenthesisSubstrings1.getStartIndex() - 1 >= 0) {
                if (newResult.charAt(parenthesisSubstrings1.getStartIndex() - 1) == '$') {

                    String strStart = newResult.substring(0, parenthesisSubstrings1.getStartIndex() - 1);
                    String strEnd = newResult.substring(parenthesisSubstrings1.getEndIndex() + 1);
                    String value = newResult.substring(parenthesisSubstrings1.getStartIndex() + 1, parenthesisSubstrings1.getEndIndex());

                    String replacement = "";
                    if (ifPass) {
                        replacement = evaluateIfStatements(value, message, customData, extraData);
                    } else {
                        replacement = runCommandActions(value, customData, message, extraData);
                    }


                    newResult = strStart + replacement + strEnd;

                    for (ParenthesisSubstrings substring : parenthesisSubstrings) {
                        if (parenthesisSubstrings1.getStartIndex() < substring.getStartIndex()) {
                            substring.shiftIndex(newResult.length() - lastResult.length());
                        }
                        if (parenthesisSubstrings1.getStartIndex() > substring.getStartIndex() && parenthesisSubstrings1.getEndIndex() < substring.getEndIndex()) {
                            substring.shiftEndIndex(newResult.length() - lastResult.length());
                        }
                    }

                    lastResult = newResult;
                }
            }
        }

        return newResult;
    }

    public static String replaceBetweenParentheses(ChatMessage message, String text, ArrayList<ParenthesisSubstrings> parenthesisSubstrings, String original, CustomData customData, HashMap<String, String> extraData) {

        String value = parseParenthesis(message, text, parenthesisSubstrings, original, customData, extraData, true);
        return parseParenthesis(message, value, parenthesisSubstrings, value, customData, extraData, false);

    }

    private static String getWeather(String data){

        JSONObject weatherBasicObject = new JSONObject(Utilities.fetchURL("https://api.openweathermap.org/data/2.5/weather?q=" + data.trim() + "&appid=f5d9ac78d3e44fa1ada880cef1e68b11"));
        JSONArray weatherArray = weatherBasicObject.getJSONArray("weather");
        JSONObject weatherObject = weatherArray.getJSONObject(0);
        JSONObject mainObject = weatherBasicObject.getJSONObject("main");
        JSONObject windObject = weatherBasicObject.getJSONObject("wind");


        int kelvinTemp = mainObject.getInt("temp");
        double windSpeedMetersSec = windObject.getDouble("speed");
        int windDegree = windObject.getInt("deg");
        String location = weatherBasicObject.getString("name");
        int humidity = mainObject.getInt("humidity");

        int celsius = (int) (kelvinTemp - 273.15);
        int fahrenheit = celsius * 9/5 + 32;

        int mph = (int) (windSpeedMetersSec * 2.237);
        int kmph = (int) (windSpeedMetersSec * 3.6);
        String[] directions = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW", "N"};
        String direction = directions[ (int)Math.round((((double)windDegree % 360) / 22.5)) ];

        String condition = weatherObject.getString("main");
        String conditionDesc = weatherObject.getString("description");
        switch (conditionDesc.toLowerCase()) {

            case "thunderstorm with light rain":
                condition = "Thunderstorms with Light Rain";
                break;
            case "thunderstorm with rain":
                condition = "Thunderstorms with Rain";
                break;
            case "thunderstorm with heavy rain":
                condition = "Thunderstorms with Heavy Rain";
                break;
            case "thunderstorm":
                condition = "Thunderstorms";
                break;
            case "heavy thunderstorm ":
                condition = "Heavy Thunderstorms";
                break;
            case "ragged thunderstorm ":
                condition = "Ragged Thunderstorms";
                break;
            case "thunderstorm with light drizzle":
                condition = "Thunderstorms with Light Drizzle";
                break;
            case "thunderstorm with drizzle":
                condition = "Thunderstorms with Drizzle";
                break;
            case "thunderstorm with heavy drizzle":
                condition = "Thunderstorms with Heavy Drizzle";
                break;
            case "light intensity drizzle":
                condition = "a Light Drizzle";
                break;
            case "drizzle":
                condition = "a Drizzle";
                break;
            case "heavy intensity drizzle":
                condition = "a Heavy Drizzle";
                break;
            case "light intensity drizzle rain":
                condition = "a Light Rainly Drizzle";
                break;
            case "drizzle rain":
                condition = "a Rainy Drizzle";
                break;
            case "heavy intensity drizzle rain":
                condition = "a Heavy Drizzle Rain";
                break;
            case "shower drizzle":
            case "shower rain and drizzle":
                condition = "a Drizzle with Showers";
                break;
            case "heavy shower rain and drizzle":
                condition = "a Drizzle with Heavy Showers";
                break;
            case "light rain":
                condition = "a Light Rain";
                break;
            case "moderate rain":
                condition = "Moderate Rain";
                break;
            case "heavy intensity rain":
                condition = "Heavy Rain";
                break;
            case "very heavy rain":
                condition = "Very Heavy Rain";
                break;
            case "extreme rain":
                condition = "Extreme Rain";
                break;
            case "freezing rain":
                condition = "Freezing Rain";
                break;
            case "light intensity shower rain":
                condition = "Light Rain Showers";
                break;
            case "shower rain":
                condition = "Rain Showers";
                break;
            case "heavy intensity shower rain":
                condition = "Heavy Rain Showers";
                break;
            case "ragged shower rain":
                condition = "Ragged Rain Showers";
                break;
            case "light snow":
                condition = "Light Snow";
                break;
            case "snow":
                condition = "Snowy";
                break;
            case "heavy snow":
                condition = "Heavy Snow";
                break;
            case "sleet":
                condition = "Sleet";
                break;
            case "light shower sleet":
                condition = "Light Shower Sleet";
                break;
            case "shower sleet ":
                condition = "Shower Sleet";
                break;
            case "light rain and snow":
                condition = "Light Rain and Snow";
                break;
            case "rain and snow":
                condition = "Rain and Snow";
                break;
            case "light shower snow":
                condition = "Light Shower Snow";
                break;
            case "shower snow":
                condition = "Shower Snow";
                break;
            case "heavy shower snow":
                condition = "Heavy Shower Snow";
                break;
        }

        switch (condition) {
            case "Mist":
                condition = "Misty";
                break;
            case "Smoke":
                condition = "Smokey";
                break;
            case "Haze":
                condition = "Hazy";
                break;
            case "Dust":
                condition = "Dusty";
                break;
            case "Fog":
                condition = "Foggy";
                break;
            case "Sand":
                condition = "Sandy";
                break;
            case "Ash":
                condition = "Ashy";
                break;
            case "Squall":
                condition = "Squall";
                break;
            case "Tornado":
                condition = "Tornadoes";
                break;
            case "Clear":
                condition = "Clear";
                break;
        }
        String cloudDesc = conditionDesc.split(":")[0];
        switch (cloudDesc){
            case "few clouds":
                condition = "Partly Cloudy";
                break;
            case "scattered clouds":
                condition = "Scattered Clouds";
                break;
            case "broken clouds":
                condition = "Broken Clouds";
                break;
            case "overcast clouds":
                condition = "Overcast";
                break;
        }
        return "Weather for "
                + location + ": Conditions are "
                + condition + " with a temperature of "
                + fahrenheit + "F (" + celsius + "C). The wind is blowing from the "
                + direction + " at "
                + mph + "mph (" + kmph + "km/h) and the current humidity is "
                + humidity + "%.";
    }
    private static String getDayNumberSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }
    private static final ArrayList<CommandData> commandDataList = new ArrayList<>();

    public static void startCooldown(CommandData commandData){
        new Thread(() -> {
            commandDataList.add(commandData);
            Utilities.sleep(commandData.getCooldown()*1000);
            commandDataList.remove(commandData);
        }).start();

    }
    public static boolean isCooldown(CommandData data){

        for(CommandData commandData : commandDataList){
            return commandData.getName().equalsIgnoreCase(data.getName());
        }
        return false;
    }
    public static boolean checkUserLevel(CommandData data, ChatMessage message){

        UserLevel commandLevel = data.getUserLevel();
        UserLevel messageLevel = message.getUserLevel();

        return UserLevel.checkLevel(commandLevel, messageLevel);
    }


    private static class ParenthesisSubstrings {

        private int startIndex;
        private int endIndex;
        private final String string;

        ParenthesisSubstrings(int startIndex, int endIndex, String string) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.string = string;
        }

        public void shiftIndex(int shift){
            this.startIndex += shift;
            this.endIndex += shift;
        }

        public void shiftEndIndex(int shift){
            this.endIndex += shift;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public String getString() {
            return string;
        }
    }
}
