package com.alphalaneous.Components;

import com.alphalaneous.Defaults;
import com.alphalaneous.Settings;
import org.jnativehook.keyboard.SwingKeyAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class SmallInputButton extends JPanel {

    private final FancyTextArea input = new FancyTextArea(false, false);
    private final LangLabel label;

    public SmallInputButton(String text, String setting, String defaultValue){
        setLayout(new BorderLayout());
        setBackground(new Color(0,0,0,0));
        setOpaque(false);
        label = new LangLabel(text);

        if(Settings.getSettings(setting).exists()) input.setText(Settings.getSettings(setting).asString());
        else input.setText(defaultValue);



        input.addKeyListener(new SwingKeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                Settings.writeSettings(setting, input.getText().replace(" ", ""));
                input.setText(input.getText().replace(" ", ""));
            }
        });

        input.setPreferredSize(new Dimension(100,32));

        label.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        label.setBounds(0, 3, 500, 30);
        label.setForeground(Defaults.FOREGROUND_A);

        add(input, BorderLayout.EAST);
        add(label, BorderLayout.WEST);

        input.setBounds(400, 1, 100, 32);
        label.setBounds(0, 3, 500, 30);

    }
    public void resizeButton(int width){
        setBounds(30,getY(),width-350, getHeight());
        input.setBounds(width-400, 1, 100, 32);
    }

    public void refreshUI(){
        setBackground(Defaults.COLOR3);
        label.setForeground(Defaults.FOREGROUND_A);
    }
}
