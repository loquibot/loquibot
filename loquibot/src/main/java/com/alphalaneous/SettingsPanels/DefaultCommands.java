package com.alphalaneous.SettingsPanels;

import com.alphalaneous.Swing.Components.SettingsPage;

import javax.swing.*;

public class DefaultCommands {

    private static final SettingsPage settingsPage = new SettingsPage("$DEFAULT_COMMAND_SETTINGS$");
    private static final String[] gdCommands = {"!gdping", "!block", "!blockuser", "!unblock", "!unblockuser", "!clear", "!info", "!move", "!next", "!position", "!queue", "!remove", "!request", "!song", "!toggle", "!top", "!wronglevel"};


    public static JPanel createPanel(){

        //refresh();
        return settingsPage;
    }
    /*public static void refresh(){
        try {
            HashMap<String, CommandSettings.ButtonInfo> existingCommands = new HashMap<>();
            try {
                URI uri = Objects.requireNonNull(Main.class.getResource("/Commands/")).toURI();
                Path myPath;
                if (uri.getScheme().equals("jar")) myPath = BotHandler.fileSystem.getPath("/Commands/");
                else myPath = Paths.get(uri);

                Stream<Path> walk = Files.walk(myPath, 1);

                for (Iterator<Path> it = walk.iterator(); it.hasNext(); ) {
                    Path path = it.next();
                    String[] file;
                    if (uri.getScheme().equals("jar")) file = path.toString().split("/");
                    else file = path.toString().split("\\\\");

                    String fileName = file[file.length - 1];
                    if (fileName.endsWith(".js")) {
                        if (!fileName.equalsIgnoreCase("!eval.js") &&
                                !fileName.equalsIgnoreCase("!end.js") &&
                                !fileName.equalsIgnoreCase("b!addcom.js") &&
                                !fileName.equalsIgnoreCase("b!editcom.js") &&
                                !fileName.equalsIgnoreCase("b!delcom.js") &&
                                !fileName.equalsIgnoreCase("b!addpoint.js") &&
                                !fileName.equalsIgnoreCase("b!editpoint.js") &&
                                !fileName.equalsIgnoreCase("b!delpoint.js")) {
                            String substring = fileName.substring(0, fileName.length() - 3);
                            if (!existingCommands.containsKey(substring)) {
                                existingCommands.put(substring, new CommandSettings.ButtonInfo(path, true));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            TreeMap<String, CommandSettings.ButtonInfo> sorted = new TreeMap<>(existingCommands);

            for (Map.Entry<String, CommandSettings.ButtonInfo> entry : sorted.entrySet()) {
                boolean exists = false;
                String key = entry.getKey();
                if (!Settings.getSettings("gdMode").asBoolean()) {
                    for (String command : gdCommands) {
                        if (key.equalsIgnoreCase(command)) {
                            exists = true;
                            break;
                        }
                    }
                }
                if (!exists) {
                    settingsPage.addConfigCheckbox(key, "Default Command", key+"Enabled");
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }*/
}
