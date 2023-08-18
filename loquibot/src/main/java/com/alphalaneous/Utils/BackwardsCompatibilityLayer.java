package com.alphalaneous.Utils;

import com.alphalaneous.Main;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BackwardsCompatibilityLayer {

    public static void setNewLocation(){
        Path originalPath = Paths.get(System.getenv("APPDATA") + "/GDBoard");
        Path newPath = Paths.get(System.getenv("APPDATA") + "/loquibot");

        if(Files.exists(Paths.get(System.getenv("APPDATA") + "/GDBoard/config.properties"))){
            if(Files.exists(Paths.get(System.getenv("APPDATA") + "/loquibot/.newPath"))) return;
            try {
                copyDirectory(originalPath, newPath);
                Files.createFile(Paths.get(System.getenv("APPDATA") + "/loquibot/.newPath"));
                Files.createFile(Paths.get(System.getenv("APPDATA") + "/GDBoard/.DONT_DELETE_PATH_WILL_BREAK"));

                Files.setAttribute(Paths.get(System.getenv("APPDATA") + "/loquibot/.newPath"), "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
            } catch (IOException e) {
                Main.logger.error(e.getLocalizedMessage(), e);

                JOptionPane.showMessageDialog(null, "Could not move to new directory", "Error", JOptionPane.ERROR_MESSAGE);
                Main.close();
            }
        }
    }
    private static void copyDirectory(Path src, Path dest) throws IOException {
        Files.walk(src).forEach(s -> {
            try {
                Path d = dest.resolve(src.relativize(s));
                if(s.toString().contains("jre") || s.toString().contains("bin")) return;

                if(Files.isDirectory(s)) {
                    if(!Files.exists(d)) Files.createDirectory(d);
                    return;
                }
                Files.copy(s, d);
            } catch(Exception e) {
                Main.logger.error(e.getLocalizedMessage(), e);

            }
        });
    }
}
