package com.alphalaneous.Swing.Components;

import com.alphalaneous.*;
import com.alphalaneous.Services.GeometryDash.LevelData;
import com.alphalaneous.Services.GeometryDash.RequestsUtils;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Windows.Window;

import javax.swing.*;

public class LevelDetailsPanel {

    private static final JPanel mainPanel = new JPanel(){{
        setLayout(null);
        setBackground(Defaults.COLOR);
        setBounds(260, 0, 510, Window.getWindow().getHeight());
    }};
    private static final JLabel label = new JLabel("No levels :("){{
        setFont(Defaults.MAIN_FONT.deriveFont(30f));
        setBounds(180, Window.getWindow().getHeight()/2-30, 510, 50);
    }};

    static int tries = 0;

    public static void setPanel(LevelData data){
        tries++;
        mainPanel.removeAll();
        if(data == null){
            label.setBounds(180, Window.getWindow().getHeight()/2-30, 510, 50);
            mainPanel.add(label);
        }
        else {
            try {
                mainPanel.add(new LevelDetails(data));
            }
            catch (NullPointerException e){
                if(tries <= 10) {
                    setPanel(data);
                }
            }
        }
        new Thread(() -> Main.sendMessageConnectedService(RequestsUtils.getInfoObject(data).toString())).start();
        if(data == null) Main.sendMessageConnectedService(RequestsUtils.getNextInfoObject(null).toString());
        else new Thread(() -> Main.sendMessageConnectedService(RequestsUtils.getNextInfoObject(RequestsTab.getRequest(RequestsUtils.getPosFromID(data.getGDLevel().id()) + 1).getLevelData()).toString())).start();

        tries = 0;
        mainPanel.repaint();

    }

    public static void refreshUI(){
        mainPanel.setBackground(Defaults.COLOR3);
        label.setForeground(Defaults.FOREGROUND_A);
        if(mainPanel.getComponents().length > 0){
            if(mainPanel.getComponents()[0] instanceof JLabel){
                mainPanel.getComponents()[0].setForeground(Defaults.FOREGROUND_A);
            }
            if(mainPanel.getComponents()[0] instanceof LevelDetails){
                ((LevelDetails)mainPanel.getComponents()[0]).refreshUI();
            }
        }

    }
    public static JPanel getPanel(){
        return mainPanel;
    }
    public static void setPositionAndHeight(int x, int height){
        mainPanel.setBounds(x, 0, 510, height);
        if(mainPanel.getComponents().length > 0) {
            if(mainPanel.getComponents()[0] instanceof LevelDetails) {
                mainPanel.getComponents()[0].setBounds(0, 0, 510, height);
                ((LevelDetails) mainPanel.getComponents()[0]).resizeAll(height);
            }
            if(mainPanel.getComponents()[0] instanceof JLabel){
                mainPanel.getComponents()[0].setBounds(180, height/2-30, 510, 50);

            }
        }

    }
}
