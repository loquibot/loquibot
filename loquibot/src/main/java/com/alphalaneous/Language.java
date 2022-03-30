package com.alphalaneous;

import com.alphalaneous.Components.CurvedButtonAlt;
import com.alphalaneous.Components.LangButton;
import com.alphalaneous.Components.LangLabel;
import com.alphalaneous.Components.RoundedJButton;
import com.alphalaneous.FileUtils.FileList;
import com.alphalaneous.FileUtils.GetInternalFiles;

import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

import static java.nio.file.StandardWatchEventKinds.*;

public class Language {

    private static final String lang = "en_us";
    private static Path myPath;

    static {
        if (!BotHandler.uri.getScheme().equals("jar")) {
            try {
                URI uri = Objects.requireNonNull(Main.class.getResource("/Languages/")).toURI();
                myPath = Paths.get(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

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

    static HashMap<String, String> language = new HashMap<>();

    static String[] uwuEndings = {"uwu", "OwO", "rawr~", "X3", "nuzzles~", "(´・ω・｀)", "\uD83E\uDD7A", "uvu", " （=´∇｀=）"};

    public static String uwuify(String text){

        String today = new SimpleDateFormat("MMdd").format(Calendar.getInstance().getTime());
        if(today.equalsIgnoreCase("0401")){
            Random generator = new Random();
            int randomIndex = generator.nextInt(uwuEndings.length);

            return text.replace('l', 'w')
                    .replace('L', 'W')
                    .replace('r', 'w')
                    .replace('R', 'W')
                    .replace("no", "nyo")
                    .replace("NO", "NYO")
                    .replace("nO", "nYO")
                    .replace("No", "Nyo") + " " + uwuEndings[randomIndex];
        }
        return text;
    }

    static String getString(String identifier){
        return language.getOrDefault(identifier, identifier);
    }

    static void loadLanguage() {
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
                            if (line.startsWith("//") || line.trim().equalsIgnoreCase("")) {
                                continue;
                            }
                            language.put(line.split("=", 2)[0].trim(), line.split("=", 2)[1].trim());
                        }
                        sc.close();
                    }
                }
            }

            GetInternalFiles getInternalFiles = new GetInternalFiles("Languages/");
            FileList files = getInternalFiles.getFiles();
            String fileName = lang + ".lang";

            String[] fileLines = files.getFile(fileName).getString().split("\n");
            for(String line : fileLines){
                if (line.startsWith("//") || line.trim().equalsIgnoreCase("")) {
                    continue;
                }
                language.put(line.split("=", 2)[0].trim(), line.split("=", 2)[1].trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    static void startFileChangeListener() {
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
                                for (int i = 0; i < LangButton.buttonList.size(); i++) {
                                    LangButton.buttonList.get(i).refreshLocale();
                                }
                                for (int i = 0; i < LangLabel.labelList.size(); i++) {
                                    LangLabel.labelList.get(i).refreshLocale();
                                }
                                for (int i = 0; i < RoundedJButton.buttons.size(); i++) {
                                    RoundedJButton.buttons.get(i).refreshLocale();
                                }
                                for (int i = 0; i < CurvedButtonAlt.buttonList.size(); i++) {
                                    CurvedButtonAlt.buttonList.get(i).refreshLocale();
                                }
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
}