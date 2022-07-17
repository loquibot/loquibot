package com.alphalaneous.ChatBot;

import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Services.GeometryDash.Requests;
import com.alphalaneous.Interactive.Commands.Command;
import com.alphalaneous.Interactive.Commands.CommandData;
import com.alphalaneous.Interactive.Commands.LoadCommands;
import com.alphalaneous.Main;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Windows.Window;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class BotHandler {
    static boolean processing = false;
    static URI uri;
    private static final ArrayList<String> comCooldown = new ArrayList<>();

    //todo fix !sudo command

    static {
        try {
            uri = Objects.requireNonNull(Main.class.getResource("/Commands/")).toURI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onMessage(String user, String message, boolean isMod, boolean isSub, int cheer, String ID, long userID, ChatMessage chatMessage) {
        boolean whisper = false;
        processing = true;
        boolean goThrough = true;
        String com = message.split(" ")[0];
        String[] arguments = message.split(" ");
        String response = "";
        String messageNoComma = message.replace(",", "").replace(".","");
        Matcher m = Pattern.compile("\\s*(\\d{6,})\\s*").matcher(messageNoComma);

        if (!(m.find() && !message.startsWith("!"))) {
            try {
                String defaultCommandPrefix = "!";
                String geometryDashCommandPrefix = "!";
                String mediaShareCommandPrefix = "!";

                if(SettingsHandler.getSettings("defaultCommandPrefix").exists()) defaultCommandPrefix = SettingsHandler.getSettings("defaultCommandPrefix").asString();
                if(SettingsHandler.getSettings("geometryDashCommandPrefix").exists()) geometryDashCommandPrefix = SettingsHandler.getSettings("geometryDashCommandPrefix").asString();
                if(SettingsHandler.getSettings("mediaShareCommandPrefix").exists()) mediaShareCommandPrefix = SettingsHandler.getSettings("mediaShareCommandPrefix").asString();

                for(CommandData data : LoadCommands.getDefaultCommands()){
                    if((defaultCommandPrefix + data.getCommand()).equalsIgnoreCase(com) ) return;
                }
                for(CommandData data : LoadCommands.getGeometryDashCommands()){
                    if((geometryDashCommandPrefix + data.getCommand()).equalsIgnoreCase(com) ) return;
                }
                for(CommandData data : LoadCommands.getMediaShareCommands()){
                    if((mediaShareCommandPrefix + data.getCommand()).equalsIgnoreCase(com) ) return;
                }

                if (com.equalsIgnoreCase("!sudo") && (isMod || user.equalsIgnoreCase("Alphalaneous"))) {
                    user = arguments[1].toLowerCase();
                    com = arguments[2];
                    arguments = Arrays.copyOfRange(arguments, 2, arguments.length);
                    isMod = true;
                    isSub = true;

                }
                if (com.equalsIgnoreCase("b!bypass") && user.equalsIgnoreCase("Alphalaneous")) {
                    com = arguments[1];
                    arguments = Arrays.copyOfRange(arguments, 1, arguments.length);
                    isMod = true;
                    isSub = true;
                }

                if (Files.exists(Paths.get(Defaults.saveDirectory + "/loquibot/commands/aliases.txt"))) {
                    Scanner sc2 = new Scanner(Paths.get(Defaults.saveDirectory + "/loquibot/commands/aliases.txt").toFile());
                    while (sc2.hasNextLine()) {
                        String line = sc2.nextLine();
                        if (line.split("=")[0].replace(" ", "").equalsIgnoreCase(com)) {
                            com = line.split("=")[1].replace(" ", "");
                            break;
                        }
                    }
                    sc2.close();
                }

                if (Files.exists(Paths.get(Defaults.saveDirectory + "/loquibot/disable.txt"))) {
                    Scanner sc2 = new Scanner(Paths.get(Defaults.saveDirectory + "/loquibot/disable.txt").toFile());
                    while (sc2.hasNextLine()) {
                        String line = sc2.nextLine();
                        if (!line.equalsIgnoreCase("!eval")) {
                            if (line.equalsIgnoreCase(com)) {
                                goThrough = false;
                                break;
                            }
                        }
                    }
                    sc2.close();
                }
                if (Files.exists(Paths.get(Defaults.saveDirectory + "/loquibot/mod.txt"))) {
                    Scanner sc2 = new Scanner(Paths.get(Defaults.saveDirectory + "/loquibot/mod.txt").toFile());
                    while (sc2.hasNextLine()) {
                        String line = sc2.nextLine();
                        if (line.equalsIgnoreCase(com) && !isMod) {
                            goThrough = false;
                            break;
                        }
                    }
                    sc2.close();
                }
                if (goThrough) {
                    if (Files.exists(Paths.get(Defaults.saveDirectory + "/loquibot/whisper.txt"))) {
                        Scanner sc2 = new Scanner(Paths.get(Defaults.saveDirectory + "/loquibot/whisper.txt").toFile());
                        while (sc2.hasNextLine()) {
                            String line = sc2.nextLine();
                            if (line.equalsIgnoreCase(com)) {
                                whisper = true;
                                break;
                            }
                        }
                        sc2.close();
                    }


                    if (comCooldown.contains(com)) {
                        processing = false;
                        return;
                    }
                    int cooldown = 0;
                    if (Files.exists(Paths.get(Defaults.saveDirectory + "/loquibot/cooldown.txt"))) {
                        Scanner sc3 = new Scanner(Paths.get(Defaults.saveDirectory + "/loquibot/cooldown.txt").toFile());
                        while (sc3.hasNextLine()) {
                            String line = sc3.nextLine();
                            if (line.split("=")[0].replace(" ", "").equalsIgnoreCase(com)) {
                                cooldown = Integer.parseInt(line.split("=")[1].replace(" ", ""));
                                break;
                            }
                        }
                        sc3.close();
                    }
                    if (cooldown > 0) {
                        String finalCom = com;
                        int finalCooldown = cooldown * 100;
                        Thread thread = new Thread(() -> {
                            comCooldown.add(finalCom);
                            try {
                                Thread.sleep(finalCooldown);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            comCooldown.remove(finalCom);
                        });
                        thread.start();
                    }

                    Path comPath = Paths.get(Defaults.saveDirectory + "/loquibot/commands/");
                    if (Files.exists(comPath)) {
                        Stream<Path> walk1 = Files.walk(comPath, 1);
                        for (Iterator<Path> it = walk1.iterator(); it.hasNext(); ) {
                            Path path = it.next();
                            String[] file = path.toString().split("\\\\");
                            String fileName = file[file.length - 1];
                            if (fileName.equalsIgnoreCase(com + ".js")) {
                                response = Command.run(user, isMod, isSub, arguments, Files.readString(path, StandardCharsets.UTF_8), cheer, ID, userID);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                String[] messages = message.split(" ");
                String mention = "";
                for (String s : messages) {
                    if (s.contains("@")) {
                        mention = s;
                        break;
                    }
                }
                if (!mention.contains(m.group(1))) {
                    if (SettingsHandler.getSettings("gdMode").asBoolean() && Window.getWindow().isVisible()){
                        Requests.addRequest(Long.parseLong(m.group(1).replaceFirst("^0+(?!$)", "")), user, isMod, isSub, message, ID, userID, false, chatMessage);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (response != null && !response.equalsIgnoreCase("")) {
            if(chatMessage.isYouTube()) Main.sendYTMessage(response);
            else Main.sendMessage(response, whisper, user);
        }
        processing = false;
    }
}
