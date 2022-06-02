package com.alphalaneous.SettingsPanels;

import com.alphalaneous.*;
import com.alphalaneous.Components.*;
import com.alphalaneous.Windows.CommandEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ChannelPointSettings {

    private static ListView listView = new ListView("$POINTS_LIST$");
    private static final LangLabel notAvailable = new LangLabel("$CHANNEL_POINTS_UNAVAILABLE$");
    private static SettingsComponent notAvailableComponent;

    public static JPanel createPanel(){
        listView = new ListView("$POINTS_LIST$"); // Redundancy fixes weird text bug
        notAvailable.setForeground(Defaults.FOREGROUND_A);
        notAvailable.setFont(Defaults.MAIN_FONT.deriveFont(16f));
        notAvailableComponent = new SettingsComponent(notAvailable, new Dimension(1500,32)){
            @Override
            protected void refreshUI(){
                notAvailable.setForeground(Defaults.FOREGROUND_A);
            }
        };
        listView.addButton("\uF078", ChannelPointSettings::refresh);

        //refresh();
        return listView;
    }

    public static void clear(){
        listView.clearElements();
    }

    public static void refresh(){
        if(Settings.getSettings("twitchEnabled").asBoolean()) {
            try {
                listView.clearElements();
                if (TwitchAccount.broadcaster_type.equalsIgnoreCase("affiliate")
                        || TwitchAccount.broadcaster_type.equalsIgnoreCase("partner")) {
                    ArrayList<ChannelPointReward> rewards = APIs.getChannelPoints();
                    for (ChannelPointReward reward : rewards) {
                        listView.addElement(createButton(reward.getTitle(), reward.getBgColor(), reward.getIcon(), reward.isDefaultIcon()));
                    }
                } else {
                    listView.addElement(notAvailableComponent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static CurvedButton createButton(String command, Color color, Icon icon, boolean defaultIcon){
        JButtonUI colorUI = new JButtonUI();
        colorUI.setBackground(color);
        colorUI.setHover(color);
        colorUI.setSelect(color.darker());
        CurvedButton button = new CurvedButton("");
        JLabel pointLabel = new JLabel("<html><div style=\"text-align: center;\" width=120; > " + command + " </div></html>", SwingConstants.CENTER);
        JLabel pointIcon = new JLabel(icon);
        pointLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        button.setBackground(color);
        button.setLayout(null);
        pointLabel.setBounds(0, 20, 120, 120);
        pointIcon.setBounds(0, -10, 120, 120);

        button.add(pointLabel);
        button.add(pointIcon);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setUI(colorUI);
        double brightness = Math.sqrt(color.getRed() * color.getRed() * .241 +
                color.getGreen() * color.getGreen() * .691 +
                color.getBlue() * color.getBlue() * .068);

        if (brightness > 130) {
            pointLabel.setForeground(Color.BLACK);
            if (defaultIcon) {
                pointIcon.setIcon(new ImageIcon(HighlightButton.colorImage(HighlightButton.convertToBufferedImage(icon), Color.BLACK)));
            }
        } else {
            pointLabel.setForeground(Color.WHITE);
        }
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setPreferredSize(new Dimension(120, 120));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (brightness > 130) {
                    button.setBackground(button.getBackground().darker());
                } else {
                    button.setBackground(button.getBackground().brighter());

                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    Path comPath = Paths.get(Defaults.saveDirectory + "/loquibot/points/" + command + ".js");

                    new Thread(() -> {
                        try {
                            if(Files.exists(comPath)) {
                                Command.run(TwitchAccount.display_name, true, true, new String[]{"dummy"}, Files.readString(comPath, StandardCharsets.UTF_8), 0, null, -1);
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }).start();

                }
            }
        });
        button.addActionListener(e -> CommandEditor.showEditor("points", command, false));
        button.refresh();
        return button;
    }
}
