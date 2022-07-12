package com.alphalaneous.Swing.Components;

import com.alphalaneous.Services.YouTube.YouTubeVideo;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Windows.Window;

import javax.swing.*;

public class VideoDetailsPanel {

    private static final JPanel mainPanel = new JPanel(){{
        setLayout(null);
        setBackground(Defaults.COLOR);
        setBounds(0, 0, 360, Window.getWindow().getHeight());
    }};
    private static final JLabel label = new JLabel("No videos :("){{
        setFont(Defaults.MAIN_FONT.deriveFont(30f));
        setBounds(95, Window.getWindow().getHeight()/2-30, 360, 50);
    }};

    static int tries = 0;

    public static void setPanel(YouTubeVideo data){
        tries++;
        mainPanel.removeAll();
        if(data == null){
            label.setBounds(95, Window.getWindow().getHeight()/2-30, 360, 50);
            mainPanel.add(label);
        }
        else {
            try {
                mainPanel.add(new VideoDetails(data));
            }
            catch (NullPointerException e){
                if(tries <= 10) {
                    setPanel(data);
                }
            }
        }
        tries = 0;
        mainPanel.repaint();
        mainPanel.revalidate();
    }

    public static void refreshUI(){
        mainPanel.setBackground(Defaults.COLOR3);
        label.setForeground(Defaults.FOREGROUND_A);
        if(mainPanel.getComponents().length > 0){
            if(mainPanel.getComponents()[0] instanceof JLabel){
                mainPanel.getComponents()[0].setForeground(Defaults.FOREGROUND_A);
            }
            if(mainPanel.getComponents()[0] instanceof VideoDetails){
                ((VideoDetails)mainPanel.getComponents()[0]).refreshUI();
            }
        }

    }
    public static JPanel getPanel(){
        return mainPanel;
    }
    public static void setPositionAndHeight(int x, int height){
        mainPanel.setBounds(x, 0, 360, height);
        if(mainPanel.getComponents().length > 0) {
            if(mainPanel.getComponents()[0] instanceof VideoDetails) {
                mainPanel.getComponents()[0].setBounds(0, 0, 360, height);
                ((VideoDetails) mainPanel.getComponents()[0]).resizeAll(height);
            }
            if(mainPanel.getComponents()[0] instanceof JLabel){
                mainPanel.getComponents()[0].setBounds(95, height/2-30, 360, 50);

            }
        }

    }
}
