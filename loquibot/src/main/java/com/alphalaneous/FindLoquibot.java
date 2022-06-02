package com.alphalaneous;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FindLoquibot {

    public static void setup() {
        Path loquibotBin = Paths.get(Defaults.saveDirectory + "/loquibot/bin/");
        Path loquibot = Paths.get(Defaults.saveDirectory + "/loquibot/");
        if (!Files.isDirectory(loquibot)) {
            try {
                Files.createDirectory(loquibot);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!Files.isDirectory(loquibotBin)) {
            try {
                Files.createDirectory(loquibotBin);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!Files.exists(Paths.get(Defaults.saveDirectory + "/loquibot/bin/getPrograms.bat"))) {
            Utilities.copyFromJar(Main.class.getResourceAsStream("/getPrograms.bat"), Defaults.saveDirectory + "/loquibot/bin/getPrograms.bat");
        }
    }

    public static void findPath() {
        if(!Settings.getSettings("installPath").exists()) {
            String exe = "loquibot.exe";
            try {
                Process p = Runtime.getRuntime().exec(Defaults.saveDirectory + "/loquibot/bin/getPrograms.bat " + exe);
                BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while (true) {
                    line = input.readLine();
                    if (line == null) {
                        break;
                    }
                    Path path1 = Paths.get(line);
                    if (path1.getParent().endsWith("loquibot")) {
                        if (path1.getParent().getParent().endsWith("Alphalaneous")) {
                            Settings.writeSettings("installPath", line);
                            break;
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
