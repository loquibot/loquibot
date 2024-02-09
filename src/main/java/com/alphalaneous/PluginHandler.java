package com.alphalaneous;

import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.Utilities;

import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class PluginHandler {

    public static void loadPlugins() {
        try {
            Path plugins = Paths.get(Utilities.saveDirectory + "/Plugins/").toAbsolutePath();
            if(!Files.isDirectory(plugins)) Files.createDirectory(plugins);

            List<Path> files = Files.list(plugins).collect(Collectors.toList());

            for (Path path : files) {
                if(path.toString().toLowerCase().endsWith(".jar")) {
                    loadClasses(path.toString());
                }
            }
        }
        catch (Exception e){
            Logging.getLogger().error(e.getMessage(), e);
        }

    }

    public static String generateErrorMessage(Throwable e, String pluginPath){

        Date now = new Date();
        SimpleDateFormat format =
                new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss+SSSS");

        String formatted = format.format(now);


        StringBuilder builder = new StringBuilder();

        builder.append(formatted);
        builder.append("\n");
        builder.append("There was an issue loading a plugin.");
        builder.append("\n");
        builder.append("\n");
        builder.append("== Information ==");
        builder.append("\n");
        builder.append("Loquibot version: v");
        builder.append("TODO VERSION");
        builder.append("\n");
        builder.append("Plugin: ");
        builder.append(pluginPath);
        builder.append("\n");
        builder.append("\n");
        builder.append("== Exception Information ==");
        builder.append("\n");
        builder.append("Exception: ");
        builder.append(e.toString());
        builder.append("\n");
        builder.append("\n");
        builder.append("== Stack Trace ==");
        builder.append("\n");

        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String exceptionAsString = sw.toString();

        builder.append(exceptionAsString);

        return builder.toString();
    }


    public static HashMap<Class, String> classes = new HashMap<>();

    public static void loadClasses(String pathToJar) throws IOException {

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

                try {
                    classes.put(Class.forName(className, true, loader), pathToJar);
                }
                catch (Error | Exception er){
                    Logging.getLogger().error(er.getMessage(), er);
                    JOptionPane.showMessageDialog(null, generateErrorMessage(er, pathToJar), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
