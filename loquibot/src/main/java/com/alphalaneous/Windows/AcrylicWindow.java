package com.alphalaneous.Windows;

import com.alphalaneous.AcrylicFrame;
import com.alphalaneous.Defaults;
import com.alphalaneous.Main;
import com.alphalaneous.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AcrylicWindow {

    private static final JFrame windowFrame = new AcrylicFrame("loquibot");



    private static final int width = 800, height = 660;

    public static void initFrame() {

        windowFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Main.close();
            }
        });
        windowFrame.setIconImages(Main.getIconImages());
        windowFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        windowFrame.setSize(width, height + 30);
        windowFrame.setMinimumSize(new Dimension(400, 200));
        windowFrame.setLayout(null);
        windowFrame.getContentPane().setBackground(Defaults.COLOR);
        windowFrame.getRootPane().setBackground(Defaults.COLOR);
        windowFrame.setBackground(Defaults.COLOR);
        if(Settings.getSettings("window").exists()){
            String position = Settings.getSettings("window").asString();
            int winX = Integer.parseInt(position.split(",")[0]);
            int winY = Integer.parseInt(position.split(",")[1]);
            windowFrame.setLocation(winX, winY);
        }
        else{
            windowFrame.setLocationRelativeTo(null);
        }
        windowFrame.add(new JLabel("bobert"));


    }

    public static void setVisible(boolean visible) {
        try {
            windowFrame.setVisible(visible);
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(null, "Error opening loquibot: " + e, "Error", JOptionPane.ERROR_MESSAGE);
            Main.close(true, false);
        }
    }


}
