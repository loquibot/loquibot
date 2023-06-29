package com.alphalaneous.Windows;

import com.alphalaneous.Images.Assets;
import com.alphalaneous.Swing.Components.CurvedButton;
import com.alphalaneous.Swing.Components.FancyTextArea;
import com.alphalaneous.Swing.Components.GraphicsFunctions;
import com.alphalaneous.Utils.Defaults;
import com.alphalaneous.Utils.Utilities;
import org.apache.http.client.utils.URIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class KickLoginWindow {

    public static String createLoginWindow(){

        JFrame frame = new JFrame();

        frame.setSize(400,350);

        frame.setResizable(false);
        frame.setTitle("Connect Kick");
        frame.setIconImage(Assets.Kick.getImage());
        frame.setLocationRelativeTo(Window.getWindow());
        frame.setLayout(null);

        frame.getContentPane().setBackground(new Color(36, 39, 44, 255));

        JLabel kickIcon = new JLabel();
        kickIcon.setIcon(Assets.KickText);
        kickIcon.setBounds(20,10,200,50);

        frame.add(kickIcon);

        JLabel usernameText = new JLabel("Username:");
        usernameText.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        usernameText.setBounds(18, 70, 350, 30);
        usernameText.setForeground(Color.WHITE);

        frame.add(usernameText);

        FancyTextArea usernameField = getFilteredField();
        usernameField.setPreferredSize(new Dimension(350, 40));
        usernameField.setFont(Defaults.MAIN_FONT.deriveFont(20f));
        usernameField.setBackground(new Color(70, 78, 83, 255));
        usernameField.setForeground(Color.WHITE);
        usernameField.setCustomAccent(new Color(82, 250, 24, 255));
        //usernameField.setBorder(new EmptyBorder(0,0,0,0));

        usernameField.setBounds(15,
                100,
                usernameField.getPreferredSize().width,
                usernameField.getPreferredSize().height);

        frame.add(usernameField);

        JLabel noticeText = new JLabel("<html>Type !connect in your chat after connecting to allow loquibot to send messages.</html>");
        noticeText.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        noticeText.setBounds(18,  180, 350, 50);
        noticeText.setForeground(Color.WHITE);

        frame.add(noticeText);


        final String[] input = {null};

        CurvedButton submitButton = new CurvedButton("Connect");

        //submitButton.setUI(Defaults.settingsButtonUI);

        submitButton.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        submitButton.setBorder(BorderFactory.createEmptyBorder());
        submitButton.setBackground(new Color(82, 250, 24, 255));
        submitButton.setForeground(Color.BLACK);
        submitButton.setBounds(15, 250, 350, 40);
        submitButton.setFocusPainted(false);
        submitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submitButton.addActionListener(e -> {

            if(!usernameField.getText().trim().equalsIgnoreCase("")) {
                input[0] = usernameField.getText();
                frame.setVisible(false);
            }
        });

        frame.add(submitButton);

        frame.setVisible(true);


        while (frame.isVisible()) Utilities.sleep(100);



        return input[0];
    }

    private static FancyTextArea getFilteredField() {
        FancyTextArea field = new FancyTextArea(false, false, false, 30);
        AbstractDocument doc = (AbstractDocument) field.getDocument();

        String regex = "[^0-9a-zA-Z\\-_]+";

        doc.setDocumentFilter(new DocumentFilter() {
            public void replace(FilterBypass fb, int offs, int length,
                                String str, AttributeSet a) throws BadLocationException {
                super.replace(fb, offs, length,
                        str.replaceAll(regex, ""), a);
            }

            public void insertString(FilterBypass fb, int offs, String str,
                                     AttributeSet a) throws BadLocationException {
                super.insertString(fb, offs,
                        str.replaceAll(regex, ""), a);
            }
        });
        return field;
    }

}
