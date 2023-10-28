package com.alphalaneous.Utilities;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Main;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.io.IoBuilder;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Logging {

    public static String logFile;

    @OnLoad
    public static void init(){

        Date now = new Date();
        SimpleDateFormat format =
                new SimpleDateFormat ("yyyy.MM.dd-HH.mm.ss.SSSS");
        Path path = Paths.get(Utilities.saveDirectory + "logs/");

        try {
            if(!Files.isDirectory(path)){
                Files.createDirectory(path);
            }
        } catch (IOException e) {
            Logging.getLogger().info("Failed to create log file directories");
        }

        String formatted = format.format(now);

        logFile = path + "/" + formatted;

        ThreadContext.put("filePath", logFile);

        System.setErr(IoBuilder.forLogger(LogManager.getRootLogger()).setLevel(org.apache.logging.log4j.Level.ERROR).buildPrintStream());
    }


    public static Logger getLogger(){
        try {
            return LogManager.getLogger(Class.forName(Thread.currentThread().getStackTrace()[2].getClassName()));
        }
        catch (Exception e){
            e.printStackTrace();
            return LogManager.getLogger(Main.class);
        }
    }
}
