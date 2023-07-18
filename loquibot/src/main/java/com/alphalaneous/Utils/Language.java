package com.alphalaneous.Utils;

import com.alphalaneous.Swing.Components.CurvedButton;
import com.alphalaneous.Swing.Components.LangButton;
import com.alphalaneous.Swing.Components.LangLabel;
import com.alphalaneous.FileUtils.FileList;
import com.alphalaneous.FileUtils.GetInternalFiles;
import com.alphalaneous.Tabs.ChatbotTab;
import com.alphalaneous.Tabs.SettingsTab;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static java.nio.file.StandardWatchEventKinds.*;

public class Language {


    private static String lang = "en_us";

    public static String setLocale(String text) {
        if(text != null) {
            String newText = text;
            String[] words = newText.split(" ");
            for (String word : words) {
                if (word.startsWith("$") && word.endsWith("$")) {
                    String newWord = Language.getString(word.replace("$", ""));
                    newText = newText.replace(word, newWord);
                }
                if(word.startsWith("〈") && word.endsWith("〉")) {
                    String newWord = Language.getLangPropString(word.replace("〈", "").replace("〉", ""));
                    newText = newText.replace(word, newWord);
                }
            }
            return newText;
        }
        return "";
    }

    static ConcurrentHashMap<String, String> language = new ConcurrentHashMap <>();
    static ConcurrentHashMap <String, String> langProp = new ConcurrentHashMap <>();

    static String[] uwuEndings = {"uwu", "OwO", "rawr~", "X3", "nuzzles~", "(´・ω・｀)", "\uD83E\uDD7A", "uvu", " （=´∇｀=）"};

    public static String modify(String text){

        if(Defaults.isAprilFools) {
            text = text.replace("queue", "form").replace("Queue", "Form");

            /*Random generator = new Random();
            int randomIndex = generator.nextInt(uwuEndings.length);

            return text.replace('l', 'w')
                    .replace('L', 'W')
                    .replace('r', 'w')
                    .replace('R', 'W')
                    .replace("no", "nyo")
                    .replace("NO", "NYO")
                    .replace("nO", "nYO")
                    .replace("No", "Nyo") + " " + uwuEndings[randomIndex];*/
        }
        return text;
    }

    public static void switchLanguage(String language){
        switch (language){
            case "en_us":
            case "es_es":
            case "fr_fr":
            case "pt_br":
                break;
            default: lang = "en_us";
                return;
        }
        lang = language;
        loadLanguage();
        reloadLocale();
        SettingsTab.refreshSettingsButtons();
        ChatbotTab.refreshSettingsButtons();
    }

    public static String getString(String identifier){
        return language.getOrDefault(identifier, identifier);
    }
    static String getLangPropString(String identifier){
        return langProp.getOrDefault(identifier, identifier);
    }

    public static void loadLanguage() {
        try {
            Path comPath = Paths.get(Defaults.saveDirectory + "/loquibot/Languages/");
            if (Files.exists(comPath)) {
                Stream<Path> walk1 = Files.walk(comPath, 1);
                for (Iterator<Path> it = walk1.iterator(); it.hasNext(); ) {
                    Path path = it.next();
                    String[] file = path.toString().split("\\\\");
                    String fileName = file[file.length - 1];
                    if (fileName.equals(lang + ".lang")) {
                        Scanner sc = new Scanner(path.toFile());
                        while (sc.hasNextLine()) {
                            String line = sc.nextLine();
                            if (line.startsWith("#") || line.trim().equalsIgnoreCase("")) {
                                continue;
                            }
                            language.put(line.split("=", 2)[0].trim(), line.split("=", 2)[1].trim());
                        }
                        sc.close();
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        try {
            GetInternalFiles getInternalFiles = new GetInternalFiles("Languages/");
            FileList files = getInternalFiles.getFiles();

            String fileName = lang + ".lang";
            String langProperties = "Languages.prop";


            String[] fileLines = files.getFile(fileName).getString().split("\n");
            for(String line : fileLines){
                if (line.startsWith("#") || line.trim().equalsIgnoreCase("")) {
                    continue;
                }
                language.put(line.split("=", 2)[0].trim(), line.split("=", 2)[1].trim());
            }

            String[] langPropLines = files.getFile(langProperties).getString().split("\n");

            for(String line : langPropLines){
                if (line.startsWith("#") || line.trim().equalsIgnoreCase("")) {
                    continue;
                }
                langProp.put(line.split("=", 2)[0].trim(), line.split("=", 2)[1].trim());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void startFileChangeListener() {
        new Thread(() -> {
            try {
                WatchService watcher = FileSystems.getDefault().newWatchService();
                Path dir = Paths.get(Defaults.saveDirectory + "/loquibot/Languages");
                if (Files.exists(dir)) {
                    dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

                    while (true) {
                        WatchKey key;
                        try {
                            key = watcher.take();
                        } catch (InterruptedException ex) {
                            return;
                        }

                        for (WatchEvent<?> event : key.pollEvents()) {
                            WatchEvent.Kind<?> kind = event.kind();

                            WatchEvent<Path> ev = (WatchEvent<Path>) event;
                            Path fileName = ev.context();

                            if (kind == ENTRY_MODIFY && fileName.toString().equals(lang + ".lang")) {
                                Language.loadLanguage();
                                reloadLocale();
                            }
                        }

                        boolean valid = key.reset();
                        if (!valid) {
                            break;
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }
    private static void reloadLocale(){
        for (LangButton button : LangButton.buttonList) {
            button.refreshLocale();
        }
        for (LangLabel langLabel : LangLabel.labelList) {
            langLabel.refreshLocale();
        }
        for (CurvedButton button : CurvedButton.buttons) {
            button.refreshLocale();
        }
    }
}