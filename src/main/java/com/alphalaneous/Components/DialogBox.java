package com.alphalaneous.Components;


import com.alphalaneous.Components.ThemableJComponents.*;
import com.alphalaneous.Utilities.GraphicsFunctions;
import com.alphalaneous.Utilities.Utilities;
import com.alphalaneous.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class DialogBox {

    public static String showDialogBox(String title, String info, String subInfo, String[] options) {
        return showDialogBox(title, info, subInfo, options, new Object[]{});
    }

    private static ThemeableJPanel panel;

    private static boolean disableClickThrough = false;

    public static void showDialogBox(JComponent component){
        showDialogBox(component, false);
    }

    public static void showDialogBox(JComponent component, boolean disableClickThrough){
        DialogBox.disableClickThrough = disableClickThrough;
        panel = new ThemeableJPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                GraphicsFunctions.dialog(g, getBackground(), getSize());
            }
        };
        panel.addMouseListener(new MouseAdapter() {});
        panel.setOpaque(false);
        panel.setLayout(null);
        panel.setBackground("background");
        component.setBounds(10,10,component.getWidth(), component.getHeight());
        component.setOpaque(false);
        component.setBackground(ThemeableColor.getColorByName("background"));
        panel.setBounds(0,0,component.getWidth() + 20, component.getHeight()+20);
        panel.add(component);
        com.alphalaneous.Window.showDialog(panel, disableClickThrough);
        panel.updateUI();
    }

    public static String showDialogBox(String title, String info, String subInfo, String[] options, Object[] args) {
        panel = new ThemeableJPanel() {
            @Override
            public void paintComponent(Graphics g) {
                GraphicsFunctions.dialog(g, getBackground(), getSize());
                super.paintComponent(g);
            }
        };

        ThemeableJPanel textPanel = new ThemeableJPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBounds(30, 70, 340, 110);
        textPanel.setBackground(new Color(0, 0, 0, 0));

        ThemeableJPanel titlePanel = new ThemeableJPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBounds(30, 25, 340, 110);
        titlePanel.setBackground(new Color(0, 0, 0, 0));

        ThemeableJPanel buttonPanel = new ThemeableJPanel();
        buttonPanel.setLayout(new GridLayout(1, 0, 6, 6));
        buttonPanel.setOpaque(false);
        buttonPanel.setBounds(30, 140, 340, 35);
        buttonPanel.setBackground(new Color(0, 0, 0, 0));

        ThemeableJLabel titleLabel = new ThemeableJLabel();
        titleLabel.setText(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        titleLabel.setForeground("foreground");

        ThemeableJLabel infoLabel = new ThemeableJLabel();
        infoLabel.setText("<html> " + info + " </html>");
        infoLabel.setBackground("background-lighter");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoLabel.setForeground("foreground");

        ThemeableJLabel subInfoLabel = new ThemeableJLabel();
        subInfoLabel.setText(subInfo);
        subInfoLabel.setBackground("background-lighter");
        subInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subInfoLabel.setForeground("foreground");

        titlePanel.add(titleLabel);
        textPanel.add(infoLabel);
        textPanel.add(subInfoLabel);

        panel.addMouseListener(new MouseAdapter() {
        });

        panel.setOpaque(false);
        panel.setBounds(0, 0, 400, 200);
        panel.setLayout(null);
        panel.setBackground("background-lighter");

        AtomicBoolean resultObtained = new AtomicBoolean(false);
        AtomicReference<String> result = new AtomicReference<>();


        for (String option : options) {
            ThemeableJButton button = createButton(option);
            button.setForeground("foreground", "foreground");
            button.setBackground("list-background-normal", "list-background-normal");
            button.addActionListener(e -> {
               result.set(button.getText());
               resultObtained.set(true);
            });
            buttonPanel.add(button);
        }

        panel.add(titlePanel);
        panel.add(textPanel);
        panel.add(buttonPanel);
        com.alphalaneous.Window.showDialog(panel);
        while(!resultObtained.get()){
            Utilities.sleep(10);
        }
        disableClickThrough = false;
        Window.closeDialog();
        return result.get();
    }

    private static ThemeableJButton createButton(String text) {

        ThemeableJButton button = new ThemeableJButton();
        button.setText(text);

        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        button.setForeground("foreground", "foreground");
        button.setBackground("list-background-normal", "list-background-normal");

        button.setBorder(BorderFactory.createEmptyBorder());

        return button;
    }

    public static void closeDialogBox() {
        disableClickThrough = false;
        com.alphalaneous.Window.closeDialog();
    }
}
