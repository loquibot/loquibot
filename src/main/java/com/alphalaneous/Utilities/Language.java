package com.alphalaneous.Utilities;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.FileUtils.FileList;
import com.alphalaneous.FileUtils.GetInternalFiles;
import com.alphalaneous.Main;

import java.io.IOException;
import java.nio.file.*;
import java.util.Iterator;
import java.util.Scanner;
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
            }
            return newText;
        }
        return "";
    }

    static ConcurrentHashMap<String, String> language = new ConcurrentHashMap <>();

    public static String getString(String identifier){
        return language.getOrDefault(identifier, identifier);
    }

    @OnLoad(order = -1001)
    public static void loadLanguage() {
        try {
            Path comPath = Paths.get(Utilities.saveDirectory + "/Languages/");
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
            Logging.getLogger().error(e.getMessage(), e);
        }
        try {
            GetInternalFiles getInternalFiles = new GetInternalFiles("Languages/");
            FileList files = getInternalFiles.getFiles();

            String fileName = lang + ".lang";


            String[] fileLines = files.getFile(fileName).getString().split("\n");
            for(String line : fileLines){
                if (line.startsWith("#") || line.trim().equalsIgnoreCase("")) {
                    continue;
                }
                language.put(line.split("=", 2)[0].trim(), line.split("=", 2)[1].trim());
            }
        } catch (Exception e) {
            Logging.getLogger().error(e.getMessage(), e);
        }
    }

    @OnLoad(order = -1000)
    public static void startFileChangeListener() {
        new Thread(() -> {
            try {
                WatchService watcher = FileSystems.getDefault().newWatchService();
                Path dir = Paths.get(Utilities.saveDirectory + "/Languages");
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
                            }
                        }

                        boolean valid = key.reset();
                        if (!valid) {
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                Logging.getLogger().error(e.getMessage(), e);

            }
        }).start();
    }
}