package com.alphalaneous;

import com.alphalaneous.Panels.LevelButton;
import com.alphalaneous.Panels.VideoButton;
import com.alphalaneous.Tabs.MediaShareTab;
import com.alphalaneous.Tabs.RequestsTab;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class MediaShare {

    static String html = "<html lang=\"en\">\n" +
            "    <head>\n" +
            "        <style>\n" +
            "            body, html {width: 100%; height: 100%; margin: 0; padding: 0}\n" +
            "            .video {position: absolute; top: 0; left: 0; right: 0; bottom: 0;}\n" +
            "            .video iframe {display: block; width: 100%; height: 100%; border: none;}\n" +
            "        </style>\n" +
            "        <title>Video</title>\n" +
            "    </head>\n" +
            "\n" +
            "    <div class =\"video\">\n" +
            "        <iframe width=\"560\" \n" +
            "        height=\"315\" \n" +
            "        src= \n" +
            "        title=\"YouTube video player\" \n" +
            "        frameborder=\"0\" \n" +
            "        allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\"\n" +
            "        rel=\"0\"\n" +
            "        allowfullscreen></iframe>\n" +
            "    </div>\n" +
            "</html>";


    static JFrame frame = new JFrame();
    static JPanel panel = new JPanel();
    static JFXPanel panel1 = new JFXPanel();
    static WebView webView = new WebView();
    static VBox vBox = new VBox(webView);
    static Scene scene = new Scene(vBox, 480, 270);
    static JLabel title = new JLabel();
    public static boolean sharingEnabled = true;

    public static void init(){
        panel.setBackground(Color.ORANGE);
        panel.setBounds(0,0,480,340);
        panel.setLayout(null);
        panel1.setScene(scene);
        panel1.setBounds(0,0,480,270);

        title.setBounds(10,280, 480, 50);
        title.setFont(Defaults.MAIN_FONT.deriveFont(20f));
        title.setForeground(Defaults.FOREGROUND_A);

        frame.setTitle("loquibot - Media Share");

        MouseAdapter ma = new MouseAdapter() {
            int lastX, lastY;
            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getXOnScreen();
                lastY = e.getYOnScreen();
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getXOnScreen();
                int y = e.getYOnScreen();
                frame.setLocation(frame.getLocationOnScreen().x + x - lastX,
                        frame.getLocationOnScreen().y + y - lastY);
                lastX = x;
                lastY = y;
            }
        };
        panel.addMouseListener(ma);
        panel.addMouseMotionListener(ma);


        panel.add(title);
        panel.add(panel1);
        frame.add(panel);

        frame.setUndecorated(true);
        frame.setSize(480, 340);
    }

    public static void addMedia(YouTubeVideo video){

        VideoButton videoButton = new VideoButton(video);
        MediaShareTab.addVideo(videoButton);
        try{
            MediaShareTab.getVideosPanel().setSelect(VideoButton.selectedID, MediaShareTab.getQueueSize() == 1);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void showMediaShare(String videoID, String videoName){

        String newHtml = html.replace("src=", "src=\"https://www.youtube-nocookie.com/embed/"+ videoID + "?&autoplay=1\"");

        title.setText(videoName);

        Platform.runLater(() -> {
            webView.getEngine().loadContent(newHtml);
        });
        frame.setVisible(true);
    }

    public static int getQueueSize(){
        return MediaShareTab.getQueueSize();
    }

    public static YouTubeVideo getVideo(int i){
        return MediaShareTab.getVideo(i).getVideoData();
    }

}
