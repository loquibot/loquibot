package com.alphalaneous;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Utilities.Utilities;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Assets {

    private static final HashMap<String, ImageIcon> images = new HashMap<>();

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    @OnLoad
    public static void init(){

        loadAsset("loquibot-large-icon", "Images/loqui.png", 512, Scalr.Method.QUALITY);
        loadAsset("loquibot-medium-icon", "Images/loqui.png", 32, Scalr.Method.QUALITY);
        loadAsset("loquibot-small-icon", "Images/loqui.png", 16, Scalr.Method.QUALITY);
        loadAsset("loquibot-icon", "Images/loqui.png", 200, Scalr.Method.QUALITY);
        loadAsset("twitch-logo", "Images/Twitch.png", 40, Scalr.Method.BALANCED);
        loadAsset("run-button", "Images/run.png", 30, Scalr.Method.QUALITY);
        loadAsset("chat-button", "Images/chat.png", 30, Scalr.Method.QUALITY);
        loadAsset("settings-button", "Images/settings.png", 30, Scalr.Method.QUALITY);

        executor.shutdown();
        while (!executor.isTerminated()) Utilities.sleep(1);
    }

    public static ImageIcon getImage(String name){
        return images.get(name);
    }

    public static void loadAsset(String name, String path, int scale, Scalr.Method scalingMethod){

        executor.execute(new Thread(() -> {
            try {
                if(scalingMethod == null){
                    images.put(name, new ImageIcon(Scalr.resize(ImageIO
                            .read(Objects.requireNonNull(Main.class.getClassLoader()
                                    .getResource(path))), scale, Scalr.OP_ANTIALIAS)));
                }
                else{
                    images.put(name, new ImageIcon(Scalr.resize(ImageIO
                            .read(Objects.requireNonNull(Main.class.getClassLoader()
                                    .getResource(path))), scalingMethod, scale, Scalr.OP_ANTIALIAS)));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }
}
