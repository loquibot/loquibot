package com.alphalaneous.Components;

import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Utilities.GraphicsFunctions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;

public class ContextMenu extends ThemeableJPanel {

    private final GridBagConstraints gbc = new GridBagConstraints();

    private double height = 0;
    private int width = 175;
    public ContextMenu() {
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(2, 0, 2, 0);
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
        addMouseListener(new MouseAdapter() {});
        setOpaque(false);
        setBackground("background-intense");
        //setBackground(new Color(Defaults.COLOR6.getRed(), Defaults.COLOR6.getGreen(), Defaults.COLOR6.getBlue(), 200));

    }


    public void setWidth(int width){
        this.width = width;
    }

    public void addButton(ContextButton button){
        height += 30;
        setBounds(0,0,width, (int) (height+4));
        add(button, gbc);
    }

    @Override
    public void paintComponent(Graphics g) {
        GraphicsFunctions.roundCorners(g, getBackground(), getSize(), 12);
        super.paintComponent(g);
    }
}
