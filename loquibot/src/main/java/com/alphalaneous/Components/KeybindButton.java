package com.alphalaneous.Components;

import com.alphalaneous.Defaults;
import com.alphalaneous.Settings;
import org.jnativehook.keyboard.SwingKeyAdapter;

import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeybindButton extends JPanel {

    private final FancyTextArea input = new FancyTextArea(false, false);
    private final LangLabel keybindLabel;

    private static boolean isInFocus = false;

    public KeybindButton(String text, String setting){
        setLayout(new BorderLayout());
        setOpaque(false);
        setBackground(new Color(0,0,0,0));
        DefaultStyledDocument doc = new DefaultStyledDocument();

        keybindLabel = new LangLabel(text);
        input.setEditable(false);
        input.getTextInput().addKeyListener(new SwingKeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 8 || e.getKeyCode() == 16 || e.getKeyCode() == 17 || e.getKeyCode() == 18 || e.getKeyCode() == 10) {
                    input.setText("");
                    Settings.writeSettings(setting, "-1");
                } else {
                    input.setText(KeyEvent.getKeyText(e.getKeyCode()));
                    Settings.writeSettings(setting, String.valueOf(e.getKeyCode()));
                }
                requestFocusInWindow();
            }
        });
        input.getTextInput().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                isInFocus = true;
            }

            @Override
            public void focusLost(FocusEvent e) {
                isInFocus = false;
            }
        });

        input.setPreferredSize(new Dimension(100,32));
        input.setDocument(doc);

        int keycode = Settings.getSettings(setting).asInteger();
        if(!(keycode == 0 || keycode == -1)){
            input.setText(KeyEvent.getKeyText(keycode));
        }

        keybindLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        keybindLabel.setBounds(0, 3, 500, 30);
        keybindLabel.setForeground(Defaults.FOREGROUND_A);

        add(input, BorderLayout.EAST);
        add(keybindLabel, BorderLayout.WEST);

        input.setBounds(400, 1, 100, 32);
        keybindLabel.setBounds(0, 3, 500, 30);

    }
    public void resizeButton(int width){
        setBounds(30,getY(),width-350, getHeight());
        input.setBounds(width-400, 1, 100, 32);
    }

    public void refreshUI(){
        //setBackground(Defaults.COLOR3);
        keybindLabel.setForeground(Defaults.FOREGROUND_A);
    }
    public static boolean getInFocus(){
        return isInFocus;
    }

}
