package com.alphalaneous;

import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.Utilities;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class PluginHandler {

    public static void loadPlugins() {
        try {
            Path plugins = Paths.get(Utilities.saveDirectory + "Plugins/").toAbsolutePath();
            if(!Files.isDirectory(plugins)) Files.createDirectory(plugins);

            List<Path> files = Files.list(plugins).collect(Collectors.toList());

            for (Path path : files) {
                if(path.toString().toLowerCase().endsWith(".jar")) {
                    loadClasses(path.toString());
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            Logging.getLogger().info("Failed to load plugins");
        }

    }

    public static ArrayList<Class> classes = new ArrayList<>();

    public static void loadClasses(String pathToJar) throws IOException, ClassNotFoundException {

        URL u = new URL("file:/" + pathToJar);

        ClassLoader loader = URLClassLoader.newInstance(new URL[]{u}, Main.class.getClassLoader());

        try (JarFile jarFile = new JarFile(pathToJar)) {
            Enumeration<JarEntry> e = jarFile.entries();


            while (e.hasMoreElements()) {
                JarEntry je = e.nextElement();
                if (je.isDirectory() || !je.getName().endsWith(".class")) {
                    continue;
                }
                String className = je.getName().substring(0, je.getName().length() - 6);
                className = className.replace('/', '.');

                classes.add(Class.forName(className, true, loader));
            }
        }
    }
}
