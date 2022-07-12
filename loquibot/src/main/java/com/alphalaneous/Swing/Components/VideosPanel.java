package com.alphalaneous.Swing.Components;

import com.alphalaneous.MediaShare;
import com.alphalaneous.Services.YouTube.YouTubeVideo;

import javax.swing.*;
import java.awt.*;

public class VideosPanel extends JPanel {

    private final JPanel buttonPanel = new JPanel();
    private int buttonWidth = 400;

    private final GridBagConstraints gbc = new GridBagConstraints();
    private final JPanel borderPanel = new JPanel(new BorderLayout());
    private final JScrollPane scrollPane = new SmoothScrollPane(borderPanel);

    public VideosPanel(){
        setOpaque(false);
        setLayout(new BorderLayout());
        setBackground(new Color(0,0,0,0));
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setOpaque(false);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(8, 9, 0, 2);
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        borderPanel.add(buttonPanel, BorderLayout.NORTH);
        borderPanel.setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setBackground(new Color(0,0,0,0));
        scrollPane.getViewport().setBackground(new Color(0,0,0,0));

        scrollPane.getViewport().setOpaque(false);
        borderPanel.setBackground(new Color(0,0,0,0));
        buttonPanel.setBackground(new Color(0,0,0,0));
        add(scrollPane);
    }

    public JPanel getButtonPanel(){
        return buttonPanel;
    }

    public void addButton(VideoButton button){
        buttonPanel.add(button, gbc);
    }
    public void addButton(VideoButton button, int pos){
        buttonPanel.add(button, gbc, pos);
    }
    public void clearVideos(){
        buttonPanel.removeAll();
    }
    public int getQueueSize(){
        return buttonPanel.getComponentCount();
    }

    public void resizePanel(int width, int height){
        buttonWidth = width - 18;
        Component[] comp = buttonPanel.getComponents();
        for (Component component : comp) {
            if (component instanceof VideoButton) {
                ((VideoButton) component).resizeButton();
            }
        }
        setBounds(0, 0, width, height+8);
    }

    public void updateUI(String ID) {
        for (Component component : buttonPanel.getComponents()) {
            if (component instanceof VideoButton) {
                if (((VideoButton) component).getVideoData().getVideoID().equals(ID)) {
                    ((VideoButton) component).refresh();
                    return;
                }
            }
        }
    }

    public VideoButton getButton(int i) {
        return ((VideoButton) buttonPanel.getComponent(i));
    }

    public void removeVideo(int pos){
        buttonPanel.remove(pos);
    }

    public void movePosition(int position, int newPosition) {
        String selectID = null;
        if (newPosition >= MediaShare.getQueueSize()) {
            newPosition = MediaShare.getQueueSize() - 1;
        }
        for (int i = 0; i < MediaShare.getQueueSize(); i++) {
            if (getButton(i).selected) {
                YouTubeVideo video = MediaShare.getVideo(i);
                if (video != null) {
                    selectID = video.getVideoID();
                }
            }
        }
        System.out.println("Position: " + position + " | newPosition: " + newPosition);
        buttonPanel.add(buttonPanel.getComponents()[position], gbc, newPosition);
        if (selectID != null){
            for (int i = 0; i < MediaShare.getQueueSize(); i++) {
                YouTubeVideo video = MediaShare.getVideo(i);
                if (video != null) {
                    if (selectID.equals(video.getVideoID())) {
                        setSelect(i, false, false);
                    }
                }
            }
        }
    }
    public void setSelect(int position){
        setSelect(position, false, true);
    }
    public void setSelect(int position, boolean refresh){
        setSelect(position, refresh, true);
    }
    public void setSelect(int position, boolean refresh, boolean resetScroll){
        deselectAll();
        if(buttonPanel.getComponentCount() != 0) {
            VideoButton button;
            if (buttonPanel.getComponentCount() == 1) {
                button = ((VideoButton) buttonPanel.getComponents()[0]);
            } else {
                button = ((VideoButton) buttonPanel.getComponents()[position]);
            }
            button.select(refresh);
            if (resetScroll) {
                if (position == 0) {
                    scrollPane.getViewport().setViewPosition(new Point(0, 0));
                } else {
                    scrollPane.getViewport().setViewPosition(new Point(0, button.getY()));
                }
            }
        }
    }

    public int getButtonWidth(){
        return buttonWidth;
    }

    public void deselectAll(){
        for(int i = 0; i < buttonPanel.getComponents().length; i++){
            if(buttonPanel.getComponents()[i] instanceof VideoButton){
                ((VideoButton)buttonPanel.getComponents()[i]).deselect();
            }
        }
    }

    public void refreshUI(){
        for (Component component : buttonPanel.getComponents()) {
            if (component instanceof VideoButton) {
                ((VideoButton) component).refresh();
            }
        }
        scrollPane.getVerticalScrollBar().setUI(new ScrollbarUI());
    }
}
