package com.alphalaneous.Swing.Components;

import com.alphalaneous.Interactive.Commands.CommandData;
import com.alphalaneous.Defaults;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class CommandListElement extends CurvedButton {

    private static final ArrayList<CommandListElement> commandListElements = new ArrayList<>();
    private final JLabel titleLabel = new JLabel();
    private final LangLabel levelLabel = new LangLabel("");

    public CommandListElement(CommandData commandData){
        super("");
        setLayout(null);
        setBackground(Defaults.COLOR2);

        titleLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        titleLabel.setForeground(Defaults.FOREGROUND_A);
        titleLabel.setText(commandData.getCommand());
        titleLabel.setBounds(10,2,410, 30);

        levelLabel.setFont(Defaults.MAIN_FONT.deriveFont(14f));
        levelLabel.setForeground(Defaults.FOREGROUND_A);
        levelLabel.setTextLang(commandData.getUserLevel());
        levelLabel.setBounds(330,2,410, 30);

        setUI(Defaults.settingsButtonUI);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addActionListener(e -> System.out.println("Show command editor"));


        add(titleLabel);
        add(levelLabel);
        setPreferredSize(new Dimension(410, 30));
        setMaximumSize(new Dimension(410, 30));
        commandListElements.add(this);
    }
    public static void refreshAll(){
        for(CommandListElement commandListElement : commandListElements){
            commandListElement.refresh();
        }
    }
    public void refresh(){
        setBackground(Defaults.COLOR2);
        titleLabel.setForeground(Defaults.FOREGROUND_A);
        levelLabel.setForeground(Defaults.FOREGROUND_A);
    }
}
