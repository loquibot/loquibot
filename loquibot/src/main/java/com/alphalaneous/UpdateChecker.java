package com.alphalaneous;

import com.alphalaneous.Windows.Window;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UpdateChecker {

    public static void checkForUpdates(){
        new Thread(() -> {
            while(true){

                String version = Utilities.fetchURL("https://raw.githubusercontent.com/Alphatism/GDBoard/Master/GD%20Request%20Bot/src/.version").trim();
                double versionNumber = Double.parseDouble(version.split("=")[1]);


                try {
                    String curVersion = Files.readString(Paths.get(Defaults.saveDirectory + "/GDBoard/version.txt"));
                    double curVersionNumber = Double.parseDouble(curVersion.split("=")[1]);

                    if(curVersionNumber < versionNumber){
                        Window.showUpdateButton();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


                Utilities.sleep(300000);
            }
        }).start();

    }
}
