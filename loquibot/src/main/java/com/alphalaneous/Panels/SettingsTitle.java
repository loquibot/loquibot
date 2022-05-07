package com.alphalaneous.Panels;

import com.alphalaneous.Components.LangLabel;
import com.alphalaneous.Defaults;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SettingsTitle extends JPanel {

    public static ArrayList<SettingsTitle> titleList = new ArrayList<>();

    private final LangLabel settingsTitleLabel;

    public SettingsTitle(String title){
        setLayout(null);
        setBackground(new Color(0,0,0,0));
        setOpaque(false);
        settingsTitleLabel = new LangLabel(title);
        settingsTitleLabel.setFont(Defaults.MAIN_FONT.deriveFont(24f));
        settingsTitleLabel.setForeground(Defaults.FOREGROUND_A);
        settingsTitleLabel.setBounds(25,20,500,50);
        setBounds(0,0, 500, 80);
        add(settingsTitleLabel);
        titleList.add(this);
    }
    public void refresh(){
        settingsTitleLabel.setForeground(Defaults.FOREGROUND_A);
    }
    public static void refreshAll(){
        for(SettingsTitle title : titleList){
            title.refresh();
        }
    }
}
