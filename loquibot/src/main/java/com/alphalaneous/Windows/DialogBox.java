package com.alphalaneous.Windows;

import com.alphalaneous.Swing.Components.CurvedButton;
import com.alphalaneous.Swing.Components.GraphicsFunctions;
import com.alphalaneous.Swing.Components.LangLabel;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Utils.Utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.alphalaneous.Utils.Defaults.defaultUI;

public class DialogBox {

    //todo redesign, allow for multilined text

    public static String showDialogBox(String title, String info, String subInfo, String[] options) {
        return showDialogBox(title, info, subInfo, options, new Object[]{});
    }

    private static JPanel panel;

    private static boolean disableClickThrough = false;

    public static void showDialogBox(JComponent component){
        showDialogBox(component, false);
    }

    public static void showDialogBox(JComponent component, boolean disableClickThrough){
        DialogBox.disableClickThrough = disableClickThrough;
        panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                GraphicsFunctions.roundedDialog(g, getBackground(), getSize());
                super.paintComponent(g);
            }
        };
        panel.addMouseListener(new MouseAdapter() {});
        panel.setOpaque(false);
        panel.setLayout(null);
        panel.setBackground(Defaults.COLOR3);
        component.setBounds(10,10,component.getWidth(), component.getHeight());
        panel.setBounds(0,0,component.getWidth() + 20, component.getHeight()+20);
        panel.add(component);
        Window.showDialog(panel, disableClickThrough);
    }

    public static String showDialogBox(String title, String info, String subInfo, String[] options, Object[] args) {
        panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                GraphicsFunctions.roundedDialog(g, getBackground(), getSize());
                super.paintComponent(g);
            }
        };

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBounds(30, 70, 340, 110);
        textPanel.setBackground(new Color(0, 0, 0, 0));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBounds(30, 25, 340, 110);
        titlePanel.setBackground(new Color(0, 0, 0, 0));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 6, 6));
        buttonPanel.setOpaque(false);
        buttonPanel.setBounds(30, 140, 340, 35);
        buttonPanel.setBackground(new Color(0, 0, 0, 0));

        LangLabel titleLabel = new LangLabel("");
        titleLabel.setTextLangFormat(title, args);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        titleLabel.setForeground(Defaults.FOREGROUND_A);

        LangLabel infoLabel = new LangLabel("<html> " + info + " </html>");
        infoLabel.setBackground(Defaults.COLOR6);
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoLabel.setForeground(Defaults.FOREGROUND_A);

        LangLabel subInfoLabel = new LangLabel(subInfo);
        subInfoLabel.setBackground(Defaults.COLOR6);
        subInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subInfoLabel.setForeground(Defaults.FOREGROUND_A);

        titlePanel.add(titleLabel);
        textPanel.add(infoLabel);
        textPanel.add(subInfoLabel);

        panel.addMouseListener(new MouseAdapter() {
        });

        panel.setOpaque(false);
        panel.setBounds(0, 0, 400, 200);
        panel.setLayout(null);
        panel.setBackground(Defaults.COLOR3);

        AtomicBoolean resultObtained = new AtomicBoolean(false);
        AtomicReference<String> result = new AtomicReference<>();


        for (String option : options) {
            CurvedButton button = createButton(option);
            button.setForeground(Defaults.FOREGROUND_A);
            button.setBackground(Defaults.COLOR);
            button.addActionListener(e -> {
               result.set(button.getIdentifier());
               resultObtained.set(true);
            });
            buttonPanel.add(button);
        }

        panel.add(titlePanel);
        panel.add(textPanel);
        panel.add(buttonPanel);
        Window.showDialog(panel);
        while(!resultObtained.get()){
            Utilities.sleep(10);
        }
        disableClickThrough = false;
        Window.closeDialog();
        return result.get();
    }

    private static CurvedButton createButton(String text) {

        CurvedButton button = new CurvedButton(text);

        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        button.setUI(defaultUI);

        button.setForeground(Defaults.FOREGROUND_A);
        button.setBackground(Defaults.COLOR2);

        button.setBorder(BorderFactory.createEmptyBorder());

        return button;
    }

    public static void closeDialogBox() {
        disableClickThrough = false;
        Window.closeDialog();
    }

    public static void refreshUI(){
        panel.setBackground(Defaults.COLOR3);
    }
}
