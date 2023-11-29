package com.alphalaneous.Utilities;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Main;
import com.alphalaneous.Utilities.Utilities;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Assets {

    private static final HashMap<String, ImageIcon> images = new HashMap<>();

    private static final HashMap<String, BufferedImage> cachedDownloads = new HashMap<>();

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    @OnLoad
    public static void init(){

        loadAsset("loquibot-large-icon", "Images/loqui.png", 512, Scalr.Method.QUALITY);
        loadAsset("loquibot-splash-icon", "Images/loqui.png", 200, Scalr.Method.QUALITY);
        loadAsset("loquibot-account-icon", "Images/loqui.png", 60, Scalr.Method.QUALITY);
        loadAsset("loquibot-medium-icon", "Images/loqui.png", 32, Scalr.Method.QUALITY);
        loadAsset("loquibot-small-icon", "Images/loqui.png", 16, Scalr.Method.QUALITY);
        loadAsset("loquibot-icon", "Images/loqui.png", 200, Scalr.Method.QUALITY);
        loadAsset("twitch-logo", "Images/Twitch.png", 40, Scalr.Method.BALANCED);
        loadAsset("run-button", "Images/run.png", 30, Scalr.Method.QUALITY);
        loadAsset("chat-button", "Images/chat.png", 30, Scalr.Method.QUALITY);
        loadAsset("stream-interaction-button", "Images/trigger.png", 30, Scalr.Method.QUALITY);
        loadAsset("plugins-button", "Images/plugin.png", 30, Scalr.Method.QUALITY);
        loadAsset("settings-button", "Images/settings.png", 30, Scalr.Method.QUALITY);

        executor.shutdown();
        while (!executor.isTerminated()) Utilities.sleep(1);
    }

    public static ImageIcon getImage(String name){
        return images.get(name);
    }

    public static BufferedImage downloadAsset(String url) throws IOException {

        if(cachedDownloads.containsKey(url)){
            return cachedDownloads.get(url);
        }

        URL url1 = new URL(url);
        BufferedImage c = ImageIO.read(url1);

        cachedDownloads.put(url, c);
        return c;
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
    public static BufferedImage makeRoundedCorner(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();

        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new Ellipse2D.Double(0, 0, w, h));
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);

        g2.dispose();

        return output;
    }

    public static ImageIcon invertImage(ImageIcon buttonIcon) {
        BufferedImage img = new BufferedImage(
                buttonIcon.getIconWidth(),
                buttonIcon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.createGraphics();
        buttonIcon.paintIcon(null, g, 0,0);
        g.dispose();

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int rgba = img.getRGB(x, y);
                Color col = new Color(rgba, true);
                col = new Color(255 - col.getRed(),
                        255 - col.getGreen(),
                        255 - col.getBlue(), col.getAlpha());
                img.setRGB(x, y, col.getRGB());
            }
        }
        return new ImageIcon(img);

    }

}
