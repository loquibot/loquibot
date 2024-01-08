package com.alphalaneous.Components;

import com.alphalaneous.Components.ThemableJComponents.ThemeableJButton;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJLabel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Utilities.Fonts;
import com.alphalaneous.Interfaces.Function;
import com.alphalaneous.Utilities.GraphicsFunctions;
import net.miginfocom.swing.MigLayout;

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

    ThemeableJPanel containerPanel = new ThemeableJPanel();
    ThemeableJPanel loginContainerPanel = new ThemeableJPanel();

    public AccountPanel(String name, Function dropDownFunction){

        accountNameLabel = new ThemeableJLabel();
        accountNameLabel.setForeground("foreground");
        accountNameLabel.setFont(Fonts.getFont("Poppins-Regular").deriveFont(20f));
        accountTypeLabel = new ThemeableJLabel(name);
        accountTypeLabel.setForeground("foreground");
        accountTypeLabel.setFont(Fonts.getFont("Poppins-Regular").deriveFont(14f));
        accountImageLabel = new ThemeableJLabel();

        dropDownButton = new RoundedButton("\uF666 \uF666 \uF666");
        dropDownButton.setOpaque(false);
        dropDownButton.setFont(Fonts.getFont("Glyphs").deriveFont(6f));
        dropDownButton.setPreferredSize(new Dimension(30,30));
        dropDownButton.addActionListener(e -> dropDownFunction.run());

        setBackground("list-background-normal");
        setOpaque(false);
        setLayout(new GridLayout(0, 1));


        containerPanel.setOpaque(false);
        containerPanel.setLayout(new MigLayout());

        ThemeableJPanel iconPanel = new ThemeableJPanel();
        iconPanel.setOpaque(false);
        iconPanel.setLayout(new GridBagLayout());
        iconPanel.add(largeIcon, new GridBagConstraints());

        ThemeableJPanel contentPanel = new ThemeableJPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        contentPanel.add(accountNameLabel);
        contentPanel.add(accountTypeLabel);

        ThemeableJPanel dropdownPanel = new ThemeableJPanel();
        dropdownPanel.setOpaque(false);

        dropdownPanel.setLayout(new GridBagLayout());

        dropdownPanel.add(dropDownButton, new GridBagConstraints());

        containerPanel.add(iconPanel, "width 100px, height 100%");
        containerPanel.add(contentPanel, "width 100% - 200px");

        if(dropDownFunction != null) {
            containerPanel.add(dropdownPanel, "width 100px, height 100%");
        }

        loginContainerPanel.setOpaque(false);
        loginContainerPanel.setLayout(new GridBagLayout());


        //add(containerPanel);
        add(loginContainerPanel);

        containerPanel.setVisible(false);

        setPreferredSize(new Dimension(475,100));

    }

    public void setLoginButton(ThemeableJButton button){
        loginContainerPanel.removeAll();
        loginContainerPanel.add(button, new GridBagConstraints());
    }


    public void login(String accountName, ImageIcon accountImage, int xShift, int yShift){
        accountNameLabel.setText(accountName);
        largeIcon.setIcon(accountImage);
        loggedIn = true;

        loginContainerPanel.setVisible(false);
        containerPanel.setVisible(true);
        remove(loginContainerPanel);
        add(containerPanel);

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

        loginContainerPanel.setVisible(true);
        containerPanel.setVisible(false);
        remove(containerPanel);
        add(loginContainerPanel);

        revalidate();
    }

    public void login(String accountName, ImageIcon accountImage){
        login(accountName, accountImage, 0,0);
    }
    @Override
    public void paintComponent(Graphics g) {

        GraphicsFunctions.roundCorners(g, getBackground(), getSize());
        super.paintComponent(g);
    }
}
