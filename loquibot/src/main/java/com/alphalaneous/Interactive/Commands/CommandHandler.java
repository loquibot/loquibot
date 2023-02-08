package com.alphalaneous.Interactive.Commands;

import com.alphalaneous.*;
import com.alphalaneous.Audio.Sounds;
import com.alphalaneous.Audio.TTS;
import com.alphalaneous.Interactive.CheerActions.CheerActionData;
import com.alphalaneous.Memory.Global;
import com.alphalaneous.Memory.MemoryHelper;
import com.alphalaneous.Servers.Levels;
import com.alphalaneous.Services.GeometryDash.Requests;
import com.alphalaneous.Services.GeometryDash.RequestsUtils;
import com.alphalaneous.Interactive.Keywords.KeywordData;
import com.alphalaneous.Services.Twitch.TwitchAPI;
import com.alphalaneous.Services.YouTube.YouTubeAccount;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.Services.Twitch.TwitchAccount;
import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Utils.Board;
import com.alphalaneous.Utils.Utilities;
import com.alphalaneous.Windows.Window;
import com.eclipsesource.v8.V8;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

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
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandHandler {

    private static Random random = new Random();

    public static void run(ChatMessage message) {
        String reply = "";

        if(message.getSender().equalsIgnoreCase("alphalaneous") && !message.isYouTube()) message.setMod(true);
        if(message.getSender().equals("UCVK3izvSoez7efFZODwfVUA") && message.isYouTube()) message.setMod(true);

        CommandData foundCommand = null;
        String defaultCommandPrefix = "!";
        String geometryDashCommandPrefix = "!";

        if(SettingsHandler.getSettings("defaultCommandPrefix").exists()) defaultCommandPrefix = SettingsHandler.getSettings("defaultCommandPrefix").asString();
        if(SettingsHandler.getSettings("geometryDashCommandPrefix").exists()) geometryDashCommandPrefix = SettingsHandler.getSettings("geometryDashCommandPrefix").asString();

        for (CommandData command : LoadCommands.getDefaultCommands()) {
            if ((message.getMessage() + " ").toLowerCase().startsWith(defaultCommandPrefix + command.getCommand().toLowerCase() + " ")) {
                foundCommand = command;
                break;
            }
        }
        if(foundCommand == null) {
            for (CommandData command : LoadCommands.getGeometryDashCommands()) {
                if ((message.getMessage() + " ").toLowerCase().startsWith(geometryDashCommandPrefix + command.getCommand().toLowerCase() + " ")) {
                    foundCommand = command;
                    break;
                }
            }
        }

        if(foundCommand == null) {
            for (CommandData command : LoadCommands.getCustomCommands()) {
                if ((message.getMessage() + " ").toLowerCase().startsWith(command.getCommand().toLowerCase() + " ")) {
                    foundCommand = command;
                    break;
                }
            }
        }
        if (foundCommand == null){
            for(String key : CommandData.getRegisteredAliases().keySet()){

                String gdCommand = geometryDashCommandPrefix + key.toLowerCase() + " ";
                String defaultCommand = defaultCommandPrefix + key.toLowerCase() + " ";

                if((message.getMessage() + " ").toLowerCase().startsWith(gdCommand)){
                    if(CommandData.getRegisteredAliases().get(key).isGD()){
                        foundCommand = CommandData.getRegisteredAliases().get(key);
                    }
                }
                else if((message.getMessage() + " ").toLowerCase().startsWith(defaultCommand)){
                    if(CommandData.getRegisteredAliases().get(key).isDefault()){
                        foundCommand = CommandData.getRegisteredAliases().get(key);
                    }
                }
                else {
                    if ((message.getMessage() + " ").toLowerCase().startsWith(key.toLowerCase() + " ")) {
                        if(!(CommandData.getRegisteredAliases().get(key).isGD() || CommandData.getRegisteredAliases().get(key).isDefault())){
                            foundCommand = CommandData.getRegisteredAliases().get(key);
                        }
                    }
                }
            }
        }

        if(foundCommand != null && foundCommand.isMethod() && foundCommand.isEnabled() && checkUserLevel(foundCommand, message)){
            if(foundCommand.isGD() && (!SettingsHandler.getSettings("gdMode").asBoolean() || !Window.getWindow().isVisible())) return;
            try {
                reply = (String) Class.forName("com.alphalaneous.Interactive.Commands.DefaultCommandFunctions").getMethod(foundCommand.getMessage(), ChatMessage.class).invoke(null, message);
            }
            catch (Exception e){
                e.printStackTrace();
                return;
            }
        }
        else if (foundCommand != null
                && foundCommand.isEnabled()
                && !isCooldown(foundCommand)
                && checkUserLevel(foundCommand, message)) {
            String response = foundCommand.getMessage();
            String[] messageSplit = message.getMessage().split(" ");
            if (messageSplit.length > 1) {
                reply = replaceBetweenParentheses(message, response, message.getMessage().split(" ", 2)[1].split(" "), foundCommand);
            } else {
                reply = replaceBetweenParentheses(message, response, new String[0], foundCommand);
            }
            startCooldown(foundCommand);
        }
        if (!reply.equalsIgnoreCase("")) {
            if(message.isYouTube()) Main.sendYTMessage(reply);
            else Main.sendMessage(reply);
        }
    }

    public static String replaceBetweenParentheses(ChatMessage message, String text, String[] arguments, CommandData data) {
        return replaceBetweenParentheses(message, text, arguments, null, text, data, null, null);
    }
    public static String replaceBetweenParentheses(ChatMessage message, String text, String[] arguments, CommandData data, CheerActionData cheerActionData) {
        return replaceBetweenParentheses(message, text, arguments, null, text, data, null, cheerActionData);
    }
    public static String replaceBetweenParentheses(ChatMessage message, String text, String[] arguments, CommandData data, KeywordData keywordData) {
        return replaceBetweenParentheses(message, text, arguments, null, text, data, keywordData, null);
    }

    private static String evaluateIfStatements(String value, ChatMessage message, CommandData commandData, KeywordData keywordData, CheerActionData cheerActionData){


        String data = "";
        String[] dataArr = value.split(" ", 2);
        if (dataArr.length > 1) data = value.split(" ", 2)[1];
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

            System.out.println(ifData);

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
            String newValue = "";

            if(state == 2){
                String ifValue = innerData.substring(startPos+1, endPos);
                String ifValueAfter = parseParenthesis(message, ifValue, ifValue.split(" "), null, ifValue, commandData, keywordData, cheerActionData, false);

                System.out.println(ifValueAfter);

                newValue = ifData;
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

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        try {
            return (boolean) engine.eval(value);
        } catch (ScriptException e) {
            return false;
        }
    }

    private static String runCommandActions(String value, CommandData commandData, KeywordData keywordData, CheerActionData cheerActionData, ChatMessage message, String[] arguments){

        String data = "";
        String[] dataArr = value.split(" ", 2);
        if (dataArr.length > 1) data = value.split(" ", 2)[1];
        String identifier = value.split(" ")[0];
        String replacement = "";

        switch (identifier.toLowerCase()) {
            case "user": {
                replacement = message.getSenderElseDisplay();
                break;
            }
            case "touser":
            case "to_user": {
                if (arguments.length > 0) {
                    replacement = arguments[0].trim();
                } else {
                    replacement = message.getSenderElseDisplay();
                }
                break;
            }
            case "arg": {
                int arg;
                try {
                    arg = Integer.parseInt(data)-1;
                } catch (NumberFormatException e) {
                    replacement = "Error";
                    break;
                }
                if (arguments.length >= arg) {
                    replacement = arguments[arg];
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
            case "instant_request":
            case "instantrequest":{
                String messageNoSymbol = message.getMessage().replace(",", "")
                        .replace(".","")
                        .replace("!", "")
                        .replace("(", "")
                        .replace(")", "")
                        .replace("{", "")
                        .replace("}", "")
                        .replace("[", "")
                        .replace("]", "")
                        .replace("-", "")
                        .replace("'", "");

                Matcher m = Pattern.compile("\\s*(\\d{6,})\\s*").matcher(messageNoSymbol);

                if (m.find()) {
                    try {
                        String[] messages = message.getMessage().split(" ");
                        String mention = "";
                        for (String s : messages) {
                            if (s.contains("@")) {
                                mention = s;
                                break;
                            }
                        }
                        if (!mention.contains(m.group(1))) {
                            if (SettingsHandler.getSettings("gdMode").asBoolean() && Window.getWindow().isVisible()) {
                                long chatIDL = 0;
                                String chatID = message.getTag("user-id");
                                if (chatID != null) {
                                    chatIDL = Long.parseLong(chatID);
                                }
                                Requests.addRequest(Long.parseLong(m.group(1).replaceFirst("^0+(?!$)", "")), message.getSender(), message.isMod(), message.isSub(), message.getMessage(), message.getTag("id"), chatIDL, false, message, 1);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
            }

            case "find_and_request":
            case "findrequest":
            case "request": {
                String messageNoSymbol = message.getMessage().replace(",", "")
                        .replace(".","")
                        .replace("!", "")
                        .replace("(", "")
                        .replace(")", "")
                        .replace("{", "")
                        .replace("}", "")
                        .replace("[", "")
                        .replace("]", "")
                        .replace("-", "")
                        .replace("'", "");

                Matcher m = Pattern.compile("\\s*(\\d{6,})\\s*").matcher(messageNoSymbol);

                if (m.find()) {
                    try {
                        String[] messages = message.getMessage().split(" ");
                        String mention = "";
                        for (String s : messages) {
                            if (s.contains("@")) {
                                mention = s;
                                break;
                            }
                        }
                        if (!mention.contains(m.group(1))) {
                            if (SettingsHandler.getSettings("gdMode").asBoolean() && Window.getWindow().isVisible()) {
                                long chatIDL = 0;
                                String chatID = message.getTag("user-id");
                                if (chatID != null) {
                                    chatIDL = Long.parseLong(chatID);
                                }
                                Requests.addRequest(Long.parseLong(m.group(1).replaceFirst("^0+(?!$)", "")), message.getSender(), message.isMod(), message.isSub(), message.getMessage(), message.getTag("id"), chatIDL, false, message);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
                if(!message.isYouTube()) replacement = message.getTag("user-id");
                break;
            }
            case "userlevel":
            case "user_level": {
                replacement = message.getUserLevel();
                break;
            }
            case "messageid":
            case "message_id": {
                replacement = message.getTag("id");
                break;
            }
            case "bwomp": {
                Board.bwomp();
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
                        content = new Scanner(new URL(path).openStream(), "UTF-8").useDelimiter("\\A").next();
                    }
                    catch (Exception e){
                        return "Invalid URL";
                    }
                }

                String[] lines = content.split("\n");

                return lines[random.nextInt(lines.length)];
            }
            case "playsound":
            case "sound": {
                switch (dataArr[1].trim().toLowerCase()){
                    case "bwomp": {
                        Board.bwomp();
                        break;
                    }
                    case "boowomp": {
                        Sounds.playSound("/sounds/boowomp.mp3", true, true, false, false);
                        break;
                    }
                    case "fart": {
                        Sounds.playSound("/sounds/fart.mp3", true, true, false, false);
                        break;
                    }
                    case "bonk": {
                        Sounds.playSound("/sounds/bonk.mp3", true, true, false, false);
                        break;
                    }
                    case "honk": {
                        Sounds.playSound("/sounds/honk.mp3", true, true, false, false);
                        break;
                    }
                    case "ping": {
                        Sounds.playSound("/sounds/ping.mp3", true, true, false, false);
                        break;
                    }
                    default: {
                        if(data.startsWith("file://")) Sounds.playSound(dataArr[1].trim().substring(7), true, true, true, false);
                        else Sounds.playSound(dataArr[1].trim(), true, true, false, true);
                        break;
                    }
                }
                break;
            }

            case "level": {
                String[] levelArguments = data.split(" ");
                if (levelArguments.length > 2) {
                    replacement = "";
                    break;
                }
                if (levelArguments.length < 2) {
                    replacement = "";
                    break;
                }
                int pos;
                try {
                    pos = Integer.parseInt(levelArguments[0].trim());
                    if (pos < RequestsUtils.getSize()) {
                        replacement = RequestsUtils.getLevel(pos, levelArguments[1].trim().toLowerCase());
                    } else {
                        replacement = "";
                    }
                } catch (NumberFormatException e) {
                    replacement = "Error";
                }
                break;
            }
            case "channel":
            case "broadcaster": {
                if(message.isYouTube()) replacement = YouTubeAccount.name;
                else replacement = TwitchAccount.login;
                break;
            }
            case "channelid":
            case "channel_id": {
                if(message.isYouTube()) replacement = YouTubeAccount.ID;
                else replacement = TwitchAccount.id;
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
                if(commandData != null) {
                    count = commandData.getCounter();
                    for (CommandData data1 : CommandData.getRegisteredCommands()) {
                        if (data1.getCommand().equalsIgnoreCase(commandData.getCommand())) {
                            data1.setCounter(count + addedNumber);
                        }
                    }
                }
                if(keywordData != null) {
                    count = keywordData.getCounter();
                    for (KeywordData data1 : KeywordData.getRegisteredKeywords()) {
                        if (data1.getKeyword().equalsIgnoreCase(keywordData.getKeyword())) {
                            data1.setCounter(count + addedNumber);
                        }
                    }
                }
                replacement = String.valueOf(count+1);
                break;
            }
            case "cheercount" :
            case "cheer_count" : {
                replacement = String.valueOf(cheerActionData.getCheerAmount());
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
                try {
                    replacement = Utilities.fetchURL("https://2g.be/twitch/following.php?user="
                            + data.split(" ")[0].trim()
                            + "&channel="
                            + TwitchAccount.login + "&format=mwdhms" );
                }
                catch (Exception e){
                    replacement = "Error";
                }
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
            case "found_word":
            case "foundword": {
                if(keywordData != null){
                    replacement = keywordData.getFoundWord();
                    break;
                }
            }
            case "random_user":
            case "random_viewer": {
                Random ran = new Random();
                replacement = TwitchAPI.allViewers.get(ran.nextInt(TwitchAPI.allViewers.size()));
                break;
            }
            case "queue_size": {
                replacement = String.valueOf(RequestsTab.getQueueSize());
                break;
            }
            case "levelpercent":
            case "level_percent": {
                if(Global.isGDOpen() && Global.isInLevel()){
                    replacement = String.valueOf(com.alphalaneous.Memory.Level.getPercent());
                }
                replacement = String.valueOf(0);
                break;
            }
            case "emptymessage":
            case "empty_message": {
                String command = message.getMessage().split(" ")[0].trim();
                String query = message.getMessage().substring(command.length()).trim();
                replacement = String.valueOf(query.trim().equals(""));
                break;
            }
            default: {

                replacement = "$(" + value + ")";
                break;
            }
        }
        return replacement;
    }

    private static String replaceBetweenParentheses(ChatMessage message, String value, String[] arguments, CommandData commandData, KeywordData keywordData, CheerActionData cheerActionData) {
        return replaceBetweenParentheses(message, value, arguments, null, value, commandData, keywordData, cheerActionData);
    }

    public static String replaceBetweenParentheses(ChatMessage message, String text, String[] arguments, ArrayList<ParenthesisSubstrings> parenthesisSubstrings, String original, CommandData commandData, KeywordData keywordData, CheerActionData cheerActionData) {
        return replaceBetweenParentheses(message, text, arguments, parenthesisSubstrings, original, commandData, keywordData, cheerActionData, false);
    }

    public static String parseParenthesis(ChatMessage message, String text, String[] arguments, ArrayList<ParenthesisSubstrings> parenthesisSubstrings, String original, CommandData commandData, KeywordData keywordData, CheerActionData cheerActionData, boolean ifPass){
        String newResult = original;


        if (parenthesisSubstrings == null) parenthesisSubstrings = new ArrayList<>();

        int pValue = 0;
        int sIndex = 0;
        int eIndex = 0;

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
            return parseParenthesis(message, result, arguments, parenthesisSubstrings, original, commandData, keywordData, cheerActionData, ifPass);
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
                        replacement = evaluateIfStatements(value, message, commandData, keywordData, cheerActionData);
                    } else {
                        replacement = runCommandActions(value, commandData, keywordData, cheerActionData, message, arguments);
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

    public static String replaceBetweenParentheses(ChatMessage message, String text, String[] arguments, ArrayList<ParenthesisSubstrings> parenthesisSubstrings, String original, CommandData commandData, KeywordData keywordData, CheerActionData cheerActionData, boolean ifPass) {

        String value = parseParenthesis(message, text, arguments, parenthesisSubstrings, original, commandData, keywordData, cheerActionData, true);
        System.out.println("Value: " + value);
        return parseParenthesis(message, value, value.split(" "), parenthesisSubstrings, value, commandData, keywordData, cheerActionData, false);

    }

    private static String getWeather(String data){

        JSONObject weatherBasicObject = new JSONObject(Utilities.fetchURL("http://api.openweathermap.org/data/2.5/weather?q=" + data.trim() + "&appid=f5d9ac78d3e44fa1ada880cef1e68b11"));
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
            return commandData.getCommand().equalsIgnoreCase(data.getCommand());
        }
        return false;
    }
    public static boolean checkUserLevel(CommandData data, ChatMessage message){

        String commandLevel = data.getUserLevel();
        String messageLevel = message.getUserLevel();

        ArrayList<String> userLevels = new ArrayList<>();
        userLevels.add("everyone");
        userLevels.add("subscriber");
        userLevels.add("twitch_vip");
        userLevels.add("moderator");
        userLevels.add("owner");

        ArrayList<String> userLevelsToRemove = new ArrayList<>();
        for(String userLevel : userLevels){
            if(commandLevel.equalsIgnoreCase(userLevel)){

                break;
            }
            userLevelsToRemove.add(userLevel);
        }
        userLevels.removeAll(userLevelsToRemove);
        return userLevels.contains(messageLevel);
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
