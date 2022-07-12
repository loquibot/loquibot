package com.alphalaneous.SettingsPanels;

import com.alphalaneous.*;
import com.alphalaneous.Swing.Components.CurvedButton;
import com.alphalaneous.Swing.Components.ListButton;
import com.alphalaneous.Swing.Components.ListView;
import com.alphalaneous.FileUtils.FileList;
import com.alphalaneous.FileUtils.GetInternalFiles;
import com.alphalaneous.FileUtils.InternalFile;
import com.alphalaneous.Windows.CommandEditor;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Commands {

    private static final ListView listView = new ListView("$COMMANDS_LIST$");
    private static final String[] gdCommands = {"!gdping", "!block", "!blockuser", "!unblock", "!unblockuser", "!clear", "!info", "!move", "!next", "!position", "!queue", "!remove", "!request", "!song", "!toggle", "!top", "!wronglevel"};

    public static JPanel createPanel(){

        listView.addButton("\uF0D1", () ->
            CommandEditor.showEditor("commands", "", false)
        );
        //refresh();
        return listView;
    }

    public static void clear(){
        listView.clearElements();
    }

    public static void refresh(){
        try {
            listView.clearElements();
            HashMap<String, ButtonInfo> existingCommands = new HashMap<>();
            try {
                Path comPath = Paths.get(Defaults.saveDirectory + "/loquibot/commands/");
                if (Files.exists(comPath)) {
                    Stream<Path> walk1 = Files.walk(comPath, 1);
                    for (Iterator<Path> it = walk1.iterator(); it.hasNext(); ) {
                        Path path = it.next();
                        String[] file = path.toString().split("\\\\");
                        String fileName = file[file.length - 1];
                        if (fileName.endsWith(".js")) {
                            existingCommands.put(fileName.substring(0, fileName.length() - 3), new ButtonInfo(path, false));
                        }
                    }
                }
                GetInternalFiles getInternalFiles = new GetInternalFiles("Commands/");
                FileList files = getInternalFiles.getFiles();
                for(InternalFile file : files){
                    if (file.getName().endsWith(".js")) {
                        if (!file.getName().equalsIgnoreCase("!rick.js") &&
                                !file.getName().equalsIgnoreCase("!stoprick.js") &&
                                !file.getName().equalsIgnoreCase("!eval.js") &&
                                !file.getName().equalsIgnoreCase("!end.js") &&
                                !file.getName().equalsIgnoreCase("!popup.js") &&
                                !file.getName().equalsIgnoreCase("b!addcom.js") &&
                                !file.getName().equalsIgnoreCase("b!editcom.js") &&
                                !file.getName().equalsIgnoreCase("b!delcom.js") &&
                                !file.getName().equalsIgnoreCase("b!addpoint.js") &&
                                !file.getName().equalsIgnoreCase("b!editpoint.js") &&
                                !file.getName().equalsIgnoreCase("b!delpoint.js")) {
                            String substring = file.getName().substring(0, file.getName().length() - 3);
                            if (!existingCommands.containsKey(substring)) {
                                existingCommands.put(substring, new ButtonInfo(Path.of(file.getName()), true));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            TreeMap<String, ButtonInfo> sorted = new TreeMap<>(existingCommands);

            for (Map.Entry<String, ButtonInfo> entry : sorted.entrySet()) {
                boolean exists = false;
                String key = entry.getKey();
                ButtonInfo value = entry.getValue();
                if (!Settings.getSettings("gdMode").asBoolean()) {
                    for (String command : gdCommands) {
                        if (key.equalsIgnoreCase(command)) {
                            exists = true;
                            break;
                        }
                    }
                }
                if (!exists) {
                    listView.addElement(createButton(key, value.isDefault));
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public static CurvedButton createButton(String text, boolean isDefault){
        ListButton button = new ListButton(text, 164);
        button.addActionListener(e -> new Thread(() -> CommandEditor.showEditor("commands", text, isDefault)).start());
        return button;
    }
    public static class ButtonInfo {

        public Path path;
        boolean isDefault;

        ButtonInfo(Path path, boolean isDefault) {
            this.path = path;
            this.isDefault = isDefault;
        }

    }
}
