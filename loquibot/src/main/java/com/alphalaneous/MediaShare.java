package com.alphalaneous;

import com.alphalaneous.Images.BoxBlurFilter;
import com.alphalaneous.Swing.Components.VideoButton;
import com.alphalaneous.Swing.Components.VideoDetailsPanel;
import com.alphalaneous.Services.YouTube.YouTubeVideo;
import com.alphalaneous.Tabs.MediaShareTab;
import com.alphalaneous.Utils.Utilities;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class MediaShare {

    public static boolean sharingEnabled = true;

    public static void init(){

    }

    static class MediaSharePanel extends JPanel{
        private final JLabel background = new JLabel();
        private final JLabel title = new JLabel();
        private final JLabel timeLabel = new JLabel("0 / 0");
        private final WebView webView = new WebView();
        private final YouTubeVideo video;

        private static MediaSharePanel currentVideo;
        private static double prevDur;
        private static YouTubeVideo prevVideo;

        public MediaSharePanel(YouTubeVideo video){
            currentVideo = this;
            this.video = video;
            JPanel panel = new JPanel();
            JFXPanel panel1 = new JFXPanel();
            VBox vBox = new VBox(webView);
            Scene scene = new Scene(vBox, 480, 270);
            panel.setBackground(Color.ORANGE);
            panel.setBounds(0,0,480,340);
            panel.setLayout(null);
            panel1.setScene(scene);
            panel1.setBounds(0,0,480,270);

            background.setBounds(0,0,480, 340);

            title.setBounds(10,280, 480, 50);
            title.setFont(Defaults.MAIN_FONT.deriveFont(20f));
            title.setForeground(Defaults.FOREGROUND_A);

            timeLabel.setBounds(10,250, 480, 20);
            timeLabel.setFont(Defaults.MAIN_FONT.deriveFont(12f));
            timeLabel.setForeground(Defaults.FOREGROUND_A);


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
            panel.add(timeLabel);
            panel.add(title);
            panel.add(panel1);
            panel.add(background);
            setLayout(null);
            setBounds(0,0,480,340);

            add(panel);

        }

        public void setBackground(Image icon){
            background.setIcon(new ImageIcon(blur((BufferedImage) icon)));
        }
        public void setTitle(String titleText){
            title.setText(titleText);
        }
        public void setTimeText(String text){
            timeLabel.setText(text);
        }
        public WebEngine getEngine(){
            return webView.getEngine();
        }
        private Thread thread;
        private final int[] tries = {0};
        boolean playing = false;


        public void play(){
            Platform.runLater(() -> {
                frame.setSize(480, 340);

                setBackground(video.getImage().getImage());
                setTitle(video.getTitle());
                getEngine().load("https://www.youtube-nocookie.com/embed/" + video.getVideoID() + "?&autoplay=1");

                thread = new Thread(() -> {
                    while (true) {
                        Platform.runLater(() -> {
                            try {

                                double duration = (double) getEngine().executeScript("document.getElementsByTagName('video')[0].duration");
                                if (Double.isNaN(duration)) {
                                    int pos = MediaShareTab.getVideoPosition(video);
                                    showMediaShare(MediaShareTab.getVideo(pos + 1).getVideoData());
                                    MediaShareTab.setVideoSelect(pos + 1);
                                    VideoDetailsPanel.setPanel(MediaShareTab.getVideo(pos + 1).getVideoData());
                                }

                                double time = (double) getEngine().executeScript("document.getElementsByTagName('video')[0].currentTime");
                                setTimeText(time + " / " + duration);
                                if (prevVideo == video) tries[0]++;
                                if (time >= duration && (prevDur != duration || tries[0] >= 5)) {

                                    System.out.println("Finished");
                                    prevDur = duration;
                                    prevVideo = video;

                                    int pos = MediaShareTab.getVideoPosition(video);

                                    //MediaShareTab.removeVideo(pos);
                                    if (MediaShareTab.getQueueSize() > pos + 1) {
                                        showMediaShare(MediaShareTab.getVideo(pos + 1).getVideoData());
                                        MediaShareTab.setVideoSelect(pos + 1);
                                        VideoDetailsPanel.setPanel(MediaShareTab.getVideo(pos + 1).getVideoData());
                                    }
                                    playing = false;
                                    stop();
                                }
                            } catch (Exception ignored) {
                            }
                        });
                        Utilities.sleep(100);
                    }
                });
                thread.start();
            });
        }
        public void stop(){
            if(thread != null) {
                thread.stop();
            }
            getEngine().load("about:blank");
            frame.remove(this);
            frame.setSize(0,0);
            currentVideo = null;
        }
        public boolean isPlaying(){
            return playing;
        }
        public static MediaSharePanel getCurrentVideo(){
            return currentVideo;
        }

    }


    public static void addMedia(YouTubeVideo video){

        if((video.getDuration() > Settings.getSettings("mediaShareMaxDuration").asInteger())
                && Settings.getSettings("mediaShareMaxDurationEnabled").asBoolean()) {
            Main.sendMessage(Utilities.format("$MEDIA_SHARE_TOO_LONG$", video.getRequester(), video.getTitle(), Settings.getSettings("mediaShareMaxDuration").asInteger()));
            Main.sendYTMessage(Utilities.format("$MEDIA_SHARE_TOO_LONG$", video.getRequester(), video.getTitle(), Settings.getSettings("mediaShareMaxDuration").asInteger()));
            return;
        }

        if(MediaShareTab.exists(video)){
            Main.sendMessage(Utilities.format("$MEDIA_SHARE_IN_QUEUE$", video.getRequester(), video.getTitle()));
            return;
        }

        Main.sendMessage(Utilities.format("$MEDIA_SHARE_ADDED$", video.getRequester(), video.getTitle()));
        Main.sendYTMessage(Utilities.format("$MEDIA_SHARE_ADDED$", video.getRequester(), video.getTitle()));

        VideoButton videoButton = new VideoButton(video);

        MediaShareTab.addVideo(videoButton);
        try{
            MediaShareTab.getVideosPanel().setSelect(VideoButton.selectedID, MediaShareTab.getQueueSize() == 1);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if(MediaShareTab.getQueueSize() == 1 && Settings.getSettings("mediaShareAutoPlay").asBoolean()){
            showMediaShare(video);
        }
        else if(VideoButton.selectedID + 2 == MediaShareTab.getQueueSize() && Settings.getSettings("mediaShareAutoPlay").asBoolean() && !MediaSharePanel.getCurrentVideo().isPlaying()){
            MediaShareTab.getVideosPanel().setSelect(MediaShareTab.getQueueSize()-1, MediaShareTab.getQueueSize() == 1);
            showMediaShare(video);
        }
    }

    static JFrame frame = new JFrame(){{
        setUndecorated(true);
        setFocusable(false);
        setSize(0, 0);
        setLocation(40,40);
        setLayout(null);
        setTitle("loquibot - Media Share");
        setVisible(true);

    }};

    public static void showMediaShare(YouTubeVideo video){
        Platform.runLater(() -> {
            if(MediaSharePanel.getCurrentVideo() != null) {
                MediaSharePanel.getCurrentVideo().stop();
            }
            MediaSharePanel panel = new MediaSharePanel(video);
            frame.add(panel);
            panel.play();
        });
    }

    public static int getQueueSize(){
        return MediaShareTab.getQueueSize();
    }

    public static YouTubeVideo getVideo(int i){
        return MediaShareTab.getVideo(i).getVideoData();
    }


    public static BufferedImage blur(BufferedImage input) {

        BufferedImage newImage = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_RGB);

        BoxBlurFilter boxBlurFilter = new BoxBlurFilter();
        boxBlurFilter.setRadius(500);
        boxBlurFilter.filter(input, newImage);

        Graphics2D gbi = newImage.createGraphics();

        gbi.drawImage(newImage, 0, 0, null);
        gbi.setColor(Defaults.OVERLAY);
        gbi.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.4f));
        gbi.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());
        newImage.getGraphics().drawImage(newImage, 0, 0, null);
        return newImage;
    }

}
