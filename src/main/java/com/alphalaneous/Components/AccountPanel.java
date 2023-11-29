package com.alphalaneous.Components;

import com.alphalaneous.Components.ThemableJComponents.ThemeableJLabel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Utilities.Fonts;
import com.alphalaneous.Interfaces.Function;
import com.alphalaneous.Utilities.GraphicsFunctions;

import javax.swing.*;
import java.awt.*;

public class AccountPanel extends ThemeableJPanel {

    ThemeableJLabel accountNameLabel;
    ThemeableJLabel accountTypeLabel;
    ThemeableJLabel accountImageLabel;
    RoundedButton dropDownButton;
    ThemeableJLabel icon = new ThemeableJLabel();
    ThemeableJLabel largeIcon = new ThemeableJLabel();
    boolean loggedIn = false;
    String service;
    public AccountPanel(String name, Function dropDownFunction){

        accountNameLabel = new ThemeableJLabel();
        accountNameLabel.setForeground("foreground");
        accountNameLabel.setFont(Fonts.getFont("Poppins-Regular").deriveFont(20f));
        accountTypeLabel = new ThemeableJLabel(name);
        accountTypeLabel.setForeground("foreground");
        accountTypeLabel.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));
        accountImageLabel = new ThemeableJLabel();

        dropDownButton = new RoundedButton("\uF666 \uF666 \uF666");

        setBackground("list-background-normal");
        setOpaque(false);

        add(largeIcon);
        add(accountNameLabel);
        add(dropDownButton);

        dropDownButton.setOpaque(false);
        dropDownButton.setFont(Fonts.getFont("Glyphs").deriveFont(6f));
        dropDownButton.setPreferredSize(new Dimension(30,30));
        dropDownButton.addActionListener(e -> dropDownFunction.run());
        setPreferredSize(new Dimension(475,100));

    }
    public void refreshInfo(String accountName, ImageIcon accountImage, int xShift, int yShift){
        accountNameLabel.setText(accountName);
        accountImageLabel.setIcon(accountImage);

        loggedIn = true;
        remove(largeIcon);
        add(icon);
        add(accountImageLabel);
        add(accountTypeLabel);
        revalidate();
    }

    public void setLargeIcon(ImageIcon icon, String text){
        largeIcon.setIcon(icon);
        accountNameLabel.setText(text);
        this.service = text;
    }

    public void hideRefreshButton(){
        dropDownButton.setVisible(false);
    }
    public void showRefreshButton(){
        dropDownButton.setVisible(true);
    }


    public void setIcon(ImageIcon icon){
        this.icon.setIcon(icon);
    }

    public void logout(){
        loggedIn = false;
        accountNameLabel.setText(service);
        remove(accountImageLabel);
        remove(accountTypeLabel);
        remove(icon);
        add(largeIcon);
        revalidate();
    }

    public void refreshInfo(String accountName, ImageIcon accountImage){
        refreshInfo(accountName, accountImage, 0,0);
    }
    @Override
    public void paintComponent(Graphics g) {

        GraphicsFunctions.roundCorners(g, getBackground(), getSize());
        super.paintComponent(g);
    }
}
