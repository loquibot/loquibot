package com.alphalaneous.Components;

import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.ChatBot.TwitchChatListener;
import com.alphalaneous.Components.ThemableJComponents.ThemeableColor;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJLabel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Utilities.Fonts;
import com.alphalaneous.Interactive.Commands.CommandHandler;
import com.alphalaneous.Interactive.CustomData;
import com.alphalaneous.Interfaces.Function;
import com.alphalaneous.Utilities.GraphicsFunctions;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ConfigCheckbox extends ThemeableJPanel {

    private final CustomData customData;

    public ConfigCheckbox(CustomData data, Function function, boolean isAction){

        this.customData = data;

        setPreferredSize(new Dimension(100,70));

        setBackground("list-background-normal");


        setLayout(new BorderLayout());

        setOpaque(false);

        ThemeableJPanel buttonPanel = new ThemeableJPanel(){
            @Override
            public void paintComponent(Graphics g) {

                Graphics2D g2d = (Graphics2D) g;
                Color endColor = ThemeableColor.getColorByName("list-background-normal");
                int startX = 0, startY = 0, endX = 40, endY = 0;

                GradientPaint gradient = new GradientPaint(startX, startY, new Color(0,0,0,0), endX, endY, endColor);
                g2d.setPaint(gradient);

                g2d.fillRect(0,0,40, getHeight());

                g2d.setColor(ThemeableColor.getColorByName("list-background-normal"));
                g2d.fillRect(40,0,getWidth()-60, getHeight());

                g2d.fillRoundRect(getWidth()-40, 0, 40, getHeight(), 20,  20);
                super.paintComponent(g);
            }
        };
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setBackground("empty");
        buttonPanel.setBorder(new EmptyBorder(0,40,0,10));

        RoundedButton settingButton = new RoundedButton("\uF309");
        settingButton.setFont(Fonts.getFont("Glyphs").deriveFont(16f));

        settingButton.setPreferredSize(new Dimension(40,40));
        settingButton.addActionListener(e -> {
            if(function != null){
                function.run();
            }
        });

        RoundedButton runButton = new RoundedButton("\uF184 ");
        runButton.setFont(Fonts.getFont("Glyphs").deriveFont(16f));

        runButton.setPreferredSize(new Dimension(40,40));
        runButton.addActionListener(e -> runAction());

        if(isAction) buttonPanel.add(runButton);
        buttonPanel.add(settingButton);

        ThemeableJPanel contentPanel = new ThemeableJPanel();

        ThemeableJLabel titleLabel = new ThemeableJLabel(data.getName());
        ThemeableJLabel descLabel = new ThemeableJLabel(data.getMessage());

        titleLabel.setFont(Fonts.getFont("Poppins-Regular").deriveFont(20f));

        descLabel.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));

        titleLabel.setForeground("foreground");
        descLabel.setForeground("foreground-darker");

        contentPanel.setOpaque(false);

        ThemeableJPanel leftPanel = new ThemeableJPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        if(isAction) leftPanel.add(Box.createRigidArea(new Dimension(25, 4)));
        else leftPanel.add(Box.createRigidArea(new Dimension(0, 4)));


        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(2));
        leftPanel.add(descLabel);

        ThemeableJPanel checkBoxPanel = new ThemeableJPanel();
        checkBoxPanel.setOpaque(false);
        checkBoxPanel.setBorder(new EmptyBorder(0,10,0,0));
        Checkbox checkbox = new Checkbox();


        checkbox.addCheckListener(b -> {
            customData.setEnabled(b);
            customData.save();
        });

        checkbox.setChecked(customData.isEnabled());

        checkBoxPanel.add(checkbox);

        if(!isAction) contentPanel.add(checkBoxPanel);
        contentPanel.add(leftPanel);

        add(buttonPanel, BorderLayout.EAST);
        add(contentPanel, BorderLayout.WEST);

    }
    @Override
    public void paintComponent(Graphics g) {

        GraphicsFunctions.roundCorners(g, getBackground(), getSize());
        super.paintComponent(g);
    }

    public void runAction(){
        new Thread(() -> {
            ChatMessage chatMessage = new ChatMessage(new String[0], "ActionHandler", "ActionHandler", "", new String[0], true, true, true, false, false);

            TwitchChatListener.getCurrentListener().sendMessage(CommandHandler.replaceBetweenParentheses(chatMessage, customData.getMessage(), customData, null));
        }).start();
    }
}
