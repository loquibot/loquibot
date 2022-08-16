package com.alphalaneous.Interactive.MediaShare;

import com.alphalaneous.Swing.Components.MultiLineLabel;
import com.alphalaneous.Swing.Components.VideoProgressBar;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Images.BoxBlurFilter;
import com.alphalaneous.Main;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Swing.Components.VideoButton;
import com.alphalaneous.Swing.Components.VideoDetailsPanel;
import com.alphalaneous.Services.YouTube.YouTubeVideo;
import com.alphalaneous.Tabs.MediaShareTab;
import com.alphalaneous.Utils.Utilities;
import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Windows.Window;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSException;
import org.apache.commons.lang3.time.DurationFormatUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MediaShare {

    public static boolean sharingEnabled = true;
    private static final ArrayList<String> sentVideos = new ArrayList<>();


    public static void init(){
        new Thread(() -> {
            Utilities.sleep(50);
            frame.setSize(0, 0);
        }).start();
    }

    static class MediaSharePanel extends JPanel{
        private final JLabel background = new JLabel();
        private final WebView webView = new WebView();
        private final YouTubeVideo video;
        private final VideoProgressBar videoProgressBar;

        private static float volume = SettingsHandler.getSettings("mediaVolume").asFloat();
        private static MediaSharePanel currentVideo;

        public MediaSharePanel(YouTubeVideo video){
            currentVideo = this;
            this.video = video;
            MultiLineLabel title = new MultiLineLabel(video.getTitle(), 470, Defaults.MAIN_FONT.deriveFont(16f));
            videoProgressBar = new VideoProgressBar(video.getDuration());
            videoProgressBar.setBounds(0,0,480,340);
            JPanel panel = new JPanel();
            JFXPanel panel1 = new JFXPanel();
            VBox vBox = new VBox(webView);
            Scene scene = new Scene(vBox, 480, 270);
            panel.setBounds(0,0,480,340);
            panel.setLayout(null);
            panel1.setScene(scene);
            panel1.setBounds(0,0,480,270);

            background.setBounds(0,0,480, 340);

            title.setBounds(10,280, 480, 50);
            title.setForeground(Defaults.FOREGROUND_A);

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
            panel.add(background);
            background.setLayout(null);
            background.add(videoProgressBar);
            setLayout(null);
            setBounds(0,0,480,340);

            add(panel);

        }

        public void setBackground(Image icon){
            background.setIcon(new ImageIcon(blur((BufferedImage) icon)));
        }
        public void setTimeText(double time){
            videoProgressBar.setValue((int) (time*100));
            videoProgressBar.repaint();
        }
        public WebEngine getEngine(){
            return webView.getEngine();
        }
        private Thread thread;
        boolean paused = false;
        public void play(){
            Platform.runLater(() -> {

                frame.setSize(480, 340);

                setBackground(video.getImage().getImage());
                getEngine().load("https://www.youtube-nocookie.com/embed/" + video.getVideoID() + "?&autoplay=1&controls=0");
                Window.setPlayButtonIcon(true);
                Window.setSliderInfo(true, video.getDuration());
                System.out.println("https://www.youtube-nocookie.com/embed/" + video.getVideoID() + "?&autoplay=1&controls=0");

                final boolean[] setInitialVolume = {false};

                thread = new Thread(() -> {
                    while (true) {
                        Platform.runLater(() -> {
                            try {
                                removeElementByClassName(getEngine(), "ytp-watermark yt-uix-sessionlink");
                                removeElementByClassName(getEngine(), "ytp-chrome-top ytp-show-cards-title");
                                removeElementByClassName(getEngine(), "ytp-gradient-top");

                                    com.sun.webkit.dom.HTMLDivElementImpl offsetParent = (com.sun.webkit.dom.HTMLDivElementImpl) getEngine().executeScript("document.getElementsByClassName('ytp-large-play-button ytp-button')[0].offsetParent");
                                    if (offsetParent != null) {
                                        showMediaShare(video);
                                    }
                                double duration = (double) getEngine().executeScript("document.getElementsByTagName('video')[0].duration");
                                int errorLength = (int) getEngine().executeScript("document.getElementsByClassName('ytp-error').length");
                                if(errorLength == 1){
                                    int pos = MediaShareTab.getVideoPosition(video);
                                    showMediaShare(MediaShareTab.getVideo(pos + 1).getVideoData());
                                    MediaShareTab.setVideoSelect(pos + 1);
                                    VideoDetailsPanel.setPanel(MediaShareTab.getVideo(pos + 1).getVideoData());
                                    stop();
                                }
                                double time;
                                try{
                                    time = (double) getEngine().executeScript("document.getElementsByTagName('video')[0].currentTime");
                                }
                                catch (ClassCastException e){
                                    time = (int) getEngine().executeScript("document.getElementsByTagName('video')[0].currentTime");
                                }
                                setTimeText(time);
                                Window.setSliderValue((int) (time*10));

                                if(!setInitialVolume[0]){
                                    setVolume(volume);
                                    setInitialVolume[0] = true;
                                }

                                String formattedDuration = DurationFormatUtils.formatDuration((int)time * 1000L, "m:ss");

                                String formattedLength =  DurationFormatUtils.formatDuration((int)duration * 1000L, "m:ss");


                                Window.setTime(formattedDuration + " / " + formattedLength);

                                if (time >= duration) {

                                    System.out.println("Finished");

                                    int pos = MediaShareTab.getVideoPosition(video);

                                    int posNew = pos + 1;

                                    if(SettingsHandler.getSettings("mediaShareRemoveWhenDone").asBoolean()){
                                        MediaShareTab.removeVideo(pos);
                                        posNew = pos;
                                    }
                                    if (MediaShareTab.getQueueSize() > posNew) {
                                        showMediaShare(MediaShareTab.getVideo(posNew).getVideoData());
                                        MediaShareTab.setVideoSelect(posNew);
                                        VideoDetailsPanel.setPanel(MediaShareTab.getVideo(posNew).getVideoData());
                                    }
                                    if(MediaShareTab.getQueueSize() == 0){
                                        VideoDetailsPanel.setPanel(null);
                                    }
                                    stop();
                                }
                                if(!paused) {
                                    getEngine().executeScript("document.getElementsByTagName('video')[0].play();");
                                }
                            }
                            catch (JSException ignored){}
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        Utilities.sleep(100);
                    }
                });
                thread.start();
            });
        }
        public void stop(){

            Window.setPlayButtonIcon(false);
            Window.setTime("0:00 / 0:00");
            Window.setSliderInfo(false, 0);
            getEngine().executeScript("document.getElementsByTagName('video')[0].volume = 0;");

            //getEngine().load("about:blank");
            if(thread != null) {
                thread.stop();
            }
            frame.remove(this);
            frame.setSize(0,0);
            currentVideo = null;
        }
        public void togglePause(){
            boolean paused = (boolean) getEngine().executeScript("document.getElementsByTagName('video')[0].paused;");
            if(paused){
                this.paused = false;
                getEngine().executeScript("document.getElementsByTagName('video')[0].play();");
            }
            else{
                this.paused = true;
                getEngine().executeScript("document.getElementsByTagName('video')[0].pause();");
            }

        }


        public void pauseMedia(){
            if(!paused) getEngine().executeScript("document.getElementsByTagName('video')[0].pause();");
        }
        public void playMedia(){
            if(paused) getEngine().executeScript("document.getElementsByTagName('video')[0].play();");
        }

        public void setVolume(float value){

            if((value >= 0 || value <= 1)) {
                getEngine().executeScript("document.getElementsByTagName('video')[0].volume = " + value);
            }
        }

        public static MediaSharePanel getCurrentVideo(){
            return currentVideo;
        }

    }

    public static void setTime(double time){
        Platform.runLater(() -> MediaSharePanel.getCurrentVideo().getEngine().executeScript("document.getElementsByTagName('video')[0].currentTime = " + time + ";"));
    }

    public static void pause(){
        if(MediaSharePanel.getCurrentVideo() != null) {
            Platform.runLater(() -> MediaSharePanel.getCurrentVideo().pauseMedia());
            Window.setPlayButtonIcon(MediaSharePanel.getCurrentVideo().paused);
        }
    }
    public static void play(){
        if(MediaSharePanel.getCurrentVideo() != null) {
            Platform.runLater(() -> MediaSharePanel.getCurrentVideo().playMedia());
            Window.setPlayButtonIcon(MediaSharePanel.getCurrentVideo().paused);
        }
    }

    public static void setVolume(float value){
        Platform.runLater(() -> MediaSharePanel.getCurrentVideo().setVolume(value));
    }

    public static void togglePause(){
        if(MediaSharePanel.getCurrentVideo() != null) {
            Platform.runLater(() -> MediaSharePanel.getCurrentVideo().togglePause());
            Window.setPlayButtonIcon(MediaSharePanel.getCurrentVideo().paused);
        }
    }

    public static void removeMedia(int pos){
        MediaShareTab.removeVideo(pos);

        if(pos == VideoButton.selectedID){
            if(pos < MediaShareTab.getQueueSize()) {
                MediaShareTab.getVideosPanel().setSelect(pos, true);
                showMediaShare(MediaShareTab.getVideo(pos).getVideoData());
            }
            else{
                if(MediaSharePanel.getCurrentVideo() != null) {
                    Platform.runLater(() -> MediaSharePanel.getCurrentVideo().stop());
                }
                VideoDetailsPanel.setPanel(null);
            }
        }
    }

    public static void clearMedia(boolean skip) {
        new Thread(() -> {
            String option;
            if(skip){
                option = "CLEAR_ALL";
            }
            else{
                option = DialogBox.showDialogBox("$CLEAR_MEDIA_TITLE$", "$CLEAR_MEDIA_INFO$", "$CLEAR_MEDIA_SUBINFO$", new String[]{"$CLEAR_ALL$", "$CANCEL$"});
            }
            if (option.equalsIgnoreCase("CLEAR_ALL")) {
                if(MediaSharePanel.getCurrentVideo() != null) {
                    Platform.runLater(() -> MediaSharePanel.getCurrentVideo().stop());
                }
                MediaShareTab.clearVideos();
                VideoDetailsPanel.setPanel(null);
            }
        }).start();
    }


    public static void addMedia(YouTubeVideo video){

        if((video.getDuration() > SettingsHandler.getSettings("mediaShareMaxDuration").asInteger())
                && SettingsHandler.getSettings("mediaShareMaxDurationEnabled").asBoolean()) {

            if(video.isYT()) Main.sendYTMessage(Utilities.format("$MEDIA_SHARE_TOO_LONG$", video.getRequester(), video.getTitle(), SettingsHandler.getSettings("mediaShareMaxDuration").asInteger()));
            else Main.sendMessage(Utilities.format("$MEDIA_SHARE_TOO_LONG$", video.getRequester(), video.getTitle(), SettingsHandler.getSettings("mediaShareMaxDuration").asInteger()));
            return;
        }

        if(MediaShareTab.exists(video)){
            if(video.isYT()) Main.sendYTMessage(Utilities.format("$MEDIA_SHARE_IN_QUEUE$", video.getRequester(), video.getTitle()));
            else Main.sendMessage(Utilities.format("$MEDIA_SHARE_IN_QUEUE$", video.getRequester(), video.getTitle()));
            return;
        }

        if(SettingsHandler.getSettings("").asBoolean() && sentVideos.contains(video.getVideoID())){
            if(video.isYT()) Main.sendYTMessage(Utilities.format("$MEDIA_SHARE_ALREADY_SENT$", video.getRequester(), video.getTitle()));
            else Main.sendMessage(Utilities.format("$MEDIA_SHARE_ALREADY_SENT$", video.getRequester(), video.getTitle()));
            return;
        }

        if(video.isYT()) Main.sendYTMessage(Utilities.format("$MEDIA_SHARE_ADDED$", video.getRequester(), video.getTitle()));
        else Main.sendMessage(Utilities.format("$MEDIA_SHARE_ADDED$", video.getRequester(), video.getTitle()));

        VideoButton videoButton = new VideoButton(video);

        MediaShareTab.addVideo(videoButton);
        sentVideos.add(video.getVideoID());
        try{
            MediaShareTab.getVideosPanel().setSelect(VideoButton.selectedID, MediaShareTab.getQueueSize() == 1);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if(MediaShareTab.getQueueSize() == 1 && SettingsHandler.getSettings("mediaShareAutoPlay").asBoolean()){
            showMediaShare(video);
        }
        else if(VideoButton.selectedID + 2 == MediaShareTab.getQueueSize() && SettingsHandler.getSettings("mediaShareAutoPlay").asBoolean() && MediaSharePanel.getCurrentVideo() == null){
            MediaShareTab.getVideosPanel().setSelect(MediaShareTab.getQueueSize()-1, true);
            showMediaShare(video);
        }
    }

    static JDialog frame = new JDialog(){{

        setUndecorated(true);
        setFocusable(false);
        setLocation(40,40);
        setLayout(null);
        setTitle("loquibot - Media Share");
        setIconImages(Main.getIconImages());
        setVisible(SettingsHandler.getSettings("mediaShareEnabled").asBoolean());

    }};

    public static void setMediaShareVisible(boolean visible){
        frame.setVisible(visible);
    }


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
    public static void removeElementByClassName(WebEngine engine, String className){
        try {
            engine.executeScript("document.getElementsByClassName('" + className + "')[0].innerHTML = null;");
        }
        catch (Exception ignored){}
    }
}
