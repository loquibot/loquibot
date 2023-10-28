package com.alphalaneous.Components;

import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Utilities.GraphicsFunctions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;

public class ContextMenu extends ThemeableJPanel {

    private final GridBagConstraints gbc = new GridBagConstraints();

    private int height = 0;
    private int width = 175;
    public ContextMenu() {
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(5,5,0,5));
        addMouseListener(new MouseAdapter() {});
        setOpaque(false);
        setBackground("background");
        //setBackground(new Color(Defaults.COLOR6.getRed(), Defaults.COLOR6.getGreen(), Defaults.COLOR6.getBlue(), 200));

    }


    public void setWidth(int width){
        this.width = width;
    }

    public void addButton(ContextButton button){
        height += 37; //Brute forced the right value, no idea why it's 37
        setBounds(0,0,width,height+5);
        add(button, gbc);
    }

    @Override
    public void paintComponent(Graphics g) {
        GraphicsFunctions.roundCorners(g, getBackground(), getSize());
        super.paintComponent(g);
    }
}
