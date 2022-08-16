package com.alphalaneous.Swing.Components;

import com.alphalaneous.Utils.Defaults;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;

//JButton because for some reason you can click through the JPanel
public class ContextMenu extends JPanel {

    private final GridBagConstraints gbc = new GridBagConstraints();

    private int height = 0;

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
        setBackground(new Color(Defaults.COLOR6.getRed(), Defaults.COLOR6.getGreen(), Defaults.COLOR6.getBlue(), 200));

    }
    public void addButton(ContextButton button){
        height += 37; //Brute forced the right value, no idea why it's 37
        setBounds(0,0,175,height+5);
        add(button, gbc);

    }

    @Override
    protected void paintComponent(Graphics g) {
        GraphicsFunctions.roundCorners(g, getBackground(), getSize());
        super.paintComponent(g);
    }
}
