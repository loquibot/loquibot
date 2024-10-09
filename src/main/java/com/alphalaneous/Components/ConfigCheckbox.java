package com.alphalaneous.Components;

import com.alphalaneous.Enums.UserLevel;
import com.alphalaneous.Interactive.Actions.ActionData;
import com.alphalaneous.Components.ThemableJComponents.ThemeableColor;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJLabel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Utilities.Fonts;
import com.alphalaneous.Interactive.CustomData;
import com.alphalaneous.Interfaces.Function;
import com.alphalaneous.Utilities.GraphicsFunctions;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ConfigCheckbox extends ThemeableJPanel {

    private final CustomData customData;

    private Color userLevelColor = null;

    private final ThemeableJPanel colorPanel = new ThemeableJPanel() {
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            g2.setComposite(AlphaComposite.Clear);

            g.fillRect(0,0, getWidth(), getHeight());

            g2.setComposite(AlphaComposite.SrcOver);

            if(userLevelColor == null) {
                g.setColor(getBackground());
            }
            else{
                g.setColor(userLevelColor);
            }

            RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHints(qualityHints);
            g2.fillRoundRect(0, 0, 50, getSize().height, 20, 20);
            g2.fillRect(10, 0, getSize().width, getSize().height);

        }
    };

    public ConfigCheckbox(CustomData data, Function function, boolean isAction){

        this.customData = data;

        colorPanel.setBackground("list-background-selected");
        colorPanel.setOpaque(false);

        colorPanel.setPreferredSize(new Dimension(5, 70));

        setPreferredSize(new Dimension(100,70));

        setBackground("list-background-normal");


        setLayout(new BorderLayout(0,0));

        setOpaque(false);

        ThemeableJPanel buttonPanel = getPanel();

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


        ThemeableJLabel titleLabel = new ThemeableJLabel(data.getName());
        ThemeableJLabel descLabel = new ThemeableJLabel(data.getMessage());

        titleLabel.setFont(Fonts.getFont("Poppins-Regular").deriveFont(20f));

        descLabel.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));

        titleLabel.setForeground("foreground");
        descLabel.setForeground("foreground-darker");

        ThemeableJPanel contentPanel = new ThemeableJPanel();
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

        contentPanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        contentPanel.add(colorPanel, 0);

        checkbox.setChecked(customData.isEnabled());

        checkBoxPanel.add(checkbox);

        if(!isAction) contentPanel.add(checkBoxPanel);
        contentPanel.add(leftPanel);

        add(buttonPanel, BorderLayout.EAST);
        add(contentPanel, BorderLayout.WEST);

    }

    private @NotNull ThemeableJPanel getPanel() {
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
        return buttonPanel;
    }


    public void setUserLevel(UserLevel level){


        userLevelColor = null;

        if(level == UserLevel.OWNER){
            userLevelColor = new Color(231, 25, 23);
        }
        if(level == UserLevel.MODERATOR){
            userLevelColor = new Color(8, 175, 12);
        }
        if(level == UserLevel.VIP){
            userLevelColor = new Color(223, 1, 186);
        }
        if(level == UserLevel.SUBSCRIBER){
            userLevelColor = new Color(129, 5, 180);
        }

        colorPanel.revalidate();
    }

    @Override
    public void paintComponent(Graphics g) {

        GraphicsFunctions.roundCorners(g, getBackground(), getSize());
        super.paintComponent(g);
    }

    public void runAction(){
        ((ActionData)customData).runAction();
    }
}
