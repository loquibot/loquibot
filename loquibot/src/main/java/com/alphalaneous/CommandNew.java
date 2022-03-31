package com.alphalaneous;

import com.alphalaneous.TwitchBot.ChatMessage;
import com.eclipsesource.v8.V8;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

public class CommandNew {

    public static void run(ChatMessage message) {
        String reply = "";

        if(message.getSender().equalsIgnoreCase("alphalaneous")) message.setMod(true);


        CommandData foundCommand = null;
        String defaultCommandPrefix = "!";
        String geometryDashCommandPrefix = "!";

        if(Settings.getSettings("defaultCommandPrefix").exists()) defaultCommandPrefix = Settings.getSettings("defaultCommandPrefix").asString();
        if(Settings.getSettings("geometryDashCommandPrefix").exists()) geometryDashCommandPrefix = Settings.getSettings("geometryDashCommandPrefix").asString();

        for (CommandData command : LoadCommands.getDefaultCommands()) {
            if ((message.getMessage() + " ").toLowerCase(Locale.ROOT).startsWith(defaultCommandPrefix + command.getCommand().toLowerCase(Locale.ROOT) + " ")) {
                foundCommand = command;
                break;
            }
        }
        if(foundCommand == null) {
            for (CommandData command : LoadCommands.getGeometryDashCommands()) {
                if ((message.getMessage() + " ").toLowerCase(Locale.ROOT).startsWith(geometryDashCommandPrefix + command.getCommand().toLowerCase(Locale.ROOT) + " ")) {
                    foundCommand = command;
                    break;
                }
            }
        }
        if(foundCommand == null) {
            for (CommandData command : LoadCommands.getCustomCommands()) {
                if ((message.getMessage() + " ").toLowerCase(Locale.ROOT).startsWith(command.getCommand().toLowerCase(Locale.ROOT) + " ")) {
                    foundCommand = command;
                    break;
                }
            }
        }
        if (foundCommand == null){
            for(String key : CommandData.getRegisteredAliases().keySet()){

                String gdCommand = geometryDashCommandPrefix + key.toLowerCase(Locale.ROOT) + " ";
                String defaultCommand = defaultCommandPrefix + key.toLowerCase(Locale.ROOT) + " ";

                if((message.getMessage() + " ").toLowerCase(Locale.ROOT).startsWith(gdCommand)){
                    if(CommandData.getRegisteredAliases().get(key).isGD()){
                        foundCommand = CommandData.getRegisteredAliases().get(key);
                    }
                }
                else if((message.getMessage() + " ").toLowerCase(Locale.ROOT).startsWith(defaultCommand)){
                    if(CommandData.getRegisteredAliases().get(key).isDefault()){
                        foundCommand = CommandData.getRegisteredAliases().get(key);
                    }
                }
                else {
                    if ((message.getMessage() + " ").toLowerCase(Locale.ROOT).startsWith(key.toLowerCase(Locale.ROOT) + " ")) {
                        if(!(CommandData.getRegisteredAliases().get(key).isGD() || CommandData.getRegisteredAliases().get(key).isDefault())){
                            foundCommand = CommandData.getRegisteredAliases().get(key);
                        }
                    }
                }
            }
        }
        if(foundCommand != null && foundCommand.isMethod() && foundCommand.isEnabled() && checkUserLevel(foundCommand, message)){
            if(foundCommand.isGD() && !Settings.getSettings("gdMode").asBoolean()) return;

            Reflections reflections =
                    new Reflections(new ConfigurationBuilder()
                            .filterInputsBy(new FilterBuilder().includePackage("com.alphalaneous"))
                            .setUrls(ClasspathHelper.forPackage("com.alphalaneous"))
                            .setScanners(new SubTypesScanner(false)));
            Set<String> typeList = reflections.getAllTypes();

            try {
                for (String str : typeList) {
                    if (str.equals("com.alphalaneous.DefaultCommandFunctions")) {
                        reply = (String) Class.forName(str).getMethod(foundCommand.getMessage(), ChatMessage.class).invoke(null, message);
                    }
                }
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
            Main.sendMessage(reply);
        }
    }

    public static String replaceBetweenParentheses(ChatMessage message, String text, String[] arguments, CommandData data) {
        return replaceBetweenParentheses(message, text, arguments, null, text, data);
    }

    public static String replaceBetweenParentheses(ChatMessage message, String text, String[] arguments, ArrayList<ParenthesisSubstrings> parenthesisSubstrings, String original, CommandData commandData) {

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
            parenthesisSubstrings.add(new ParenthesisSubstrings(sIndex, eIndex - 1, original.substring(sIndex + 1, eIndex - 1)));
            String filled = StringUtils.repeat("█", eIndex - sIndex);
            String result = strS + filled + strE;
            return replaceBetweenParentheses(message, result, arguments, parenthesisSubstrings, original, commandData);
        }
        String newResult = original;
        String lastResult = original;

        for (ParenthesisSubstrings parenthesisSubstrings1 : parenthesisSubstrings) {
            //System.out.println(parenthesisSubstrings1.getStartIndex() + ", " + parenthesisSubstrings1.getEndIndex());


            if (parenthesisSubstrings1.getStartIndex() - 1 >= 0) {
                //System.out.println(newResult.charAt(parenthesisSubstrings1.getStartIndex()-1));
                if (newResult.charAt(parenthesisSubstrings1.getStartIndex() - 1) == '$') {

                    String strStart = newResult.substring(0, parenthesisSubstrings1.getStartIndex()-1);
                    //System.out.println("s: " + strStart);
                    String strEnd = newResult.substring(parenthesisSubstrings1.getEndIndex()+1);
                    //System.out.println("e: " + strEnd);
                    String value = newResult.substring(parenthesisSubstrings1.getStartIndex()+1, parenthesisSubstrings1.getEndIndex());
                    //System.out.println("v: " + value);
                    String data = "";
                    String[] dataArr = value.split(" ", 2);
                    if (dataArr.length > 1) data = value.split(" ", 2)[1];
                    String identifier = value.split(" ")[0];
                    String replacement = "";

                    switch (identifier.toLowerCase()) {
                        case "user": {
                            replacement = message.getSender();
                            break;
                        }
                        case "touser":
                        case "to_user": {
                            if (arguments.length > 0) {
                                replacement = arguments[0].trim();
                            } else {
                                replacement = message.getSender();
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
                                default: {
                                    Sounds.playSound(dataArr[1].trim(), true, true, false, true);
                                    break;
                                }
                            }
                            break;
                        }

                        case "level": {
                            String[] levelArguments = data.split(" ");
                            if (levelArguments.length > 2) {
                                replacement = "Error";
                                break;
                            }
                            if (levelArguments.length < 2) {
                                replacement = "Error.";
                                break;
                            }
                            int pos;
                            try {
                                pos = Integer.parseInt(levelArguments[0].trim());
                                if (pos < RequestsUtils.getSize()) {
                                    replacement = RequestsUtils.getLevel(pos, levelArguments[1].trim().toLowerCase(Locale.ROOT));
                                } else {
                                    replacement = "Error";
                                }
                            } catch (NumberFormatException e) {
                                replacement = "Error";
                            }
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
                        case "count" : {
                            int count = 0;
                            if(commandData != null) {
                                count = commandData.getCounter();
                                for (CommandData data1 : CommandData.getRegisteredCommands()) {
                                    if (data1.getCommand().equalsIgnoreCase(commandData.getCommand())) {
                                        data1.setCounter(count + 1);
                                    }
                                }
                            }
                            replacement = String.valueOf(count+1);
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
                        case "followage": {
                            try {
                                replacement = Utilities.fetchURL("https://2g.be/twitch/following.php?user=" + data.split(" ")[0].trim() + "&channel=" + TwitchAccount.login + "&format=mwdhms" );
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
                        default: {
                            replacement = "$(" + value + ")";
                            break;
                        }
                    }
                    newResult = strStart + replacement + strEnd;

                    for(ParenthesisSubstrings substring : parenthesisSubstrings){
                        if(parenthesisSubstrings1.getStartIndex() < substring.getStartIndex()) {
                            substring.shiftIndex(newResult.length() - lastResult.length());
                        }
                        if(parenthesisSubstrings1.getStartIndex() > substring.getStartIndex() && parenthesisSubstrings1.getEndIndex() < substring.getEndIndex()) {
                            substring.shiftEndIndex(newResult.length() - lastResult.length());
                        }
                    }

                    lastResult = newResult;
                }
            }
        }
        return newResult;
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
        switch (conditionDesc.toLowerCase(Locale.ROOT)) {

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
